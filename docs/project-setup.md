# InsuFind: project setup

Status: initial setup done on 2026-11-25. Nothing is committed yet.

## 1. Purpose
InsuFind is an auto insurance claim searcher. Adjusters search claims by a driver (the insured) or by the other
drivers involved in the incident. The product is deliberately small: the main goal is to learn and grow UI testing
with Playwright, so the backend and frontend stay thin and the test suite is the part that grows.

## 2. Stack
| Area | Choice | Notes |
|---|---|---|
| Language / build | Java 25, Maven multi-module with wrapper | `./mvnw`, no system Maven needed |
| Backend | Spring Boot 4.2.0 | Jackson 3 only (`tools.jackson.*`), JPA, Flyway, Actuator |
| Database | Postgres 17 (Docker Compose) | one schema, migrations through Flyway |
| Frontend | React 19, Vite, TypeScript, React Router | open decision: React vs Vue (see section 9) |
| UI tests | Playwright for Java 1.65.1, JUnit 5, AssertJ | Lombok available for test models |
| Root package | `com.isf` | `com.isf.claims` (backend), `com.isf.uitests` (tests) |

## 3. Layout
```
InsuFind/
  pom.xml                 parent (com.isf:insufind-parent), modules: insufind-app, insufind-integrations-tests, insufind-ui-tests
  insufind-app/           Spring Boot API and data
  insufind-ui/            React SPA (not a Maven module; run with npm)
  insufind-integrations-tests/  API integration tests (HTTP against the running stack)
  insufind-ui-tests/      Playwright UI tests
  docker-compose.yml      local Postgres
  .env.example            dummy config; copy to .env (gitignored)
  docs/                   this file and future design notes
  .claude/rules, .claude/skills, CLAUDE.md
```

## 4. Backend
- `GET /api/claims?q=&scope=ANY|INSURED|OTHER&page=&size=`
  - `q` is a case-insensitive partial match on driver name or license number (and claim number when scope is `ANY`).
    LIKE wildcards in `q` are escaped. An empty `q` lists everything.
  - `ANY` searches the insured driver, the other drivers and the claim number. `INSURED` and `OTHER` limit the
    search to that kind of driver.
  - Paginated (default size 5, max 50), newest incident first with id as tie-breaker.
  - Response: `{ items[], page, size, totalItems, totalPages }`.
- `GET /api/claims/{id}` returns the claim detail, or 404.
- Entities never leave the API: controllers return DTO records (`ClaimDtos`).
- Health: `/actuator/health` (liveness and readiness probes enabled). Logs are structured JSON (ECS).
- Config through environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `PORT`) with local defaults.

### Data model
- `driver(id, full_name, license_number unique)`
- `claim(id, claim_number unique, policy_number, incident_date, location, status, description, insured_driver_id)`
  with status in `OPEN, IN_REVIEW, APPROVED, DENIED, CLOSED`
- `claim_other_driver(claim_id, driver_id)`: the other drivers involved in an incident

### Migrations and seed data
- `db/migration/V1__create_claims_schema.sql`: schema. Never edit an applied migration; add a new one.
- `db/seed/V1000__seed_demo_data.sql`: 8 fictional drivers and 12 claims. Flyway loads `db/seed` by default in
  `application.yml`; drop that location for a real environment. All people and numbers are fictional.

## 5. Frontend
- Search page (`/`): query input, scope select (any / insured / other drivers), results table, empty state, error
  state, pagination. The search state lives in the URL (`?q=&scope=&page=`), so reload and Back restore it.
  Nothing is fetched until the first search (an empty search lists all claims).
- Claim detail page (`/claims/:id`): claim data, insured driver, other drivers, not-found state, back button.
- The browser only calls `/api`. In dev Vite (port 5173) proxies `/api` to the backend on 8080.
- Every element a test touches has a `data-testid`.

## 6. UI test framework
- `BaseUiTest`: one Playwright and Browser per test class, a fresh `BrowserContext` and `Page` per test. On failure it
  saves a trace (`<Class>.<test>-trace.zip`) and a full-page screenshot to `insufind-ui-tests/playwright-artifacts/`.
- `pages/`: page objects (`SearchPage`, `ClaimDetailPage`). They act and read, and hold locators. They never assert.
- `tests/`: tests hold the assertions, using Playwright web-first `assertThat(locator)`.
- `support/`: `Env` (config), `SeedData` (read-only reference data), `SearchScope` and `ClaimStatus` enums.
- Parallelism: test classes run in parallel (3 threads), methods inside a class run sequentially
  (`junit-platform.properties`).
- Config: `BASE_URL` (default `http://localhost:5173`), `HEADLESS` (default true), `BROWSER` (`chromium`, `firefox`
  or `webkit`). A system property of the same name in camelCase (`-Dheadless=false`) wins over the env variable.
- Tags: `smoke` (app up, main search path) and `regression` (everything else).
- UI tests are skipped by default so `./mvnw test` does not need a running stack. Enable with `-DskipUiTests=false`.
- Playwright downloads the browsers on the first run (this takes a while once).

### Current tests (`SearchClaimsTest`, 10 tests)
Search by name (smoke), partial and case-insensitive match, insured scope, other-drivers scope, search by license
number, no results, empty query with pagination, open a claim and go back (query restored), unknown claim id, and a
forced backend 500 (route interception).

## 7. How to run
```bash
cp .env.example .env
docker compose up -d postgres                      # Docker runs through Colima: `colima start`
./mvnw -pl insufind-app spring-boot:run                 # http://localhost:8080
cd insufind-ui && npm install && npm run dev          # http://localhost:5173

./mvnw -pl insufind-app test                            # backend unit tests
./mvnw -pl insufind-ui-tests test -DskipUiTests=false       # UI tests (stack must be running)
./mvnw -pl insufind-ui-tests test -DskipUiTests=false -Dheadless=false -Dtest=SearchClaimsTest   # watch a test run
```
Open a trace:
`./mvnw -pl insufind-ui-tests exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace <trace.zip>"`.
Record a test: same command with `-Dexec.args="codegen http://localhost:5173"`.

## 8. Claude rules and skills (`.claude/`)
Adapted from the SafeDrive project; only the project-agnostic parts were kept.
- `rules/git.md`: always ask before commit and push; commit messages start with `ISF-<n>`; never commit secrets.
- `rules/code-style.md`: Java 25, Spring Boot 4 and Jackson 3 only, env-var config, JSON logs, no secrets or PII in
  logs, `data-testid` on tested elements.
- `rules/architecture.md`: backend, frontend and insufind-ui-tests have one job each; the browser only calls `/api`; schema
  changes through Flyway only; DTOs at the API boundary; list endpoints paginated.
- `rules/testing.md`: page objects without assertions, locator order (`getByTestId`, then role or label), no
  `Thread.sleep`, isolated and parallel-safe tests, read-only seed data, factories and enums, tags, failure artifacts.
- `skills/new-ui-test`: steps for adding a Playwright test.
- Not carried over: `contracts.md` and the four SafeDrive skills (Kafka, minikube and TestNG specific).

## 9. Verification at setup time
- Backend unit tests: 2 pass. Frontend `npm run build`: passes.
- Full stack up (Postgres, backend, Vite): 10 of 10 UI tests pass.
- A deliberately failing test produced the trace zip and the screenshot.

## 10. Open decisions and next steps
- **React vs Vue.** React is the default. The tests only depend on `data-testid`, so a switch would not touch
  `insufind-ui-tests/`.
- **Ticket key.** `ISF` is assumed; change it in `rules/git.md` if the real key differs.
- **First commit.** Not made yet. It needs your approval and a ticket number.
- **Reporting.** No Allure or CI yet. Candidates: Allure reports, a GitHub Actions workflow that starts the stack
  and runs the smoke tag.
- **Test data.** Tests only read the seed data. Writing tests (create or update a claim) will need a create API or a
  setup path, plus test-data factories (`aClaim()`, `aDriver()`).
- **Ideas to grow the suite.** Filters (status, date range), sorting, authentication and adjuster roles, a
  claim-edit flow, accessibility checks, visual snapshots, API-assisted setup, cross-browser runs, and a slow-response
  or loading-state test.
