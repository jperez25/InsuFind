package com.isf.uitests.tests;

import static com.isf.uitests.support.TestGroups.REGRESSION;
import static com.isf.uitests.support.TestGroups.SMOKE;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.ClaimStatus;
import com.isf.uitests.support.SearchScope;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class SearchClaimsTest extends BaseUiTest {

    @Test
    @Tag(SMOKE)
    void searchByDriverNameShowsTheirClaims() {
        searchPage()
                .searchFor(SeedData.MARIA)
                .validate()
                .showsResults()
                .allRowsContain(SeedData.MARIA);
    }

    @Test
    @Tag(SMOKE)
    void tableIsEmptyBeforeAnySearch() {
        searchPage()
                .validate()
                .showsPlaceholder();
    }

    @Test
    @Tag(REGRESSION)
    void searchIsPartialAndCaseInsensitive() {
        searchPage()
                .searchFor(SeedData.MARIA_FRAGMENT)
                .validate()
                .showsResults()
                .allRowsContain(SeedData.MARIA_FRAGMENT);
    }

    @Test
    @Tag(REGRESSION)
    void insuredScopeOnlyReturnsClaimsWhereTheDriverIsInsured() {
        searchPage()
                .searchFor(SeedData.MARIA, SearchScope.INSURED)
                .validate()
                .showsResults()
                .allCellsContain("insured", SeedData.MARIA);
    }

    @Test
    @Tag(REGRESSION)
    void otherDriversScopeOnlyReturnsClaimsWhereTheDriverIsTheOtherParty() {
        searchPage()
                .searchFor(SeedData.MARIA, SearchScope.OTHER)
                .validate()
                .showsResults()
                .allCellsContain("others", SeedData.MARIA)
                .noCellContains("insured", SeedData.MARIA);
    }

    @Test
    @Tag(REGRESSION)
    void searchByLicenseNumberFindsClaimsOfThatDriver() {
        searchPage()
                .searchFor(SeedData.JOHN_LICENSE)
                .validate()
                .showsResults()
                .and()
                .openFirstClaim()
                .validate()
                .involvesLicense(SeedData.JOHN_LICENSE);
    }

    @Test
    @Tag(REGRESSION)
    void noMatchShowsEmptyState() {
        searchPage()
                .searchFor(SeedData.NOBODY)
                .validate()
                .showsEmptyState();
    }

    @Test
    @Tag(REGRESSION)
    void emptyQueryListsAllClaimsPaginated() {
        searchPage()
                .setPageSize(SeedData.SMALL_PAGE_SIZE)
                .searchFor("")
                .validate()
                .hasRows(SeedData.SMALL_PAGE_SIZE)
                .pagerMatchesSummaryFor(SeedData.SMALL_PAGE_SIZE)
                .isOnPage(1)
                .hasPreviousPageEnabled(false)
                .and()
                .nextPage()
                .validate()
                .isOnPage(2)
                .hasPreviousPageEnabled(true);
    }

    @Test
    @Tag(REGRESSION)
    void openingAClaimShowsItsDetailAndCanGoBack() {
        searchPage()
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .listsClaim(SeedData.CLAIM_1)
                .and()
                .openClaim(SeedData.CLAIM_1)
                .validate()
                .hasClaimNumber(SeedData.CLAIM_1)
                .and()
                .backToSearch()
                .validate()
                .listsClaim(SeedData.CLAIM_1);
    }

    @Test
    @Tag(REGRESSION)
    void unknownClaimShowsNotFound() {
        claimDetailPage()
                .open(SeedData.UNKNOWN_CLAIM_ID)
                .validate()
                .isNotFound();
    }

    @Test
    @Tag(REGRESSION)
    void backendFailureShowsAnErrorMessage() {
        searchPage()
                .failSearchApi()
                .searchFor(SeedData.MARIA)
                .validate()
                .showsError();
    }

    @Test
    @Tag(REGRESSION)
    void searchByVinFindsTheClaimsOfThatCar() {
        searchPage()
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .listsExactly(SeedData.CLAIM_1)
                .hasTotalClaims(1);
    }

    @Test
    @Tag(REGRESSION)
    void searchByClaimNumberFindsThatSingleClaim() {
        searchPage()
                .searchFor(SeedData.CLAIM_777)
                .validate()
                .listsExactly(SeedData.CLAIM_777)
                .hasTotalClaims(1)
                .and()
                .openFirstClaim()
                .validate()
                .hasStatus(ClaimStatus.APPROVED)
                .hasPolicyNumber("POL-100002");
    }

    @Test
    @Tag(REGRESSION)
    void extraColumnsCanBeAddedFromTheColumnPicker() {
        searchPage()
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .validate()
                .lacksColumn("plate")
                .and()
                .showColumn("plate")
                .validate()
                .hasColumn("plate")
                .allCellsContain("plate", SeedData.PLATE_OF_CLAIM_1);
    }

    @Test
    @Tag(REGRESSION)
    void clickingAColumnHeaderSortsTheCurrentPage() {
        searchPage()
                .searchFor(SeedData.MARIA)
                .sortBy("claim")
                .validate()
                .isSortedBy("claim", true)
                .and()
                .sortBy("claim")
                .validate()
                .isSortedBy("claim", false);
    }

    @Test
    @Tag(REGRESSION)
    void optionalPeriodFieldsAreFoldedByDefaultAndCanBeToggled() {
        searchPage()
                .validate()
                .optionalFieldsAreFolded()
                .and()
                .toggleOptionalFields()
                .validate()
                .optionalFieldsAreOpen()
                .and()
                .toggleOptionalFields()
                .validate()
                .optionalFieldsAreFolded();
    }

    @Test
    @Tag(REGRESSION)
    void claimDetailShowsVehicleAndPolicyFields() {
        searchPage()
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .openClaim(SeedData.CLAIM_1)
                .validate()
                .hasClaimNumber(SeedData.CLAIM_1)
                .hasVin(SeedData.VIN_OF_CLAIM_1)
                .hasPlate(SeedData.PLATE_OF_CLAIM_1)
                .showsAllDetailFields();
    }
}
