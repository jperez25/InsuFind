# Code style rules
- Java 25 Corretto, Maven. Package root `com.isf.<module>` (`com.isf.claims`, `com.isf.uitests`).
- Structured JSON logs. No secrets or PII in logs (driver names, license numbers, policy numbers stay out of log lines).
- Config via environment variables; `.env` is gitignored and never committed.
- Keep it thin: prefer simple, readable code over frameworks and abstraction.
- Do not commit until asked (see `git.md`).

# Spring Boot and Jackson
- Spring Boot 4.x only. Do not add Spring Boot 3.x dependencies or starters.
- Jackson 3+ only (`tools.jackson.*`: `tools.jackson.databind.json.JsonMapper`, `tools.jackson.core`). Never use Jackson 2 (`com.fasterxml.jackson.databind` / `com.fasterxml.jackson.core`) and never add the Spring Boot Jackson 2 compatibility module.
- Exception: the Jackson annotations package stays `com.fasterxml.jackson.annotation` (Jackson 3 still uses it). Nothing else under `com.fasterxml.jackson` is allowed.
- Check transitive dependencies (`./mvnw dependency:tree`) so Jackson 2 does not leak in (Playwright for Java does not need it).
- Use `JsonMapper` (built via builder, one shared instance from Spring), not `new ObjectMapper()`.

# Frontend
- TypeScript strict, function components and hooks only. Frontend talks to the backend only through `/api`.
- Every element a UI test touches has a stable `data-testid`; tests never depend on CSS classes or DOM position.
