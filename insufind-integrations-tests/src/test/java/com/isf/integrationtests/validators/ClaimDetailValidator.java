package com.isf.integrationtests.validators;

import static org.assertj.core.api.Assertions.assertThat;

import com.isf.integrationtests.models.ClaimDetailResponse;
import com.isf.integrationtests.models.ClaimSummaryResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;

public class ClaimDetailValidator extends ResponseValidator<ClaimDetailValidator> {

  private ClaimDetailResponse detail;

  private ClaimDetailValidator(Response response) {
    super(response);
  }

  public static ClaimDetailValidator validate(Response response) {
    return new ClaimDetailValidator(response);
  }

  public ClaimDetailValidator hasId(long expected) {
    assertThat(detail().id()).as("claim id").isEqualTo(expected);
    return this;
  }

  /** The detail repeats every field the list shows; they must agree. */
  public ClaimDetailValidator matches(ClaimSummaryResponse summary) {
    SoftAssertions.assertSoftly(soft -> {
      soft.assertThat(detail().id()).as("id").isEqualTo(summary.id());
      soft.assertThat(detail().claimNumber()).as("claimNumber").isEqualTo(summary.claimNumber());
      soft.assertThat(detail().incidentDate()).as("incidentDate").isEqualTo(summary.incidentDate());
      soft.assertThat(detail().status()).as("status").isEqualTo(summary.status());
      soft.assertThat(detail().insuredDriver()).as("insuredDriver").isEqualTo(summary.insuredDriver());
      soft.assertThat(detail().otherDrivers()).as("otherDrivers").isEqualTo(summary.otherDrivers());
    });
    return this;
  }

  private ClaimDetailResponse detail() {
    if (detail == null) {
      detail = response.as(ClaimDetailResponse.class);
    }
    return detail;
  }
}
