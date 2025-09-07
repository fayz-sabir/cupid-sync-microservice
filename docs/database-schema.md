# Database Schema

This document describes the database schema for the Cupid Sync microservice. The schema includes tables for hotels, hotel translations, and reviews. Each table's primary columns and relationships are illustrated below.

```mermaid
erDiagram
    HOTEL {
        BIGINT id PK
        BIGINT hotel_id
        VARCHAR name
        BIGINT chain_id
        INT stars
        DOUBLE rating
        VARCHAR hero_photo
        DOUBLE lat
        DOUBLE lng
        BOOLEAN child_allowed
        BOOLEAN pets_allowed
        TEXT contact_json
        TEXT checkin_policy_json
        TEXT address_json
    }
    HOTEL_I18N {
        BIGINT id PK
        BIGINT hotel_id FK
        VARCHAR lang
        VARCHAR parking
        VARCHAR important_info
        TEXT markdown_description
        TEXT facilities
        TEXT rooms
        VARCHAR hotel_type
        TEXT description
        TEXT policies_json
        TEXT photos_json
    }
    REVIEW {
        BIGINT id PK
        BIGINT review_id
        BIGINT hotel_id FK
        BIGINT external_hotel_id
        VARCHAR country
        VARCHAR type
        VARCHAR name
        DOUBLE avg_score
        VARCHAR headline
        TEXT pros
        TEXT cons
        VARCHAR language
        VARCHAR source
        DATETIME date
    }

    HOTEL ||--o{ HOTEL_I18N : "translations"
    HOTEL ||--o{ REVIEW : "reviews"
```

## Tables

### `hotel`
Represents a hotel synchronized from the external source.

### `hotel_i18n`
Stores translations and localized information for a hotel. Each row is linked to a hotel and a language.

### `review`
Captures customer reviews associated with a hotel.
