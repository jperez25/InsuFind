package com.isf.integrationtests.validators;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;

/**
 * Fluent base: every check returns the validator so calls chain. Subclasses add checks for one response shape.
 * Bodies are mapped lazily, so a status-only check works on error responses.
 */
abstract class ResponseValidator<SELF extends ResponseValidator<SELF>> {

  protected final Response response;

  protected ResponseValidator(Response response) {
    this.response = response;
  }

  public SELF hasStatus(int expected) {
    assertThat(response.statusCode()).as("HTTP status").isEqualTo(expected);
    return self();
  }

  @SuppressWarnings("unchecked")
  private SELF self() {
    return (SELF) this;
  }
}
