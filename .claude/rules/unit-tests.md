---
paths:
  - "insufind-app/src/test/**"
---
# Unit test rules (`insufind-app/src/test`)
- Fast, no running stack, no real database. JUnit 5 + AssertJ + Mockito. Run with `./mvnw -pl insufind-app test`.
- Test sub-directories mirror the production layers: `controller/`, `service/` (add `repository/` only for custom queries).
  Test class is named `<Class>Test` in the same layer package as the class under test.
- `service/`: plain JUnit + Mockito, no Spring context. Mock the repository; test business logic and edge cases.
- `controller/`: `@WebMvcTest(<Controller>.class)` + `MockMvc`, service mocked with `@MockitoBean`.
  Assert HTTP status codes only (200, 400, 404, 405, 415); response bodies and logic are covered elsewhere.
  Cover each endpoint's success status and each way it can reject a request (validation, bad type, malformed body,
  unknown id, wrong method, wrong content type).
- Names state the behavior and the expected status: `getUnknownClaimReturns404`.
- No shared state between tests; stub in `@BeforeEach`, override per test.
- Finite values (paths, media types) are constants in the test class, not repeated literals.
