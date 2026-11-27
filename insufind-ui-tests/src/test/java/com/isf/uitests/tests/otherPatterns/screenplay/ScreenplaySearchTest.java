package com.isf.uitests.tests.otherPatterns.screenplay;

import static com.isf.uitests.support.TestGroups.REGRESSION;
import static com.isf.uitests.tests.otherPatterns.screenplay.Questions.*;
import static com.isf.uitests.tests.otherPatterns.screenplay.Tasks.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PATTERN: Screenplay. The test describes WHO does WHAT (actor, tasks) and WHAT THEY SEE (questions).
 * No page objects: locators sit in {@link Targets}, behavior in {@link Tasks} / {@link Questions}.
 * Hand-rolled and minimal; Serenity BDD is the full library for this pattern.
 */
class ScreenplaySearchTest extends BaseUiTest {

    private Actor adjuster;

    @BeforeEach
    void anAdjusterWithABrowser() {
        adjuster = Actor.named("Adjuster").whoCan(BrowseTheWeb.with(page));
    }

    @Test
    @Tag(REGRESSION)
    void adjusterFindsClaimsOfADriver() {
        adjuster.attemptsTo(
                openTheSearchPage(),
                searchForClaims(SeedData.MARIA));

        assertTrue(adjuster.asksFor(theResultRows())
                .stream().allMatch(row -> row.contains(SeedData.MARIA)));
    }

    @Test
    @Tag(REGRESSION)
    void adjusterSeesAnEmptyStateWhenNothingMatches() {
        adjuster.attemptsTo(
                openTheSearchPage(),
                searchForClaims(SeedData.NOBODY));

        assertTrue(adjuster.asksFor(theEmptyStateIsShown()));
    }

    @Test
    @Tag(REGRESSION)
    void adjusterOpensAClaimFromTheResults() {
        adjuster.attemptsTo(
                openTheSearchPage(),
                searchForClaims(SeedData.CLAIM_1),
                openTheFirstClaim());

        assertEquals(SeedData.CLAIM_1, adjuster.asksFor(theDisplayedClaimNumber()));
        assertFalse(adjuster.asksFor(theDisplayedClaimNumber()).isBlank());
    }
}
