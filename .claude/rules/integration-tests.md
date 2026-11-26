---
paths:
  - "insufind-integrations-tests/**"
---
# Integration test rules (`insufind-integrations-tests`)
- Black-box HTTP against the running stack. Never import backend code or touch the database; `/api` only.
- Test contracts and wiring (status, shape, pagination, real DB behavior). Business-logic edge cases belong in unit tests.
- REST Assured, black-box, against the running stack. Package layout under `com.isf.integrationtests`:
  `clients/`, `constants/`, `models/`, `tests/`, `validators/`.
- `clients/`: one client per API area (`ClaimsClient`, `HealthClient`) extending `ApiClient`, which owns the base URL
  (`API_URL`) and common request setup. Clients only send requests and return the raw `Response`; no assertions.
- `constants/`: finite values and HTTP vocabulary. `Http` (nested `Status`, `Header`, `MediaType`, `Path`, `Param`),
  `TestGroups` (`SMOKE`, `REGRESSION`), plus enums/constants such as `SearchScope`, `HealthStatus`, `HealthGroups`.
  No inline literals for status codes, paths, params or tags in tests.
- `tests/`: abstract `BaseTest` owns the clients; one test class per area, named without "Api" (`HealthTest`,
  `ClaimSearchTest`).
- `validators/`: fluent validators, one per response shape (`HealthValidator`, `ClaimPageValidator`), extending
  `ResponseValidator<SELF>`. Entry point is a static `validate(response)`; every check returns the validator so
  calls chain: `validate(resp).hasStatus(OK).hasStatus(UP)`. Validators hold the assertions; tests only chain them.
- Assertions in validators: a check on a single field uses a plain AssertJ assertion (fail fast). A check that
  validates an object with multiple fields uses `SoftAssertions.assertSoftly(...)` so all mismatches are reported at once.
- Validate against typed objects, not JSON paths: map response bodies to records in `models/` (named `*Response`)
  with `response.as(...)`. Validators map lazily, so status-only checks work on error responses.
- JSON mapping uses Jackson 3 only. REST Assured has no Jackson 3 support, so `JacksonObjectMapper` is registered in
  `ApiClient`. Never add Jackson 2 for REST Assured.
- `rest-assured` is pinned in the parent pom (not managed by the Spring Boot BOM).
