package com.isf.integrationtests.validators;

import static org.assertj.core.api.Assertions.assertThat;

import com.isf.integrationtests.models.ClaimPageResponse;
import io.restassured.response.Response;

/** Checks for the paginated claim list/search response. */
public class ClaimPageValidator extends ResponseValidator<ClaimPageValidator> {

  private ClaimPageResponse page;

  private ClaimPageValidator(Response response) {
    super(response);
  }

  public static ClaimPageValidator validate(Response response) {
    return new ClaimPageValidator(response);
  }

  public ClaimPageValidator hasPage(int expected) {
    assertThat(page().page()).as("page").isEqualTo(expected);
    return this;
  }

  public ClaimPageValidator hasSize(int expected) {
    assertThat(page().size()).as("size").isEqualTo(expected);
    return this;
  }

  public ClaimPageValidator hasAtMostItems(int max) {
    assertThat(page().items()).as("items").hasSizeLessThanOrEqualTo(max);
    return this;
  }

  public ClaimPageValidator hasTotalItems() {
    assertThat(page().totalItems()).as("totalItems").isPositive();
    return this;
  }

  public ClaimPageValidator isEmpty() {
    assertThat(page().items()).as("items").isEmpty();
    assertThat(page().totalItems()).as("totalItems").isZero();
    return this;
  }

  public ClaimPageValidator insuredDriversContain(String text) {
    var names = page().items().stream().map(item -> item.insuredDriver().fullName()).toList();
    assertThat(names).as("insured driver names").isNotEmpty()
        .allMatch(name -> name.toLowerCase().contains(text.toLowerCase()));
    return this;
  }

  private ClaimPageResponse page() {
    if (page == null) {
      page = response.as(ClaimPageResponse.class);
    }
    return page;
  }
}
