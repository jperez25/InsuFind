# InsuFind: search performance and enhancements

Status: plan only, nothing measured yet. Written on 2026-11-26. All numbers below are estimates until the test in
section 4 is run.

## 1. Where search stands
- `GET /api/claims` (`q` + `scope`) and `POST /api/claims/search` (`ClaimSearchCriteria` body, `page`/`size` query params).
- The POST builds its query dynamically with JPA Specifications (`ClaimSpecifications`). Text filters are
  `lower(col) like '%x%' escape '\'`. Results are sorted `incident_date desc, id desc`.
- Seed data: 1000 claims and 8 drivers (Flyway `V1000`, `V1001`, `V1002`). At this size every search is about 1 ms.

## 2. Known issues to fix before scale matters
| # | Issue | Why it matters | Fix |
|---|---|---|---|
| 1 | `idx_claim_vehicle_plate` / `idx_claim_vehicle_vin` index the raw column, but queries filter on `lower(...)` | The indexes are never used | Index `lower(vehicle_vin)` and `lower(vehicle_plate)`, or store them normalized (upper case) and drop `lower()` |
| 2 | No index on `policy_state` / `loss_state` | Low cardinality, so only useful combined with other filters | Add only if measurement shows a need (composite with `incident_date`) |
| 3 | Leading-wildcard `LIKE` cannot use a btree index | Full scan of `driver` (and `claim`) per text search | `pg_trgm` GIN indexes (section 5) |
| 4 | N+1 loading in `ClaimService.toSummary` | One page of 50 claims issues about 100 small queries | `@EntityGraph` / join fetch for `insuredDriver` and `otherDrivers` |
| 5 | `count(distinct ...)` runs for every page request | Often the slowest part of a broad search | Cap or skip the exact total on broad searches (section 5) |
| 6 | `size=500` returns 500, not 400 | `ConstraintViolationException` has no handler (affects GET and POST) | `@RestControllerAdvice` mapping it to 400 |

## 3. Expected behavior by size (estimates, not measured)
Assumes leading-wildcard `LIKE`, no trigram indexes, a modest machine such as the minikube pod.

| Claims | Expected single search |
|---|---|
| up to 10k | a few ms |
| ~100k | 10 to 50 ms |
| 500k to 1M | 100 to 500 ms, broad searches and the count query dominate |
| 5M and up | seconds for broad searches |

Searches on exact, indexed values (claim number, status plus date range) stay fast much longer. The worst cases are a
broad `q` such as `a`, `scope=OTHER` (joins through `claim_other_driver`), and deep pages (`OFFSET`).

## 4. Performance test plan
Goal: replace the estimates above with real numbers on the minikube stack.

1. **Isolate the data.** Do not put millions of rows in the committed seed: the Postgres volume is an `emptyDir`, so
   the seed re-runs on every pod restart. Use a scratch database or schema (for example `insufind_perf`) on the same
   Postgres, loaded with `generate_series`.
2. **Sizes.** 100k, 1M and 5M claims. Reuse the same fictional drivers plus a generated pool of fictional drivers
   (no real names, no real license numbers).
3. **Queries to time** (`EXPLAIN (ANALYZE, BUFFERS)`, run each three times, keep the last two, cache warm):
   - broad name search (`q=a`, scope `ANY`), narrow name search (`q=smith`), scope `OTHER`
   - claim number prefix, plate contains, exact VIN
   - status plus date range, state filters, a combined filter
   - the count query, and page 1 versus a deep page (`page=10000`)
4. **Two configurations.** Baseline (current schema) versus with the section 5 indexes. Record the plan type
   (Seq Scan / Bitmap / Index), time and buffers read.
5. **End to end.** Time `POST /api/claims/search` through the port-forward with a loop of `curl` calls (p50 and p95)
   to include N+1 and JSON mapping.
6. **Record results** in a table in this file (size, query, baseline ms, indexed ms).

Resource notes for the run:
- Colima default is 8 GB (`MEMORY`), minikube gets 1 GB less. Pods have no requests or limits. Estimated table plus
  indexes size: 1M claims about 0.6 GB (1 to 1.5 GB with trigram indexes), 5M about 3 GB (5 to 7 GB with trigram).
- Up to about 1M claims fits in the VM page cache with 8 GB. At 5M raise `MEMORY` to 12 to 16 and add a PVC.
- Per-query memory is bounded by `work_mem` (4 MB default) and spills to disk, so large searches get slower, not bigger.
  The app heap does not depend on table size (one page of at most 50 claims).

## 5. Enhancements, in order of effort
1. **Fix the indexes** (issue 1 above). One Flyway migration.
2. **Trigram indexes.** Migration with `CREATE EXTENSION IF NOT EXISTS pg_trgm;` then GIN indexes with
   `gin_trgm_ops` on `lower(driver.full_name)`, `lower(driver.license_number)`, `lower(claim.claim_number)`,
   `lower(claim.vehicle_plate)`. Makes `%text%` use an index. Check the extension is allowed in the target Postgres.
3. **Remove the N+1** with `@EntityGraph` on the Specification query. Watch for the `distinct` plus fetch-join plus
   pagination combination (Hibernate can paginate in memory). If that happens, fetch ids first, then load the page.
4. **Cheaper pagination.**
   - Make the exact `totalItems` optional, or return "more than N" past a cap.
   - Keyset pagination (`incident_date`, `id` cursor) for deep paging.
5. **Fuzzy name search.** `fuzzy` flag in `ClaimSearchCriteria`, applied only to the driver-name part of `q`, using
   `pg_trgm` `similarity()` / `%`. Identifiers (license, VIN, claim number) stay exact or prefix. Default off, so
   existing behavior and tests are unchanged. For deterministic UI tests, use seeded names and a fixed threshold.
6. **Operational limits.** `resources.requests/limits` on the postgres and app pods (for example 1 GB and 768 MB), a
   PVC for Postgres once data is large, and a statement timeout on the search query.
7. **Dedicated search engine** (Elasticsearch / OpenSearch) only if typo tolerance, ranking or tens of millions of
   rows are needed. Not planned.

## 6. Test impact
- The UI tests no longer assume 12 claims: they match dynamically and derive totals and page counts from the page.
  Keep it that way; only unique identifiers (claim number, VIN, license) are matched exactly.
- The UI shows the new fields (`claimType`, `reportedDate`, `vehiclePlate`, `vehicleVin`, `policyState`, `lossState`)
  on the detail page and as optional table columns, with `data-testid` attributes, and the UI tests cover them.
- Every new filter, index or ranking change needs an integration test against `POST /api/claims/search` first, then a
  UI test.

## 7. Decisions already made
- Search stays on Postgres. `GET` for the simple search box, `POST /api/claims/search` for multi-filter search, both
  through one service-level contract.
- Flyway runs with `out-of-order: true` because seed migrations are versioned `V1000+`, above any new schema migration.
