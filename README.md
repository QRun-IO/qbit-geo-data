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

## License

AGPL-3.0 - See [LICENSE](LICENSE)
