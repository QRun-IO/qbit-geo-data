/*******************************************************************************
 ** Sync step for loading and updating geographic reference data.
 **
 ** Performs natural key upsert:
 ** - Country: by alpha2Code
 ** - StateProvince: by countryId + code
 ** - City: by stateProvinceId + name
 *******************************************************************************/
package com.kingsrook.qbits.geodata.sync;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.kingsrook.qqq.backend.core.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.actions.processes.RunBackendStepOutput;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.actions.tables.UpdateAction;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryOutput;
import com.kingsrook.qqq.backend.core.model.actions.tables.update.UpdateInput;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.processes.implementations.etl.streamedwithfrontend.AbstractTransformStep;
import org.json.JSONArray;
import org.json.JSONObject;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


public class GeoDataSyncStep extends AbstractTransformStep
{
   private static final QLogger LOG = QLogger.getLogger(GeoDataSyncStep.class);



   /*******************************************************************************
    ** Run the sync step.
    *******************************************************************************/
   @Override
   public void runOnePage(RunBackendStepInput input, RunBackendStepOutput output) throws QException
   {
      String tableName = input.getValueString("tableName");
      String resourcePath = input.getValueString("resourcePath");
      List<String> naturalKeyFields = parseNaturalKeyFields(input.getValueString("naturalKeyFields"));

      LOG.info("Starting geo data sync",
         logPair("table", tableName),
         logPair("resource", resourcePath),
         logPair("naturalKey", naturalKeyFields));

      /////////////////////////////////////////////////////////////////////////
      // 1. Load source data from classpath JSON                             //
      /////////////////////////////////////////////////////////////////////////
      List<QRecord> sourceRecords = loadJsonData(resourcePath);
      LOG.info("Loaded source data", logPair("count", sourceRecords.size()));

      /////////////////////////////////////////////////////////////////////////
      // 2. Query existing records by natural key                            //
      /////////////////////////////////////////////////////////////////////////
      Map<String, QRecord> existingByKey = queryExisting(tableName, naturalKeyFields);
      LOG.info("Queried existing records", logPair("count", existingByKey.size()));

      /////////////////////////////////////////////////////////////////////////
      // 3. Categorize: insert new, update changed, deactivate removed       //
      /////////////////////////////////////////////////////////////////////////
      List<QRecord> toInsert = new ArrayList<>();
      List<QRecord> toUpdate = new ArrayList<>();
      List<QRecord> toDeactivate = new ArrayList<>();

      for(QRecord source : sourceRecords)
      {
         String key = buildNaturalKey(source, naturalKeyFields);
         QRecord existing = existingByKey.remove(key);

         if(existing == null)
         {
            source.setValue("isActive", true);
            toInsert.add(source);
         }
         else if(hasChanges(source, existing))
         {
            source.setValue("id", existing.getValue("id"));
            source.setValue("isActive", true);
            toUpdate.add(source);
         }
      }

      //////////////////////////////////////////////////////////////////////////
      // Records remaining in existingByKey are no longer in source          //
      //////////////////////////////////////////////////////////////////////////
      for(QRecord orphan : existingByKey.values())
      {
         if(Boolean.TRUE.equals(orphan.getValueBoolean("isActive")))
         {
            orphan.setValue("isActive", false);
            toDeactivate.add(orphan);
         }
      }

      /////////////////////////////////////////////////////////////////////////
      // 4. Execute operations                                               //
      /////////////////////////////////////////////////////////////////////////
      if(!toInsert.isEmpty())
      {
         insertRecords(tableName, toInsert);
      }
      if(!toUpdate.isEmpty())
      {
         updateRecords(tableName, toUpdate);
      }
      if(!toDeactivate.isEmpty())
      {
         updateRecords(tableName, toDeactivate);
      }

      /////////////////////////////////////////////////////////////////////////
      // 5. Log summary                                                      //
      /////////////////////////////////////////////////////////////////////////
      LOG.info("Geo data sync complete",
         logPair("table", tableName),
         logPair("inserted", toInsert.size()),
         logPair("updated", toUpdate.size()),
         logPair("deactivated", toDeactivate.size()));
   }



   /*******************************************************************************
    ** Parse comma-separated natural key fields.
    *******************************************************************************/
   private List<String> parseNaturalKeyFields(String naturalKeyFields)
   {
      List<String> fields = new ArrayList<>();
      if(naturalKeyFields != null)
      {
         for(String field : naturalKeyFields.split(","))
         {
            fields.add(field.trim());
         }
      }
      return fields;
   }



   /*******************************************************************************
    ** Build a composite natural key string from record values.
    *******************************************************************************/
   private String buildNaturalKey(QRecord record, List<String> keyFields)
   {
      StringBuilder key = new StringBuilder();
      for(String field : keyFields)
      {
         if(key.length() > 0)
         {
            key.append("|");
         }
         Object value = record.getValue(field);
         key.append(value != null ? value.toString() : "");
      }
      return key.toString();
   }



   /*******************************************************************************
    ** Load records from a JSON resource file on the classpath.
    *******************************************************************************/
   private List<QRecord> loadJsonData(String resourcePath) throws QException
   {
      try(InputStream is = getClass().getResourceAsStream(resourcePath))
      {
         if(is == null)
         {
            throw new QException("Resource not found: " + resourcePath);
         }

         String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
         JSONArray array = new JSONArray(json);
         List<QRecord> records = new ArrayList<>();

         for(int i = 0; i < array.length(); i++)
         {
            JSONObject obj = array.getJSONObject(i);
            QRecord record = new QRecord();
            for(String key : obj.keySet())
            {
               Object value = obj.get(key);
               if(!JSONObject.NULL.equals(value))
               {
                  record.setValue(key, value);
               }
            }
            records.add(record);
         }

         return records;
      }
      catch(Exception e)
      {
         throw new QException("Error loading JSON data from " + resourcePath, e);
      }
   }



   /*******************************************************************************
    ** Query existing records and index by natural key.
    *******************************************************************************/
   private Map<String, QRecord> queryExisting(String tableName, List<String> naturalKeyFields) throws QException
   {
      QueryInput queryInput = new QueryInput();
      queryInput.setTableName(tableName);
      queryInput.setFilter(new QQueryFilter());

      QueryOutput queryOutput = new QueryAction().execute(queryInput);

      Map<String, QRecord> byKey = new HashMap<>();
      for(QRecord record : queryOutput.getRecords())
      {
         String key = buildNaturalKey(record, naturalKeyFields);
         byKey.put(key, record);
      }
      return byKey;
   }



   /*******************************************************************************
    ** Check if source record has changes compared to existing.
    *******************************************************************************/
   private boolean hasChanges(QRecord source, QRecord existing)
   {
      for(String fieldName : source.getValues().keySet())
      {
         if(fieldName.equals("id") || fieldName.equals("createDate") || fieldName.equals("modifyDate"))
         {
            continue;
         }
         Object sourceValue = source.getValue(fieldName);
         Object existingValue = existing.getValue(fieldName);
         if(!Objects.equals(sourceValue, existingValue))
         {
            return true;
         }
      }
      return false;
   }



   /*******************************************************************************
    ** Insert new records.
    *******************************************************************************/
   private void insertRecords(String tableName, List<QRecord> records) throws QException
   {
      InsertInput insertInput = new InsertInput();
      insertInput.setTableName(tableName);
      insertInput.setRecords(records);
      new InsertAction().execute(insertInput);
   }



   /*******************************************************************************
    ** Update existing records.
    *******************************************************************************/
   private void updateRecords(String tableName, List<QRecord> records) throws QException
   {
      UpdateInput updateInput = new UpdateInput();
      updateInput.setTableName(tableName);
      updateInput.setRecords(records);
      new UpdateAction().execute(updateInput);
   }
}
