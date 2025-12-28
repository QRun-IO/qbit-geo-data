/*******************************************************************************
 ** Sync step for loading and updating example reference data.
 **
 ** This step demonstrates the natural key upsert pattern:
 ** 1. Load source data from classpath JSON
 ** 2. Query existing records by natural key
 ** 3. Categorize: insert new, update changed, deactivate removed
 ** 4. Execute operations and log summary
 *******************************************************************************/
package com.kingsrook.qbits.example.sync;


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
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
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


public class ExampleDataSyncStep extends AbstractTransformStep
{
   private static final QLogger LOG = QLogger.getLogger(ExampleDataSyncStep.class);



   /*******************************************************************************
    ** Run the sync step.
    *******************************************************************************/
   @Override
   public void runOnePage(RunBackendStepInput input, RunBackendStepOutput output) throws QException
   {
      String tableName = input.getValueString("tableName");
      String naturalKeyField = input.getValueString("naturalKeyField");
      String dataResourcePath = input.getValueString("dataResourcePath");

      /////////////////////////////////////////////////////////////////////////
      // 1. Load source data from classpath JSON                             //
      /////////////////////////////////////////////////////////////////////////
      List<QRecord> sourceRecords = loadJsonData(dataResourcePath);
      LOG.info("Loaded source data", logPair("count", sourceRecords.size()), logPair("resource", dataResourcePath));

      /////////////////////////////////////////////////////////////////////////
      // 2. Query existing records by natural key                            //
      /////////////////////////////////////////////////////////////////////////
      Map<String, QRecord> existingByKey = queryExisting(input, tableName, naturalKeyField);
      LOG.info("Queried existing records", logPair("count", existingByKey.size()), logPair("table", tableName));

      /////////////////////////////////////////////////////////////////////////
      // 3. Categorize: insert new, update changed, deactivate removed       //
      /////////////////////////////////////////////////////////////////////////
      List<QRecord> toInsert = new ArrayList<>();
      List<QRecord> toUpdate = new ArrayList<>();
      List<QRecord> toDeactivate = new ArrayList<>();

      for(QRecord source : sourceRecords)
      {
         String key = source.getValueString(naturalKeyField);
         QRecord existing = existingByKey.remove(key);

         if(existing == null)
         {
            toInsert.add(source);
         }
         else if(hasChanges(source, existing))
         {
            source.setValue("id", existing.getValue("id"));
            toUpdate.add(source);
         }
      }

      //////////////////////////////////////////////////////////////////////////
      // Records remaining in existingByKey are no longer in source - deactivate
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
      insertRecords(input, tableName, toInsert);
      updateRecords(input, tableName, toUpdate);
      updateRecords(input, tableName, toDeactivate);

      /////////////////////////////////////////////////////////////////////////
      // 5. Log summary                                                      //
      /////////////////////////////////////////////////////////////////////////
      LOG.info("Sync complete",
         logPair("table", tableName),
         logPair("inserted", toInsert.size()),
         logPair("updated", toUpdate.size()),
         logPair("deactivated", toDeactivate.size()));
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
               record.setValue(key, obj.get(key));
            }
            record.setValue("isActive", true);
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
   private Map<String, QRecord> queryExisting(RunBackendStepInput input, String tableName, String naturalKeyField) throws QException
   {
      QueryInput queryInput = new QueryInput();
      queryInput.setTableName(tableName);
      queryInput.setFilter(new QQueryFilter());

      QueryOutput queryOutput = new QueryAction().execute(queryInput);

      Map<String, QRecord> byKey = new HashMap<>();
      for(QRecord record : queryOutput.getRecords())
      {
         String key = record.getValueString(naturalKeyField);
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
         if(fieldName.equals("id"))
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
   private void insertRecords(RunBackendStepInput input, String tableName, List<QRecord> records) throws QException
   {
      if(records.isEmpty())
      {
         return;
      }

      InsertInput insertInput = new InsertInput();
      insertInput.setTableName(tableName);
      insertInput.setRecords(records);
      new InsertAction().execute(insertInput);
   }



   /*******************************************************************************
    ** Update existing records.
    *******************************************************************************/
   private void updateRecords(RunBackendStepInput input, String tableName, List<QRecord> records) throws QException
   {
      if(records.isEmpty())
      {
         return;
      }

      UpdateInput updateInput = new UpdateInput();
      updateInput.setTableName(tableName);
      updateInput.setRecords(records);
      new UpdateAction().execute(updateInput);
   }
}
