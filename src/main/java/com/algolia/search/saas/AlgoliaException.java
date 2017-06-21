package com.algolia.search.saas;

import java.util.List;

/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public class AlgoliaException extends Exception {

  private static final long serialVersionUID = 1L;
  private final int code;

  public AlgoliaException(Throwable cause) {
    super(cause);
    this.code = 0;
  }

  public AlgoliaException(String message) {
    this(0, message);
  }

  public AlgoliaException(int code, String message) {
    super(message);
    this.code = code;
  }

  public AlgoliaException(String message, Throwable cause) {
    super(message, cause);
    this.code = 0;
  }

  public static AlgoliaException from(String message, List<AlgoliaInnerException> errorsByHost) {
    AlgoliaInnerException lastException = null;
    if (!errorsByHost.isEmpty()) {
      lastException = errorsByHost.get(errorsByHost.size() - 1);
    }
    StringBuilder m = new StringBuilder(message);
    for (AlgoliaInnerException e : errorsByHost) {
      m
        .append("\n")
        .append(e.getHost())
        .append(": ")
        .append(e.getMessage());
    }

    return new AlgoliaException(m.toString(), lastException);
  }

  public int getCode() {
    return code;
  }
}
