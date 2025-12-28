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
new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("shipping"))
   .produce(qInstance, "shipping-geo");
```

This adds Country, StateProvince, and City tables with the specified prefix (e.g., `shipping_country`). Run the sync process to populate data from bundled JSON files.

### Querying Data

```java
// Get a country by alpha-2 code
Country usa = new GetAction().executeForRecord(new GetInput(Country.TABLE_NAME)
   .withUniqueKey(Map.of("alpha2Code", "US")), Country.class);

// Query states for a country
List<StateProvince> states = new QueryAction().execute(new QueryInput(StateProvince.TABLE_NAME)
   .withFilter(new QQueryFilter(new QFilterCriteria("countryId", EQUALS, usa.getId()))))
   .getRecordEntities(StateProvince.class);

// Query cities with population filter
List<City> cities = new QueryAction().execute(new QueryInput(City.TABLE_NAME)
   .withFilter(new QQueryFilter()
      .withCriteria(new QFilterCriteria("stateProvinceId", EQUALS, state.getId()))
      .withCriteria(new QFilterCriteria("population", GREATER_THAN, 100000))))
   .getRecordEntities(City.class);
```

### Using as PossibleValueSource

The entities are registered as PossibleValueSources for use in dropdowns:

```java
new QFieldMetaData("countryId", QFieldType.INTEGER)
   .withPossibleValueSourceName("country")
```

## License

AGPL-3.0 - See [LICENSE](LICENSE)
