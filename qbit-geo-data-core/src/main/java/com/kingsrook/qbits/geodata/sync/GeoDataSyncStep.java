/*******************************************************************************
 ** Sync step for loading and updating geographic reference data.
 **
 ** Syncs all three entity types (Country, StateProvince, City) from bundled
 ** JSON files into database tables. Performs natural key upsert:
 ** - Country: by alpha2Code
 ** - StateProvince: by countryAlpha2 + code
 ** - City: by countryAlpha2 + stateCode + name
 *******************************************************************************/
package com.kingsrook.qbits.geodata.sync;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.kingsrook.qqq.backend.core.actions.processes.BackendStep;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
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
import org.json.JSONArray;
import org.json.JSONObject;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


public class GeoDataSyncStep implements BackendStep
{
   private static final QLogger LOG = QLogger.getLogger(GeoDataSyncStep.class);

   public static final String FIELD_TABLE_NAME_PREFIX = "tableNamePrefix";



   /*******************************************************************************
    ** Run the sync step - syncs all three entity types.
    *******************************************************************************/
   @Override
   public void run(RunBackendStepInput input, RunBackendStepOutput output) throws QException
   {
      String prefix = input.getValueString(FIELD_TABLE_NAME_PREFIX);
      if(prefix == null || prefix.isEmpty())
      {
         throw new QException("tableNamePrefix is required");
      }

      LOG.info("Starting geo data sync", logPair("prefix", prefix));

      //////////////////////////////////////////////////////////////////////////
      // Sync in order: countries first (no dependencies), then states, then //
      // cities (which may reference states)                                  //
      //////////////////////////////////////////////////////////////////////////
      int countriesInserted = syncTable(prefix + "_country", "/data/countries.json", List.of("alpha2Code"));
      int statesInserted = syncTable(prefix + "_stateProvince", "/data/states.json", List.of("countryAlpha2", "code"));
      int citiesInserted = syncTable(prefix + "_city", "/data/cities.json", List.of("countryAlpha2", "stateCode", "name"));

      LOG.info("Geo data sync complete",
         logPair("prefix", prefix),
         logPair("countries", countriesInserted),
         logPair("states", statesInserted),
         logPair("cities", citiesInserted));

      output.addValue("countriesSynced", countriesInserted);
      output.addValue("statesSynced", statesInserted);
      output.addValue("citiesSynced", citiesInserted);
   }



   /*******************************************************************************
    ** Sync a single table from JSON resource.
    ** Returns count of records processed.
    *******************************************************************************/
   private int syncTable(String tableName, String resourcePath, List<String> naturalKeyFields) throws QException
   {
      LOG.info("Syncing table",
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
      LOG.info("Table sync complete",
         logPair("table", tableName),
         logPair("inserted", toInsert.size()),
         logPair("updated", toUpdate.size()),
         logPair("deactivated", toDeactivate.size()));

      return sourceRecords.size();
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
