# QBit Template: Data

Template repository for creating QQQ Data QBits - reference data with table prefixing, sync processes, and Liquibase generation.

## What is a Data QBit?

Data QBits provide reusable reference data sets that applications consume. They support:
- **Table prefixing** for multi-instance deployment
- **Sync processes** with natural key upsert
- **Liquibase generation** for schema management
- **PossibleValueSources** for dropdown lookups

## Quick Start

1. Click "Use this template" to create your QBit
2. Run `scripts/customize_template.py` to rename the example
3. Define your entity classes
4. Add reference data JSON files
5. Implement sync process
6. Generate Liquibase changelog

## Structure

```
src/main/java/com/kingsrook/qbits/example/
├── ExampleDataQBitConfig.java       # Extends AbstractDataQBitConfig
├── ExampleDataQBitProducer.java     # Extends AbstractDataQBitProducer
├── model/
│   ├── ExampleEntity.java
│   └── ExampleChildEntity.java
├── sync/
│   └── ExampleDataSyncProcess.java
└── liquibase/
    └── ExampleLiquibaseGenerator.java
```

## Key Characteristics

- **Table prefixing** - `shipping_country`, `billing_country`
- **Multi-instance** - Same QBit deployed multiple times
- **Sync process** - Upsert by natural key
- **Liquibase generator** - Template-based, prefix-aware

## Multi-Instance Usage

```java
// Shipping addresses
new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("shipping"))
   .produce(qInstance, "shipping-geo");

// Billing addresses
new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("billing"))
   .produce(qInstance, "billing-geo");
```

## Documentation

- [Getting Started](docs/00-getting-started.md)
- [Data QBit Architecture](docs/01-data-qbit-architecture.md)
- [Sync Process](docs/02-sync-process.md)
- [Liquibase Generation](docs/03-liquibase-generation.md)

## License

AGPL-3.0 - See [LICENSE](LICENSE)
