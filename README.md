# InsuFind

Auto insurance claim searcher. Adjusters search claims by a driver (the insured) or by the other drivers involved
in the incident. Built as a playground for UI testing with Playwright.

- `insufind-app/` Spring Boot 4 + Postgres + Flyway (`GET /api/claims?q=&scope=ANY|INSURED|OTHER&page=`, `GET /api/claims/{id}`)
- `insufind-ui/` React + Vite + TypeScript
- `insufind-integrations-tests/` JUnit 5 API integration tests (HTTP against the running stack)
- `insufind-ui-tests/` Playwright for Java + JUnit 5 (page objects, parallel classes, trace and screenshot on failure)

## Run it
```bash
cp .env.example .env
docker compose up -d postgres
./mvnw -pl insufind-app spring-boot:run          # http://localhost:8080 (loads fictional seed data)
cd insufind-ui && npm install && npm run dev   # http://localhost:5173
```

## Test it
```bash
./mvnw -pl insufind-app test                                  # backend unit tests
./mvnw -pl insufind-integrations-tests test -DskipIntegrationTests=false   # API integration tests (stack must be running)
./mvnw -pl insufind-ui-tests test -DskipUiTests=false             # UI tests (stack must be running)
./mvnw -pl insufind-ui-tests test -DskipUiTests=false -Dheadless=false -Dtest=SearchClaimsTest   # watch it run
```
Failed tests leave `insufind-ui-tests/playwright-artifacts/<Test>-trace.zip` and `.png`. Open a trace with:
```bash
./mvnw -pl insufind-ui-tests exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace insufind-ui-tests/playwright-artifacts/<file>-trace.zip"
```
Record a test interactively: `-Dexec.args="codegen http://localhost:5173"`.
