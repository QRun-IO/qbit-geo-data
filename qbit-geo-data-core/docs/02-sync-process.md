# Sync Process Pattern

Data QBits use a sync process to load and update reference data using natural key upsert.

## Natural Key Design

Each table defines fields that uniquely identify records for upsert matching:

| Table | Natural Key | Example |
|-------|-------------|---------|
| exampleEntity | `code` | "US", "CA", "MX" |
| exampleChildEntity | `exampleEntityId` + `code` | (1, "CA"), (1, "TX") |

## Sync Algorithm

```
Source Data (JSON) --> Sync Process --> Database Tables
                           |
                           v
                    [Compare by natural key]
                           |
         +-----------------+------------------+
         |                 |                  |
      Insert           Update            Deactivate
    (new keys)    (changed values)    (removed keys)
```

## Implementation

```java
public class ExampleDataSyncStep extends AbstractTransformStep
{
   @Override
   public void runOnePage(RunBackendStepInput input, RunBackendStepOutput output)
   {
      // 1. Load source data from classpath JSON
      List<QRecord> sourceRecords = loadJsonData(resourcePath);

      // 2. Query existing by natural key
      Map<String, QRecord> existingByKey = queryExisting(tableName, naturalKeyField);

      // 3. Categorize records
      for(QRecord source : sourceRecords)
      {
         String key = source.getValueString(naturalKeyField);
         QRecord existing = existingByKey.remove(key);

         if(existing == null)
            toInsert.add(source);           // New record
         else if(hasChanges(source, existing))
            toUpdate.add(source);           // Changed record
      }

      // 4. Remaining in map - no longer in source
      for(QRecord orphan : existingByKey.values())
      {
         orphan.setValue("isActive", false);
         toDeactivate.add(orphan);          // Soft delete
      }

      // 5. Execute operations
      insertRecords(toInsert);
      updateRecords(toUpdate);
      updateRecords(toDeactivate);
   }
}
```

## Data File Format

Reference data ships as JSON in `src/main/resources/data/`:

```json
[
   {
      "code": "US",
      "name": "United States",
      "description": "United States of America"
   },
   {
      "code": "CA",
      "name": "Canada"
   }
]
```

## Best Practices

1. **Always use natural keys** - Enables upsert without duplicates
2. **Include `isActive` field** - Soft deletes during sync
3. **Version data files** - For reproducible syncs
4. **Log sync summary** - Inserted, updated, deactivated counts
