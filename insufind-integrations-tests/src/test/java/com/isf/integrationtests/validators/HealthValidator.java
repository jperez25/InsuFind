package com.isf.integrationtests.validators;

import static org.assertj.core.api.Assertions.assertThat;

import com.isf.integrationtests.constants.HealthStatus;
import com.isf.integrationtests.models.HealthResponse;
import io.restassured.response.Response;

public class HealthValidator extends ResponseValidator<HealthValidator> {

  private final HealthResponse health;

  private HealthValidator(Response response) {
    super(response);
    this.health = response.as(HealthResponse.class);
  }

  public static HealthValidator validate(Response response) {
    return new HealthValidator(response);
  }

  public HealthValidator hasStatus(HealthStatus expected) {
    assertThat(health.status()).as("health status").isEqualTo(expected);
    return this;
  }

  public HealthValidator hasGroups(String... expected) {
    assertThat(health.groups()).as("health groups").containsExactlyInAnyOrder(expected);
    return this;
  }
}
