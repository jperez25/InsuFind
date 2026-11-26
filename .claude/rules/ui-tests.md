---
paths:
  - "insufind-ui-tests/**"
---
# UI test rules (`insufind-ui-tests`)
- Black-box browser tests against the running stack; the slowest and most brittle level, so keep them few and
  user-journey focused. Anything checkable at the API or unit level does not belong here.
- Playwright for Java + JUnit 5; Playwright's `assertThat(locator)` for anything in the page, AssertJ for plain values.
  A `BaseUiTest` owns browser lifecycle and config.
- Page Object Model: one class per page or component in `pages/`. Page objects expose intent (`searchFor(...)`,
  `results()`), hold the locators, and never contain assertions. UI tests hold the assertions.
- Locators: prefer `getByTestId`, then `getByRole`/`getByLabel`. Never XPath, CSS classes or positional selectors.
- Never `Thread.sleep` or fixed waits. Rely on Playwright auto-waiting and web-first assertions
  (`assertThat(locator).hasCount(3)`); wait explicitly on a response or locator state when needed.
- Fetch, wait and check are separate: page objects act and read, Playwright waits, tests check.
- One `BrowserContext` per test.
- When tests create data, build it with factories returning the model itself: names start with an article
  (`aDriver()`, `aClaim().withStatus(...)`). Models are Java records with Lombok `@With` + `@Builder(toBuilder = true)`.
  Field names are full words (`licenseNumber`, not `lic`).
- Tag every test `smoke` or `regression`.
- On failure the base class saves a Playwright trace (and screenshot) to `insufind-ui-tests/playwright-artifacts/`.
