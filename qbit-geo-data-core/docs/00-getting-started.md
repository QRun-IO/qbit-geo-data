# Getting Started

## Prerequisites

- Java 17+
- Maven 3.8+
- QQQ 0.23.0+

## Creating Your Data QBit

1. Click "Use this template" on GitHub
2. Clone your new repository
3. Run the customization script:
   ```bash
   python scripts/customize_template.py
   ```
4. Enter your QBit name when prompted

## Project Structure

```
src/main/java/com/kingsrook/qbits/yourdata/
├── YourDataQBitConfig.java
├── YourDataQBitProducer.java
├── model/
│   └── YourEntity.java
├── sync/
│   └── YourDataSyncProcess.java
└── liquibase/
    └── YourLiquibaseGenerator.java
```

## Using Your QBit

```java
new YourDataQBitProducer()
   .withConfig(new YourDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("myprefix"))
   .produce(qInstance, "my-data");
```

## Next Steps

- Read [Data QBit Architecture](01-data-qbit-architecture.md)
- Implement your sync process
- Generate Liquibase changelog
