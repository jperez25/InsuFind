package com.isf.integrationtests.tests;

import static com.isf.integrationtests.constants.HealthGroups.LIVENESS;
import static com.isf.integrationtests.constants.HealthGroups.READINESS;
import static com.isf.integrationtests.constants.HealthStatus.UP;
import static com.isf.integrationtests.constants.Http.Status.OK;
import static com.isf.integrationtests.constants.TestGroups.SMOKE;
import static com.isf.integrationtests.validators.HealthValidator.validate;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class HealthTest extends BaseTest {

  @Test
  @Tag(SMOKE)
  void healthIsUp() {
    validate(health.health())
        .hasStatus(OK)
        .hasStatus(UP)
        .hasGroups(LIVENESS, READINESS);
  }
}
