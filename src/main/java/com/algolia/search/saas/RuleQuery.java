package com.algolia.search.saas;

/*
 * Copyright (c) 2017 Algolia
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

@SuppressWarnings("WeakerAccess")
public class RuleQuery {

  private String query;
  private String anchoring = null;
  private String context = null;

  private Integer page = null;
  private Integer hitsPerPage = null;

  public RuleQuery(String query) {
    this.query = query;
  }

  public RuleQuery() {
    this(null);
  }

  public String getQuery() {
    return query;
  }

  /**
   * Set the full text query
   */
  public RuleQuery setQuery(String query) {
    this.query = query;
    return this;
  }

  public String getAnchoring() {
    return anchoring;
  }

  /**
   * Set the anchoring, restricts the search to rules with a specific anchoring type
   */
  public RuleQuery setAnchoring(String anchoring) {
    this.anchoring = anchoring;
    return this;
  }

  public String getContext() {
    return context;
  }

  /**
   * Set the context, restricts the search to rules with a specific context (exact match)
   */
  public RuleQuery setContext(String context) {
    this.context = context;
    return this;
  }

  public Integer getPage() {
    return page;
  }

  /**
   * Set the page to retrieve (zero base). Defaults to 0.
   */
  public RuleQuery setPage(Integer page) {
    this.page = page;
    return this;
  }

  public Integer getHitsPerPage() {
    return hitsPerPage;
  }

  /**
   * Set the number of hits per page. Defaults to 10.
   */
  public RuleQuery setHitsPerPage(Integer hitsPerPage) {
    this.hitsPerPage = hitsPerPage;
    return this;
  }
}
