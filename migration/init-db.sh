#!/bin/bash
set -e

# Wait for Postgres to accept connections
until pg_isready -h postgres -U "$POSTGRES_USER" >/dev/null 2>&1; do
  sleep 1
done

# Create database if it does not exist
psql -h postgres -U "$POSTGRES_USER" -d postgres <<-EOSQL
  SELECT 'CREATE DATABASE ${POSTGRES_DB}' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${POSTGRES_DB}')\gexec
EOSQL

# Apply schema V2 if no tables are present
TABLE_COUNT=$(psql -h postgres -U "$POSTGRES_USER" -d "$POSTGRES_DB" -tAc "SELECT count(*) FROM information_schema.tables WHERE table_schema='public'")
if [ "$TABLE_COUNT" = "0" ]; then
  psql -h postgres -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f /V2_schema.sql
fi
