package com.isf.uitests.tests.otherPatterns.bdd;

import static com.isf.uitests.support.TestGroups.REGRESSION;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PATTERN: BDD (Given / When / Then). Equivalent Gherkin:
 * <pre>
 *   Scenario: Search by driver name
 *     Given an adjuster is on the search page
 *     When they search for "Maria Gonzalez"
 *     Then every result mentions "Maria Gonzalez"
 * </pre>
 * Real Cucumber adds a .feature file anyone can read; the price is a Gherkin-to-code layer to maintain.
 */
class ClaimSearchBddTest extends BaseUiTest {

    private ClaimSearchSteps steps;

    @BeforeEach
    void createSteps() {
        steps = new ClaimSearchSteps(page);
    }

    @Test
    @Tag(REGRESSION)
    @DisplayName("Scenario: search by driver name")
    void searchByDriverName() {
        steps.givenAnAdjusterIsOnTheSearchPage();
        steps.whenTheySearchFor(SeedData.MARIA);
        steps.thenEveryResultMentions(SeedData.MARIA);
    }

    @Test
    @Tag(REGRESSION)
    @DisplayName("Scenario: search with no match")
    void searchWithNoMatch() {
        steps.givenAnAdjusterIsOnTheSearchPage();
        steps.whenTheySearchFor(SeedData.NOBODY);
        steps.thenTheyAreToldNothingMatched();
    }
}
