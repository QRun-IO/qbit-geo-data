# QBit: Geo Data

[![Alpha](https://img.shields.io/badge/status-alpha-orange)]()

Geographic reference data QBit providing countries, states/provinces, and cities for QQQ applications.

## Quick Start

Add the dependency to your `pom.xml`:

```xml
<dependency>
   <groupId>com.kingsrook.qbits</groupId>
   <artifactId>qbit-geo-data-core</artifactId>
   <version>${qbit-geo-data.version}</version>
</dependency>
```

## What You Get

This QBit provides three entities with pre-populated reference data:

| Entity | Fields | Records |
|--------|--------|---------|
| **Country** | alpha2Code, alpha3Code, numericCode, name, officialName | 250 |
| **StateProvince** | countryId, code, name, subdivisionType | 5,296 |
| **City** | countryId, stateProvinceId, name, population, latitude, longitude | 32,000+ |

## Using this QBit

### Step 1: Register the QBit

In your application's metadata setup, register the QBit with a table prefix:

```java
import com.kingsrook.qbits.geodata.GeoDataQBitConfig;
import com.kingsrook.qbits.geodata.GeoDataQBitProducer;

new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("shipping"))
   .produce(qInstance, "shipping-geo");
```

This creates three tables in your database:
- `shipping_country`
- `shipping_stateProvince`
- `shipping_city`

### Step 2: Run the Sync Process

The QBit includes a sync process that populates tables from bundled JSON data. Run it once after creating your tables, or schedule it to pick up updates.

### Step 3: Query the Data

Use standard QQQ actions with the **prefixed table names** and the entity classes:

```java
import com.kingsrook.qqq.backend.core.actions.tables.GetAction;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.model.actions.tables.get.GetInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterCriteria;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QueryInput;
import com.kingsrook.qbits.geodata.model.City;
import com.kingsrook.qbits.geodata.model.Country;
import com.kingsrook.qbits.geodata.model.StateProvince;

import static com.kingsrook.qqq.backend.core.model.actions.tables.query.QCriteriaOperator.*;

// Get a country by alpha-2 code
Country usa = new GetAction().executeForRecord(new GetInput("shipping_country")
   .withUniqueKey(Map.of("alpha2Code", "US")), Country.class);

// Query states for a country
List<StateProvince> states = new QueryAction().execute(new QueryInput("shipping_stateProvince")
   .withFilter(new QQueryFilter(new QFilterCriteria("countryId", EQUALS, usa.getId()))))
   .getRecordEntities(StateProvince.class);

// Query cities with population filter
List<City> cities = new QueryAction().execute(new QueryInput("shipping_city")
   .withFilter(new QQueryFilter()
      .withCriteria(new QFilterCriteria("stateProvinceId", EQUALS, state.getId()))
      .withCriteria(new QFilterCriteria("population", GREATER_THAN, 100000))))
   .getRecordEntities(City.class);
```

**Key concept:** The table name (e.g., `"shipping_country"`) includes your prefix. The entity class (`Country.class`) is always the same regardless of prefix.

### Step 4: Use in Dropdowns

Each entity is registered as a PossibleValueSource for use in form dropdowns:

```java
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;

new QFieldMetaData("countryId", QFieldType.INTEGER)
   .withPossibleValueSourceName("shipping_country")
```

## Multiple Instances

Need geo data for different contexts? Register the QBit multiple times with different prefixes:

```java
// For shipping addresses
new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("shipping"))
   .produce(qInstance, "shipping-geo");

// For billing addresses
new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("billing"))
   .produce(qInstance, "billing-geo");
```

This creates two independent sets of tables:
- `shipping_country`, `shipping_stateProvince`, `shipping_city`
- `billing_country`, `billing_stateProvince`, `billing_city`

Query each using its prefixed table name, same entity classes.

## License

AGPL-3.0 - See [LICENSE](LICENSE)
