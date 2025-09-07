# Cupid Sync Microservice

This project synchronizes hotel data, photos, rooms and reviews from the Cupid source into a local persistence layer.

For a detailed overview of how data is retrieved, stored and broadcast, see [Data Sync Strategy](docs/data-sync-strategy.md).

## Modules
- `application` – application service layer and mappers.
- `adapter-integration-cupid` – HTTP client for the Cupid API.
- `adapter-persistence` – Persistence adapters (current implementation uses JPA)
- `adapter-cache-redis` – Redis caching adapter.
- `adapter-messaging` – messaging components.
- `cupid-sync-app` – executable application module.
- `ingestion-app` – scheduled jobs for nightly synchronization.
- `core-domain` – domain models and ports.
- `web-app` – REST API layer.
- `infra` – Dockerfiles and Kubernetes manifests.

## Infrastructure
Infrastructure code lives in the `infra` module. It contains Dockerfiles for building container images and Kubernetes manifests for running Redis, Postgres, Grafana, Prometheus, Alertmanager and the events follower. Shared environment settings are centralized in the `cupid-sync-config` ConfigMap.

## Logging, Monitoring and Alerting
The application modules expose Prometheus metrics via Spring Boot Actuator. Kubernetes manifests deploy Prometheus for monitoring and alerting, and Grafana for dashboards.

## Building
The project is built with Maven. To run the tests:

```bash
mvn test
```


## Installation and Usage
See [docs/installation-and-usage.md](docs/installation-and-usage.md) for how to build the app, run it locally, call the APIs and explore metrics in Grafana.

