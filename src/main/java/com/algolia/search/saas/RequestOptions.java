package com.algolia.search.saas;

import java.util.HashMap;
import java.util.Map;

public class RequestOptions {

  public static final RequestOptions empty = new RequestOptions();

  private final Map<String, String> headers = new HashMap<String, String>();
  private final Map<String, String> queryParams = new HashMap<String, String>();
  private String forwardedFor;

  public String getForwardedFor() {
    return forwardedFor;
  }

  public RequestOptions setForwardedFor(String forwardedFor) {
    this.forwardedFor = forwardedFor;
    return this;
  }

  public RequestOptions addExtraHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  public RequestOptions addExtraQueryParameters(String key, String value) {
    queryParams.put(key, value);
    return this;
  }

  public Map<String, String> generateExtraHeaders() {
    if (forwardedFor != null) {
      headers.put("X-Forwarded-For", forwardedFor);
    }
    return headers;
  }

  public Map<String, String> generateExtraQueryParams() {
    return queryParams;
  }

  @Override
  public String toString() {
    return "RequestOptions{" +
      "headers=" + headers +
      ", queryParams=" + queryParams +
      ", forwardedFor='" + forwardedFor + '\'' +
      '}';
  }
}
