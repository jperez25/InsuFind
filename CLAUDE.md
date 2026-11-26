# InsuFind

Auto insurance claim searcher. Adjusters search claims by a driver (the insured) or by the other drivers
involved. The UI test suite (Playwright) is the learning goal; backend and frontend stay thin.

## Layout
- `insufind-app/` Spring Boot 4 REST API (`/api/claims`), Postgres, Flyway. Package `com.isf.claims`.
- `insufind-ui/` React + Vite + TypeScript SPA (dev server on 5173, proxies `/api` to 8080). Frontend framework is
  still an open choice (React vs Vue); React is the current default.
- `insufind-integrations-tests/` JUnit 5 black-box API integration tests (HTTP against the running stack). Package `com.isf.integrationtests`.
- `insufind-ui-tests/` Playwright (Java) + JUnit 5 end-to-end UI tests. Package `com.isf.uitests`.
- `docker-compose.yml` local Postgres. `deploy/k8s/` minikube manifests, `scripts/` minikube/colima scripts. `docs/` notes and decisions.

## Stack
Java 25, Spring Boot 4.x, Maven multi-module (wrapper), Postgres 17, Flyway, JUnit 5, Playwright for Java,
React 19 + Vite + TypeScript. Root package: `com.isf`.

## Commands
- Database: `docker compose up -d postgres`
- Backend: `./mvnw -pl insufind-app spring-boot:run` (seed data loads through Flyway)
- Frontend: `cd insufind-ui && npm install && npm run dev`
- Minikube (colima, docker driver): `scripts/insufind.sh [-all|-vm|-minikube|-db|-app|-ui] [-down]` (no service flag = all; `CPUS`/`MEMORY` env, default 4/8). `scripts/port-forward.sh` exposes UI on 5173 and API on 8080.
- Backend unit tests: `./mvnw -pl insufind-app test`
- Integration tests (stack must be running): `./mvnw -pl insufind-integrations-tests test -DskipIntegrationTests=false` (`API_URL` env var)
- UI tests (stack must be running): `./mvnw -pl insufind-ui-tests test -DskipUiTests=false`
  (options: `-Dheadless=false` to watch, `BASE_URL`, `BROWSER=chromium|firefox|webkit` env vars)
- Open a Playwright trace: `./mvnw -pl insufind-ui-tests exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace <trace.zip>"`

## Rules
Detailed rules are in `.claude/rules/`. Skills are in `.claude/skills/`.
