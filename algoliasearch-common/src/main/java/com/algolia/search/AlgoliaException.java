package com.algolia.search;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class AlgoliaException extends Exception {

  /**
   * HTTP response code of the Algolia API
   */
  private int httpResponseCode;

  /**
   * List of exception if all retries failed
   */
  private List<IOException> ioExceptionList;

  public AlgoliaException(int httpResponseCode, String message) {
    super(message);
    this.httpResponseCode = httpResponseCode;
  }

  public AlgoliaException(String message) {
    super(message);
  }

  public AlgoliaException(String message, Exception e) {
    super(message, e);
  }

  public AlgoliaException(String message, List<IOException> ioExceptionList) {
    super(message + ", exceptions: [" + String.join(",", ioExceptionList.stream().map(Throwable::getMessage).collect(Collectors.toList())) + "]");
    this.ioExceptionList = ioExceptionList;
  }

  @SuppressWarnings("unused")
  public int getHttpResponseCode() {
    return httpResponseCode;
  }

  @SuppressWarnings("unused")
  public List<IOException> getIoExceptionList() {
    return ioExceptionList;
  }
}
