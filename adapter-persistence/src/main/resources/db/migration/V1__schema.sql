-- Consolidated schema for Cupid Sync
CREATE TABLE hotel (
  id BIGINT PRIMARY KEY,
  chain_id BIGINT,
  stars INT,
  rating DOUBLE PRECISION,
  hero_photo TEXT,
  lat DOUBLE PRECISION,
  lng DOUBLE PRECISION,
  contact JSONB,
  checkin_policy JSONB,
  photos JSONB
);

CREATE TABLE room (
  id BIGINT PRIMARY KEY,
  hotel_id BIGINT NOT NULL REFERENCES hotel(id) ON DELETE CASCADE,
  name TEXT,
  description TEXT,
  size_sqm DOUBLE PRECISION,
  size_unit TEXT,
  max_adults INT,
  max_children INT,
  beds JSONB,
  photos JSONB
);

CREATE TABLE room_amenity (
  id BIGSERIAL PRIMARY KEY,
  room_id BIGINT NOT NULL REFERENCES room(id) ON DELETE CASCADE,
  name TEXT
);

CREATE TABLE catalog (
  id BIGINT PRIMARY KEY,
  facilities JSONB,
  policies JSONB
);

CREATE TABLE hotel_i18n (
  id BIGSERIAL PRIMARY KEY,
  hotel_id BIGINT NOT NULL REFERENCES hotel(id) ON DELETE CASCADE,
  lang VARCHAR(8) NOT NULL,
  hotel_name TEXT,
  hotel_type TEXT,
  description_html OID,
  description_md TEXT,
  important_info TEXT,
  parking_text TEXT,
  address_json OID,
  UNIQUE (hotel_id, lang)
);

CREATE TABLE room_i18n (
  id BIGSERIAL PRIMARY KEY,
  room_id BIGINT NOT NULL REFERENCES room(id) ON DELETE CASCADE,
  hotel_i18n_id BIGINT REFERENCES hotel_i18n(id) ON DELETE CASCADE,
  lang VARCHAR(8) NOT NULL,
  room_name TEXT,
  description OID,
  beds JSONB,
  UNIQUE (room_id, lang)
);

CREATE TABLE facility_i18n (
  id BIGSERIAL PRIMARY KEY,
  facility_id BIGINT NOT NULL,
  hotel_i18n_id BIGINT REFERENCES hotel_i18n(id) ON DELETE CASCADE,
  lang VARCHAR(8) NOT NULL,
  name TEXT,
  UNIQUE (facility_id, lang)
);

CREATE TABLE policy_i18n (
  id BIGSERIAL PRIMARY KEY,
  policy_id BIGINT NOT NULL,
  hotel_i18n_id BIGINT REFERENCES hotel_i18n(id) ON DELETE CASCADE,
  lang VARCHAR(8) NOT NULL,
  name TEXT,
  description OID,
  UNIQUE (policy_id, lang)
);

CREATE TABLE amenity_i18n (
  id BIGSERIAL PRIMARY KEY,
  amenity_id BIGINT NOT NULL,
  room_i18n_id BIGINT REFERENCES room_i18n(id) ON DELETE CASCADE,
  lang VARCHAR(8) NOT NULL,
  name TEXT,
  UNIQUE (amenity_id, lang)
);

CREATE TABLE photo_class_i18n (
  id BIGSERIAL PRIMARY KEY,
  class_id BIGINT NOT NULL,
  hotel_i18n_id BIGINT REFERENCES hotel_i18n(id) ON DELETE CASCADE,
  lang VARCHAR(8) NOT NULL,
  class_name TEXT,
  UNIQUE (class_id, lang)
);

CREATE TABLE review (
  id BIGINT PRIMARY KEY,
  hotel_id BIGINT NOT NULL REFERENCES hotel(id) ON DELETE CASCADE,
  avg_score DOUBLE PRECISION,
  headline TEXT,
  pros OID,
  cons OID,
  language VARCHAR(8),
  source VARCHAR(64),
  created_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_review_hotel_createdat ON review(hotel_id, created_at DESC);

CREATE TABLE hotel_rooms
(
    hotel_entity_id BIGINT NOT NULL,
    rooms_id        BIGINT NOT NULL
);

CREATE TABLE room_amenities
(
    room_entity_id BIGINT NOT NULL,
    amenities_id   BIGINT NOT NULL
);
