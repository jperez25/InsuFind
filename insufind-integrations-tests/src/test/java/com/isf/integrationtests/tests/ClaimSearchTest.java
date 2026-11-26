package com.isf.integrationtests.tests;

import static com.isf.integrationtests.constants.Http.Status.BAD_REQUEST;
import static com.isf.integrationtests.constants.Http.Status.NOT_FOUND;
import static com.isf.integrationtests.constants.Http.Status.OK;
import static com.isf.integrationtests.constants.TestGroups.REGRESSION;
import static com.isf.integrationtests.constants.TestGroups.SMOKE;

import com.isf.integrationtests.constants.SearchScope;
import com.isf.integrationtests.models.ClaimPageResponse;
import com.isf.integrationtests.models.ClaimSummaryResponse;
import com.isf.integrationtests.validators.ClaimDetailValidator;
import com.isf.integrationtests.validators.ClaimPageValidator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ClaimSearchTest extends BaseTest {

  @Test
  @Tag(SMOKE)
  void claimsListIsPaginated() {
    ClaimPageValidator.validate(claims.list(0, 5))
        .hasStatus(OK)
        .hasPage(0)
        .hasSize(5)
        .hasAtMostItems(5)
        .hasTotalItems();
  }

  @Test
  @Tag(REGRESSION)
  void searchWithNoMatchReturnsEmptyPage() {
    ClaimPageValidator.validate(claims.search("Zzyzx Nonexistent", SearchScope.ANY, 0, 5))
        .hasStatus(OK)
        .isEmpty();
  }

  @Test
  @Tag(REGRESSION)
  void searchIsCaseInsensitiveAndPartial() {
    ClaimPageValidator.validate(claims.search("maria", SearchScope.INSURED, 0, 5))
        .hasStatus(OK)
        .insuredDriversContain("maria");
  }

  @Test
  @Tag(REGRESSION)
  void claimDetailMatchesListedClaim() {
    ClaimSummaryResponse listed = claims.list(0, 1).as(ClaimPageResponse.class).items().getFirst();

    ClaimDetailValidator.validate(claims.byId(listed.id()))
        .hasStatus(OK)
        .hasId(listed.id())
        .matches(listed);
  }

  @Test
  @Tag(REGRESSION)
  void unknownClaimIsNotFound() {
    ClaimDetailValidator.validate(claims.byId(Long.MAX_VALUE)).hasStatus(NOT_FOUND);
  }

  @Test
  @Tag(REGRESSION)
  void oversizedPageIsRejected() {
    ClaimPageValidator.validate(claims.list(0, 51)).hasStatus(BAD_REQUEST);
  }
}
