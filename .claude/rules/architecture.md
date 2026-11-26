# Architecture rules
- Four parts, one job each: `insufind-app` (REST API, data, unit tests), `insufind-ui` (SPA), `insufind-integrations-tests` (black-box API tests), `insufind-ui-tests` (black-box browser tests).
- The browser only calls `/api/**` on the backend (Vite proxies it in dev). No other cross-origin calls.
- Schema changes only through Flyway migrations in `insufind-app/src/main/resources/db/migration` (never edit an applied one).
  Seed/demo data lives in `db/seed` and must never contain real people: all names and license numbers are fictional.
- Entities never leave the API: controllers return DTO records.
- Every list endpoint is paginated (`page`, `size`) and sorted deterministically.
- Backend exposes Actuator health and readiness endpoints.
- `insufind-integrations-tests` and `insufind-ui-tests` must not import backend code or touch the database; it only drives the browser (and may call `/api` for setup).
