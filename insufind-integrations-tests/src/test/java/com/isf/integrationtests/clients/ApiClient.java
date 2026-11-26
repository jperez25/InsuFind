package com.isf.integrationtests.clients;

import static com.isf.integrationtests.constants.Http.Header.ACCEPT;
import static com.isf.integrationtests.constants.Http.MediaType.JSON;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

/** Base for API clients: owns the base URL (API_URL env var, default http://localhost:8080) and common request setup. */
abstract class ApiClient {

  private static final String API_URL = System.getenv().getOrDefault("API_URL", "http://localhost:8080");

  private static final RequestSpecification SPEC = new RequestSpecBuilder()
      .setBaseUri(API_URL)
      .setConfig(RestAssuredConfig.config()
          .objectMapperConfig(new ObjectMapperConfig().defaultObjectMapper(new JacksonObjectMapper())))
      .addHeader(ACCEPT, JSON)
      .log(LogDetail.URI)
      .build();

  /** A fresh request built on the shared spec. */
  protected RequestSpecification request() {
    return RestAssured.given().spec(SPEC);
  }
}
