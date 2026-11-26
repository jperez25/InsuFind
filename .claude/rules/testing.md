---
paths:
  - "insufind-ui-tests/**"
  - "insufind-integrations-tests/**"
  - "insufind-app/src/test/**"
---
# Testing rules (shared)
- Put each check at the cheapest level that can catch the bug (unit, then integration, then UI); do not repeat the same
  check at every level. Level-specific rules: `unit-tests.md`, `integration-tests.md`, `ui-tests.md`.
- Tests must be atomic and parallel-safe: no shared state, no test depends on another.
- Seed data is read-only reference data. A test that needs to change data creates its own, unique per test, cleaned up best-effort.
- Finite values (claim status, search scope, routes, status codes) come from constants/enums, not inline literals.
- Every integration and UI test has one tag: smoke (app up, main search path) or regression (everything else).
- Cover: empty query, no results, partial and case-insensitive match, insured vs other-driver scope, pagination,
  detail navigation, backend error state, slow response (route interception).
