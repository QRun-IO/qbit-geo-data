# Geo Data Tools

Data acquisition utilities for fetching and updating geographic reference data.

## Usage

```bash
mvn exec:java                                      # Full refresh
mvn exec:java -Dexec.args="--min-city-population=15000"
mvn exec:java -Dexec.args="--countries=US,CA,MX"
mvn exec:java -Dexec.args="--states-only"
mvn exec:java -Dexec.args="--help"
```

## Data Source

[dr5hn/countries-states-cities-database](https://github.com/dr5hn/countries-states-cities-database) (ODbL license)

Output written to `../qbit-geo-data-core/src/main/resources/data/`.
