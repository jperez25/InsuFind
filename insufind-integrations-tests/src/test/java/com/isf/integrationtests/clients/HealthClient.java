package com.isf.integrationtests.clients;

import static com.isf.integrationtests.constants.Http.Path.HEALTH;

import io.restassured.response.Response;

public class HealthClient extends ApiClient {

  public Response health() {
    return request().get(HEALTH);
  }
}
