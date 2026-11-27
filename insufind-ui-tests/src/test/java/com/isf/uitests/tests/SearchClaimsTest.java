package com.isf.uitests.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.pages.ClaimDetailPage;
import com.isf.uitests.pages.SearchPage;
import com.isf.uitests.support.ClaimStatus;
import com.isf.uitests.support.SearchScope;
import com.isf.uitests.support.SeedData;
import com.microsoft.playwright.Route;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class SearchClaimsTest extends BaseUiTest {

    private SearchPage search;

    @BeforeEach
    void openSearch() {
        search = new SearchPage(page).open();
    }

    @Test
    @Tag("smoke")
    void searchByDriverNameShowsTheirClaims() {
        search.searchFor(SeedData.MARIA);

        assertThat(search.claimNumbers()).hasText(new String[] {
                "CLM-2026-0011", "CLM-2026-0010", "CLM-2026-0005", "CLM-2026-0002", "CLM-2026-0001"});
    }

    @Test
    @Tag("regression")
    void searchIsPartialAndCaseInsensitive() {
        search.searchFor("gONz");

        assertThat(search.rows().first()).containsText(SeedData.MARIA);
    }

    @Test
    @Tag("regression")
    void insuredScopeOnlyReturnsClaimsWhereTheDriverIsInsured() {
        search.searchFor(SeedData.MARIA, SearchScope.INSURED);

        assertThat(search.claimNumbers()).hasText(SeedData.MARIA_INSURED_CLAIMS);
    }

    @Test
    @Tag("regression")
    void otherDriversScopeOnlyReturnsClaimsWhereTheDriverIsTheOtherParty() {
        search.searchFor(SeedData.MARIA, SearchScope.OTHER);

        assertThat(search.claimNumbers()).hasText(SeedData.MARIA_OTHER_CLAIMS);
    }

    @Test
    @Tag("regression")
    void searchByLicenseNumberFindsInsuredAndOtherDriverClaims() {
        search.searchFor(SeedData.JOHN_LICENSE);

        assertThat(search.claimNumbers()).hasText(SeedData.JOHN_ANY_CLAIMS);
    }

    @Test
    @Tag("regression")
    void noMatchShowsEmptyState() {
        search.searchFor(SeedData.NOBODY);

        assertThat(search.emptyState()).isVisible();
        assertThat(search.rows()).hasCount(0);
    }

    @Test
    @Tag("regression")
    void emptyQueryListsAllClaimsPaginated() {
        search.searchFor("");
        search.setPageSize(SeedData.SMALL_PAGE_SIZE);

        assertThat(search.rows()).hasCount(SeedData.SMALL_PAGE_SIZE);
        assertThat(search.resultsSummary()).containsText(SeedData.TOTAL_CLAIMS + " claims");
        assertThat(search.pageIndicator()).hasText("Page 1 of 3");
        assertThat(search.previousPageButton()).isDisabled();

        search.nextPage();

        assertThat(search.pageIndicator()).hasText("Page 2 of 3");
        assertThat(search.previousPageButton()).isEnabled();
    }

    @Test
    @Tag("regression")
    void openingAClaimShowsItsDetailAndCanGoBack() {
        search.searchFor(SeedData.JOHN_LICENSE);

        ClaimDetailPage detail = search.openClaim("CLM-2026-0001");

        assertThat(detail.claimNumber()).hasText("CLM-2026-0001");
        assertThat(detail.status()).hasText(ClaimStatus.OPEN.label());
        assertThat(detail.insuredDriver()).containsText(SeedData.MARIA);
        assertThat(detail.otherDrivers()).hasText(new Pattern[] {Pattern.compile(SeedData.JOHN + ".*")});

        detail.backToSearch();

        assertThat(search.queryInput()).hasValue(SeedData.JOHN_LICENSE);
    }

    @Test
    @Tag("regression")
    void unknownClaimShowsNotFound() {
        page.navigate("/claims/999999");

        assertThat(new ClaimDetailPage(page).notFound()).isVisible();
    }

    @Test
    @Tag("regression")
    void backendFailureShowsAnErrorMessage() {
        page.route(Pattern.compile(".*/api/claims/search\\?.*"), route -> route.fulfill(
                new Route.FulfillOptions().setStatus(500).setBody("{}")));

        search.searchFor(SeedData.MARIA);

        assertThat(search.errorMessage()).isVisible();
        assertThat(search.rows()).hasCount(0);
    }

    @Test
    @Tag("smoke")
    void tableIsEmptyBeforeAnySearch() {
        assertThat(search.resultsTable()).isVisible();
        assertThat(search.tablePlaceholder()).isVisible();
        assertThat(search.rows()).hasCount(0);
    }

    @Test
    @Tag("regression")
    void searchByVinFindsTheClaimsOfThatCar() {
        search.searchByVin(SeedData.VIN_OF_CLAIM_1);

        assertThat(search.claimNumbers()).hasText(new String[] {"CLM-2026-0001"});
        assertThat(search.resultsSummary()).containsText("1 claim");
    }

    @Test
    @Tag("regression")
    void extraColumnsCanBeAddedFromTheColumnPicker() {
        search.searchByVin(SeedData.VIN_OF_CLAIM_1);
        assertThat(search.header("plate")).hasCount(0);

        search.showColumn("plate");

        assertThat(search.cells("plate")).hasText(new String[] {SeedData.PLATE_OF_CLAIM_1});
    }

    @Test
    @Tag("regression")
    void clickingAColumnHeaderSortsTheCurrentPage() {
        search.searchFor(SeedData.MARIA);

        search.sortBy("claim");

        assertThat(search.claimNumbers()).hasText(new String[] {
                "CLM-2026-0001", "CLM-2026-0002", "CLM-2026-0005", "CLM-2026-0010", "CLM-2026-0011"});

        search.sortBy("claim");

        assertThat(search.claimNumbers().first()).hasText("CLM-2026-0011");
    }
}
