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

```java
new GeoDataQBitProducer()
   .withConfig(new GeoDataQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("shipping"))
   .produce(qInstance, "shipping-geo");
```

This adds Country, StateProvince, and City tables with the specified prefix (e.g., `shipping_country`). Run the sync process to populate data from bundled JSON files.

## Modules

- **qbit-geo-data-core** - QBit with entities and sync process
- **tools** - Data acquisition utilities for fetching updates

## Data Acquisition

From the `tools/` directory:

```bash
mvn exec:java                                      # Full refresh
mvn exec:java -Dexec.args="--min-city-population=15000"
mvn exec:java -Dexec.args="--countries=US,CA,MX"
mvn exec:java -Dexec.args="--help"
```

Data source: [dr5hn/countries-states-cities-database](https://github.com/dr5hn/countries-states-cities-database) (ODbL license)

## License

AGPL-3.0 - See [LICENSE](LICENSE)
