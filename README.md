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

## Using this QBit

### Registration

```java
import com.kingsrook.qbits.geodata.GeoDataQBitConfig;
import com.kingsrook.qbits.geodata.GeoDataQBitProducer;

new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("shipping"))
   .produce(qInstance, "shipping-geo");
```

This adds Country, StateProvince, and City tables with the specified prefix (e.g., `shipping_country`). Run the sync process to populate data from bundled JSON files.

### Querying Data

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

### Using as PossibleValueSource

The entities are registered as PossibleValueSources for use in dropdowns:

```java
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;

new QFieldMetaData("countryId", QFieldType.INTEGER)
   .withPossibleValueSourceName("shipping_country")
```

## License

AGPL-3.0 - See [LICENSE](LICENSE)
