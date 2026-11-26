package com.isf.integrationtests.constants;

/** HTTP vocabulary used by the API tests: status codes, headers, media types, routes and query parameters. */
public final class Http {

  private Http() {
  }

  public static final class Status {
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;

    private Status() {
    }
  }

  public static final class Header {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";

    private Header() {
    }
  }

  public static final class MediaType {
    public static final String JSON = "application/json";

    private MediaType() {
    }
  }

  public static final class Path {
    public static final String HEALTH = "/actuator/health";
    public static final String CLAIMS = "/api/claims";
    public static final String CLAIM_BY_ID = CLAIMS + "/{id}";

    private Path() {
    }
  }

  public static final class Param {
    public static final String QUERY = "q";
    public static final String SCOPE = "scope";
    public static final String PAGE = "page";
    public static final String SIZE = "size";

    private Param() {
    }
  }
}
