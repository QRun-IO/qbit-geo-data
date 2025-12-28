# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Multi-module structure (qbit-geo-data-core + tools)
- Country, StateProvince, City entities with sync process
- Data acquisition tools for dr5hn repository
- Gzip decompression for cities data
- Population and country filtering options
- Liquibase changelog generator with prefix substitution
- Unit tests for config, producer, generator, and sync components
- CircleCI CI/CD pipeline configuration

### Changed
- Upgraded to QQQ 0.35.0 with Java 21 support

## [0.1.0-alpha] - 2024-12-28

### Added
- Initial project structure
- Geographic data entities
- Data sync process
