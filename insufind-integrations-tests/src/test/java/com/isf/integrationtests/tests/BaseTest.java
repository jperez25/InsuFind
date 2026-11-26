package com.isf.integrationtests.tests;

import com.isf.integrationtests.clients.ClaimsClient;
import com.isf.integrationtests.clients.HealthClient;

/** Base for every API test: owns the clients. Subclasses hold the assertions. */
abstract class BaseTest {

  protected final HealthClient health = new HealthClient();
  protected final ClaimsClient claims = new ClaimsClient();
}
