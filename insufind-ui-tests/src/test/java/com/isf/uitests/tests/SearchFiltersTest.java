package com.isf.uitests.tests;

import static com.isf.uitests.support.TestGroups.REGRESSION;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.ClaimStatus;
import com.isf.uitests.support.ClaimType;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Period fields, table filters and the clear button. */
class SearchFiltersTest extends BaseUiTest {

    @Test
    @Tag(REGRESSION)
    void lossPeriodContainingTheIncidentDateKeepsTheClaim() {
        searchPage()
                .toggleOptionalFields()
                .setLossPeriod(SeedData.LOSS_DATE_CLAIM_1, SeedData.LOSS_DATE_CLAIM_1)
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .listsExactly(SeedData.CLAIM_1);
    }

    @Test
    @Tag(REGRESSION)
    void lossPeriodOutsideTheIncidentDateFindsNothing() {
        searchPage()
                .toggleOptionalFields()
                .setLossPeriod(SeedData.PERIOD_WITHOUT_CLAIM_1_FROM, SeedData.PERIOD_WITHOUT_CLAIM_1_TO)
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .showsEmptyState();
    }

    @Test
    @Tag(REGRESSION)
    void reportedPeriodContainingTheReportedDateKeepsTheClaim() {
        searchPage()
                .toggleOptionalFields()
                .setReportedPeriod(SeedData.REPORTED_DATE_CLAIM_1, SeedData.REPORTED_DATE_CLAIM_1)
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .listsExactly(SeedData.CLAIM_1);
    }

    @Test
    @Tag(REGRESSION)
    void reportedPeriodOutsideTheReportedDateFindsNothing() {
        searchPage()
                .toggleOptionalFields()
                .setReportedPeriod(SeedData.PERIOD_WITHOUT_CLAIM_1_FROM, SeedData.PERIOD_WITHOUT_CLAIM_1_TO)
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .showsEmptyState();
    }

    @Test
    @Tag(REGRESSION)
    void statusFilterNarrowsResultsAndAllRestoresThem() {
        var search = searchPage().searchFor("");
        int all = search.totalClaims();

        search.filterByStatus(ClaimStatus.DENIED)
                .validate()
                .hasFewerClaimsThan(all)
                .allCellsContain("status", ClaimStatus.DENIED.label());

        search.clearStatusFilter()
                .validate()
                .hasTotalClaims(all);
    }

    @Test
    @Tag(REGRESSION)
    void typeFilterNarrowsResultsAndAllRestoresThem() {
        var search = searchPage().searchFor("").showColumn("type");
        int all = search.totalClaims();

        search.filterByType(ClaimType.THEFT)
                .validate()
                .hasFewerClaimsThan(all)
                .allCellsContain("type", ClaimType.THEFT.label());

        search.clearTypeFilter()
                .validate()
                .hasTotalClaims(all);
    }

    @Test
    @Tag(REGRESSION)
    void clearResetsTheFormAndTheResults() {
        searchPage()
                .toggleOptionalFields()
                .setLossPeriod(SeedData.LOSS_DATE_CLAIM_1, SeedData.LOSS_DATE_CLAIM_1)
                .searchFor(SeedData.CLAIM_1)
                .validate()
                .showsResults()
                .and()
                .clear()
                .validate()
                .showsPlaceholder()
                .hasQueryValue("")
                .lossPeriodIs("", "");
    }
}
