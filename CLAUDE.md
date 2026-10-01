# qbit-geo-data

## Knowledge base

Reviewed knowledge for this repo and the wider QQQ platform lives in the second-brain vault:

- Platform hub: `R:/Git.Local/KofTwentyTwo/second-brain/knowledge/qqq/qqq-hub.md`
- This repo's dossier: `R:/Git.Local/KofTwentyTwo/second-brain/knowledge/qqq/repos/qbit-geo-data.md`
  (reviewed commit `f7889fdf7495` on `main`, 2026-07-04)
- QBit mechanics refresher: `R:/Git.Local/KofTwentyTwo/second-brain/knowledge/qqq/architecture/metadata-model.md`

Key cautions from the review: entities produce PossibleValueSources only (`produceTableMetaData` defaults false — no tables are registered); the sync step's natural keys (`countryAlpha2`/`stateCode`) don't exist in the schema; the `geoDataSync` process name collides on multi-instance registration; current first-party pom/README declarations align with Apache-2.0 LICENSE/NOTICE; main and develop have diverged.
