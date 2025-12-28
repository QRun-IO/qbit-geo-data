# Liquibase Generation

Data QBits ship a template changelog that host apps use to generate final changelogs with their configured prefix.

## Template Structure

The template uses `${prefix}` tokens and section markers:

```xml
<!-- SECTION: exampleEntity -->
<changeSet id="${prefix}-create-example-entity-v1" author="example-data-qbit">
   <createTable tableName="${prefix}_example_entity">
      <column name="id" type="INT" autoIncrement="true">
         <constraints primaryKey="true"/>
      </column>
      <column name="code" type="VARCHAR(50)">
         <constraints nullable="false" unique="true"/>
      </column>
      ...
   </createTable>
</changeSet>
<!-- END SECTION: exampleEntity -->
```

## Generator Usage

```java
// Generate changelog during build or setup
ExampleLiquibaseGenerator.generate(
   new ExampleDataQBitConfig()
      .withTableNamePrefix("shipping")
      .withEnableExampleChildEntity(true),
   Path.of("src/main/resources/db/generated/shipping-example-changelog.xml"));
```

## Generated Output

With prefix "shipping":

```xml
<changeSet id="shipping-create-example-entity-v1" author="example-data-qbit">
   <createTable tableName="shipping_example_entity">
      ...
   </createTable>
</changeSet>
```

## Host App Integration

```xml
<!-- Host app's master-changelog.xml -->
<databaseChangeLog>
   <include file="db/app-changelog.xml"/>
   <include file="db/generated/shipping-example-changelog.xml"/>
   <include file="db/generated/billing-example-changelog.xml"/>
</databaseChangeLog>
```

## Section Removal

Disabled tables are removed by their section markers:

```java
// With enableExampleChildEntity=false
// The <!-- SECTION: exampleChildEntity --> block is removed
```

## Changeset ID Convention

Use prefix in changeset IDs to avoid collisions across instances:

```
${prefix}-create-{table}-v{version}
```

Examples:
- `shipping-create-example-entity-v1`
- `billing-create-example-entity-v1`
