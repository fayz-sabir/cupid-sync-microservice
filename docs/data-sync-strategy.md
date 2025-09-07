# Data Sync Strategy

This service synchronizes hotel data from the external Cupid API into local storage. Synchronization can be triggered manually via REST endpoints or automatically through a nightly job.

## Sync Triggers
- **Manual API calls** – `/api/v1/hotels/{id}/sync` updates a hotel and `/api/v1/hotels/{id}/reviews/sync` refreshes reviews.
- **Nightly job** – `NightlySyncJob` runs at midnight to process configured hotel IDs.
- **Full sync endpoint** – `/api/v1/hotels/sync-all` triggers a full synchronization for all configured hotels.

## Data Retrieval
Requests to Cupid are performed by `CupidClientAdapter` which wraps a Feign client with Resilience4j for retries and circuit breaking and records metrics for each call.

## Processing Flow
### Hotel Data
1. `SyncHotelUseCase` fetches the hotel and optional localization from Cupid.
2. External models are converted to domain objects and upserted into the database.
3. Existing cached hotel views are evicted and, if localization was fetched, a fresh view is cached.
4. A `HotelSynced` event is published describing the update.

### Reviews
1. `SyncReviewsUseCase` retrieves reviews for the hotel.
2. Reviews are mapped, persisted and cached for subsequent reads.
3. A `ReviewsSynced` event is emitted with counts and applied limits.

## Caching
`RedisCacheAdapter` stores rendered hotel views and review lists with TTLs and tracks cache hit/miss metrics. Cache entries are evicted when hotel data changes to ensure consistency.

## Event Publication
Both sync use cases publish domain events after successful updates. Downstream systems can react asynchronously to `HotelSynced` and `ReviewsSynced` events.

## Nightly Sync
`NightlySyncJob` coordinates nightly execution, invoking the sync use cases for each configured hotel in multiple languages and catching failures to continue processing.
