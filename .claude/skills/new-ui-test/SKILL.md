---
name: new-ui-test
description: Write a new Playwright UI test for InsuFind following project conventions (JUnit 5, page objects, data-testid locators, factories, tags). Use when adding or extending tests in insufind-ui-tests/.
---
# New UI test
Args: feature/page and scenario.

1. Put the test in `insufind-ui-tests/src/test/java/com/isf/uitests/tests/` as `<Feature>Test`, extending `BaseUiTest`.
2. Use or extend the page object in `pages/` (one per page/component). Add locators with `getByTestId` there; if the
   element has no `data-testid`, add it in `insufind-ui/` first. Page objects act and read, they never assert.
3. Seed data (`support/SeedData`) is read-only. If the test creates data, use a factory from `support` (`aDriver().withFullName(...)`) with unique values.
4. Assert in the test with Playwright's `assertThat(locator)` (auto-waiting). No `Thread.sleep`.
5. Tag the test `@Tag("smoke")` or `@Tag("regression")`.
6. Run it: `./mvnw -pl insufind-ui-tests test -DskipUiTests=false -Dtest=<Feature>Test` (stack running).
7. Follow `.claude/rules/testing.md`.
