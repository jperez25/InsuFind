package com.isf.integrationtests.clients;

import static com.isf.integrationtests.constants.Http.Param.PAGE;
import static com.isf.integrationtests.constants.Http.Param.QUERY;
import static com.isf.integrationtests.constants.Http.Param.SCOPE;
import static com.isf.integrationtests.constants.Http.Param.SIZE;
import static com.isf.integrationtests.constants.Http.Path.CLAIMS;
import static com.isf.integrationtests.constants.Http.Path.CLAIM_BY_ID;

import com.isf.integrationtests.constants.SearchScope;
import io.restassured.response.Response;

/** Client for /api/claims. Returns raw responses; tests own the assertions. */
public class ClaimsClient extends ApiClient {

  public Response search(String query, SearchScope scope, int page, int size) {
    return request()
        .queryParam(QUERY, query)
        .queryParam(SCOPE, scope)
        .queryParam(PAGE, page)
        .queryParam(SIZE, size)
        .get(CLAIMS);
  }

  /** List with the API defaults (no parameters). */
  public Response list() {
    return request().get(CLAIMS);
  }

  public Response list(int page, int size) {
    return request().queryParam(PAGE, page).queryParam(SIZE, size).get(CLAIMS);
  }

  public Response byId(long id) {
    return request().get(CLAIM_BY_ID, id);
  }
}
