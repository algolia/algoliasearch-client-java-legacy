package com.algolia.search.saas;

import com.algolia.search.saas.APIClient.IndexQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


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

/**
 * Contains all the functions related to one index
 * You should use APIClient.initIndex(indexName) to retrieve this object
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class Index {
  private static final long MAX_TIME_MS_TO_WAIT = 10000L;

  private APIClient client;
  private String encodedIndexName;
  private String indexName;

  /**
   * Index initialization (You should not call this yourself)
   */
  protected Index(APIClient client, String indexName) {
    try {
      this.client = client;
      this.encodedIndexName = URLEncoder.encode(indexName, "UTF-8");
      this.indexName = indexName;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return the underlying index name
   */
  public String getIndexName() {
    return indexName;
  }

  /**
   * Add an object in this index
   *
   * @param obj the object to add
   */
  public JSONObject addObject(JSONObject obj) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName, obj.toString(), true, false);
  }

  /**
   * Add an object in this index with a uniq identifier
   *
   * @param obj      the object to add
   * @param objectID the objectID associated to this object
   *                 (if this objectID already exist the old object will be overriden)
   */
  public JSONObject addObject(JSONObject obj, String objectID) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), obj.toString(), true);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Custom batch
   *
   * @param actions the array of actions
   */
  public JSONObject batch(JSONArray actions) throws AlgoliaException {
    return postBatch(actions);
  }

  /**
   * Custom batch
   *
   * @param actions the array of actions
   */
  public JSONObject batch(List<JSONObject> actions) throws AlgoliaException {
    return postBatch(actions);
  }

  private JSONObject postBatch(Object actions) throws AlgoliaException {
    try {
      JSONObject content = new JSONObject();
      content.put("requests", actions);
      return client.postRequest("/1/indexes/" + encodedIndexName + "/batch", content.toString(), true, false);
    } catch (JSONException e) {
      throw new AlgoliaException(e);
    }
  }

  /**
   * Add several objects
   *
   * @param objects the array of objects to add
   */
  public JSONObject addObjects(List<JSONObject> objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (JSONObject obj : objects) {
        JSONObject action = new JSONObject();
        action.put("action", "addObject");
        action.put("body", obj);
        array.put(action);
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Add several objects
   *
   * @param objects the array of objects to add
   */
  public JSONObject addObjects(JSONArray objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (int n = 0; n < objects.length(); n++) {
        JSONObject action = new JSONObject();
        action.put("action", "addObject");
        action.put("body", objects.getJSONObject(n));
        array.put(action);
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Get an object from this index. Return null if the object doens't exist.
   *
   * @param objectID the unique identifier of the object to retrieve
   */
  public JSONObject getObject(String objectID) throws AlgoliaException {
    try {
      return client.getRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), false);
    } catch (AlgoliaException e) {
      if (e.getCode() == 404) {
        return null;
      }
      throw e;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get an object from this index
   *
   * @param objectID             the unique identifier of the object to retrieve
   * @param attributesToRetrieve contains the list of attributes to retrieve.
   */
  public JSONObject getObject(String objectID, List<String> attributesToRetrieve) throws AlgoliaException {
    try {
      String params = encodeAttributes(attributesToRetrieve, true);
      return client.getRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8") + params.toString(), false);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get several objects from this index
   *
   * @param objectIDs the array of unique identifier of objects to retrieve
   */
  public JSONObject getObjects(List<String> objectIDs) throws AlgoliaException {
    return getObjects(objectIDs, null);
  }

  /**
   * Get several objects from this index
   *
   * @param objectIDs            the array of unique identifier of objects to retrieve
   * @param attributesToRetrieve contains the list of attributes to retrieve.
   */
  public JSONObject getObjects(List<String> objectIDs, List<String> attributesToRetrieve) throws AlgoliaException {
    try {
      JSONArray requests = new JSONArray();
      for (String id : objectIDs) {
        JSONObject request = new JSONObject();
        request.put("indexName", this.indexName);
        request.put("objectID", id);
        request.put("attributesToRetrieve", encodeAttributes(attributesToRetrieve, false));
        requests.put(request);
      }
      JSONObject body = new JSONObject();
      body.put("requests", requests);
      return client.postRequest("/1/indexes/*/objects", body.toString(), false, false);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    } catch (UnsupportedEncodingException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  private String encodeAttributes(List<String> attributesToRetrieve, boolean forURL) throws UnsupportedEncodingException {
    if (attributesToRetrieve == null) {
      return null;
    }

    StringBuilder params = new StringBuilder();
    if (forURL) {
      params.append("?attributes=");
    }
    for (int i = 0; i < attributesToRetrieve.size(); ++i) {
      if (i > 0) {
        params.append(",");
      }
      params.append(URLEncoder.encode(attributesToRetrieve.get(i), "UTF-8"));
    }
    return params.toString();
  }


  /**
   * Update partially an object (only update attributes passed in argument), create the object if it does not exist
   *
   * @param partialObject the object to override
   */
  public JSONObject partialUpdateObject(JSONObject partialObject, String objectID) throws AlgoliaException {
    return partialUpdateObject(partialObject, objectID, true);
  }

  /**
   * Update partially an object (only update attributes passed in argument), do nothing if object does not exist
   *
   * @param partialObject the object to override
   */
  public JSONObject partialUpdateObjectNoCreate(JSONObject partialObject, String objectID) throws AlgoliaException {
    return partialUpdateObject(partialObject, objectID, false);
  }

  private JSONObject partialUpdateObject(JSONObject partialObject, String objectID, Boolean createIfNotExists) throws AlgoliaException {
    String parameters = "";
    if (!createIfNotExists) {
      parameters = "?createIfNotExists=false";
    }
    try {
      return client.postRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8")
        + "/partial" + parameters, partialObject.toString(), true, false);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Partially Override the content of several objects
   *
   * @param objects the array of objects to update (each object must contains an objectID attribute)
   */
  public JSONObject partialUpdateObjects(JSONArray objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (int n = 0; n < objects.length(); n++) {
        array.put(partialUpdateObject(objects.getJSONObject(n)));
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Partially Override the content of several objects
   *
   * @param objects the array of objects to update (each object must contains an objectID attribute)
   */
  public JSONObject partialUpdateObjects(List<JSONObject> objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (JSONObject obj : objects) {
        array.put(partialUpdateObject(obj));
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  private JSONObject partialUpdateObject(JSONObject object) throws JSONException {
    JSONObject action = new JSONObject();
    action.put("action", "partialUpdateObject");
    action.put("objectID", object.getString("objectID"));
    action.put("body", object);
    return action;
  }

  /**
   * Override the content of object
   *
   * @param object the object to update
   */
  public JSONObject saveObject(JSONObject object, String objectID) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), object.toString(), true);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Override the content of several objects
   *
   * @param objects the array of objects to update (each object must contains an objectID attribute)
   */
  public JSONObject saveObjects(List<JSONObject> objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (JSONObject obj : objects) {
        JSONObject action = new JSONObject();
        action.put("action", "updateObject");
        action.put("objectID", obj.getString("objectID"));
        action.put("body", obj);
        array.put(action);
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Override the content of several objects
   *
   * @param objects the array of objects to update (each object must contains an objectID attribute)
   */
  public JSONObject saveObjects(JSONArray objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (int n = 0; n < objects.length(); n++) {
        JSONObject obj = objects.getJSONObject(n);
        JSONObject action = new JSONObject();
        action.put("action", "updateObject");
        action.put("objectID", obj.getString("objectID"));
        action.put("body", obj);
        array.put(action);
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Delete an object from the index
   *
   * @param objectID the unique identifier of object to delete
   */
  public JSONObject deleteObject(String objectID) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.deleteRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), true);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete all objects matching a query
   *
   * @param query the query string
   */
  public void deleteByQuery(Query query) throws AlgoliaException {
    deleteByQuery(query, 100000);
  }

  public void deleteByQuery(Query query, int batchLimit) throws AlgoliaException {
    List<String> attributesToRetrieve = new ArrayList<String>();
    attributesToRetrieve.add("objectID");
    query.setAttributesToRetrieve(attributesToRetrieve);
    query.setAttributesToHighlight(new ArrayList<String>());
    query.setAttributesToSnippet(new ArrayList<String>());
    query.setHitsPerPage(1000);
    query.enableDistinct(false);

    IndexBrowser it = this.browse(query);
    try {
      while (true) {
        List<String> objectIDs = new ArrayList<String>();
        while (it.hasNext()) {
          JSONObject elt = it.next();
          objectIDs.add(elt.getString("objectID"));
          if (objectIDs.size() > batchLimit) {
            break;
          }
        }
        JSONObject task = this.deleteObjects(objectIDs);
        this.waitTask(task.getString("taskID"));
        if (!it.hasNext())
          break;
      }
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Search inside the index
   */
  public JSONObject search(Query params) throws AlgoliaException {
    String paramsString = params.getQueryString();
    JSONObject body = new JSONObject();
    try {
      body.put("params", paramsString);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return client.postRequest("/1/indexes/" + encodedIndexName + "/query", body.toString(), false, true);
  }

  /**
   * Search into a facet value
   */
  public JSONObject searchInFacetValues(String facetName, String facetQuery, Query params) throws AlgoliaException {
    params = params == null ? new Query() : params;
    String paramsString = params.setFacetQuery(facetQuery).getQueryString();
    JSONObject body = new JSONObject();
    String encodedFacetName;
    try {
      encodedFacetName = URLEncoder.encode(facetName, "UTF-8");
      body.put("params", paramsString);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return client.postRequest("/1/indexes/" + encodedIndexName + "/facets/" + encodedFacetName + "/query", body.toString(), false, true);
  }

  /**
   * Delete several objects
   *
   * @param objects the array of objectIDs to delete
   */
  public JSONObject deleteObjects(List<String> objects) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (String id : objects) {
        JSONObject obj = new JSONObject();
        obj.put("objectID", id);
        JSONObject action = new JSONObject();
        action.put("action", "deleteObject");
        action.put("body", obj);
        array.put(action);
      }
      return batch(array);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * Browse all index content
   *
   * @param page Pagination parameter used to select the page to retrieve.
   *             Page is zero-based and defaults to 0. Thus, to retrieve the 10th page you need to set page=9
   * @deprecated Use the `browse(Query params)` version
   */
  public JSONObject browse(int page) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/browse?page=" + page, false);
  }

  /**
   * Browse all index content
   */
  public IndexBrowser browse(Query params) throws AlgoliaException {
    return new IndexBrowser(client, encodedIndexName, params, null);
  }

  /**
   * Browse all index content starting from a cursor
   */
  public IndexBrowser browseFrom(Query params, String cursor) throws AlgoliaException {
    return new IndexBrowser(client, encodedIndexName, params, cursor);
  }

  @Deprecated
  public IndexBrowser browseFrow(Query params, String cursor) throws AlgoliaException {
    return browseFrom(params, cursor);
  }

  /**
   * Browse all index content
   *
   * @param page        Pagination parameter used to select the page to retrieve.
   *                    Page is zero-based and defaults to 0. Thus, to retrieve the 10th page you need to set page=9
   * @param hitsPerPage Pagination parameter used to select the number of hits per page. Defaults to 1000.
   */
  public JSONObject browse(int page, int hitsPerPage) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/browse?page=" + page + "&hitsPerPage=" + hitsPerPage, false);
  }

  /**
   * Wait the publication of a task on the server.
   * All server task are asynchronous and you can check with this method that the task is published.
   *
   * @param taskID     the id of the task returned by server
   * @param timeToWait time to sleep seed
   */
  public void waitTask(String taskID, long timeToWait) throws AlgoliaException {
    try {
      while (true) {
        JSONObject obj = client.getRequest("/1/indexes/" + encodedIndexName + "/task/" + URLEncoder.encode(taskID, "UTF-8"), false);
        if (obj.getString("status").equals("published"))
          return;
        try {
          Thread.sleep(timeToWait);
        } catch (InterruptedException ignored) {
        }
        timeToWait *= 2;
        timeToWait = timeToWait > MAX_TIME_MS_TO_WAIT ? MAX_TIME_MS_TO_WAIT : timeToWait;
      }
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Wait the publication of a task on the server.
   * All server task are asynchronous and you can check with this method that the task is published.
   *
   * @param taskID the id of the task returned by server
   */
  public void waitTask(String taskID) throws AlgoliaException {
    waitTask(taskID, 100);
  }

  /**
   * Get settings of this index
   */
  public JSONObject getSettings() throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/settings?getVersion=2", false);
  }

  /**
   * Delete the index content without removing settings and index specific API keys.
   */
  public JSONObject clearIndex() throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/clear", "", true, false);
  }

  /**
   * Set settings for this index
   *
   * @param settings the settings object that can contains :
   *                 - minWordSizefor1Typo: (integer) the minimum number of characters to accept one typo (default = 3).
   *                 - minWordSizefor2Typos: (integer) the minimum number of characters to accept two typos (default = 7).
   *                 - hitsPerPage: (integer) the number of hits per page (default = 10).
   *                 - attributesToRetrieve: (array of strings) default list of attributes to retrieve in objects.
   *                 If set to null, all attributes are retrieved.
   *                 - attributesToHighlight: (array of strings) default list of attributes to highlight.
   *                 If set to null, all indexed attributes are highlighted.
   *                 - attributesToSnippet**: (array of strings) default list of attributes to snippet alongside the number of words to return (syntax is attributeName:nbWords).
   *                 By default no snippet is computed. If set to null, no snippet is computed.
   *                 - attributesToIndex: (array of strings) the list of fields you want to index.
   *                 If set to null, all textual and numerical attributes of your objects are indexed, but you should update it to get optimal results.
   *                 This parameter has two important uses:
   *                 - Limit the attributes to index: For example if you store a binary image in base64, you want to store it and be able to
   *                 retrieve it but you don't want to search in the base64 string.
   *                 - Control part of the ranking*: (see the ranking parameter for full explanation) Matches in attributes at the beginning of
   *                 the list will be considered more important than matches in attributes further down the list.
   *                 In one attribute, matching text at the beginning of the attribute will be considered more important than text after, you can disable
   *                 this behavior if you add your attribute inside `unordered(AttributeName)`, for example attributesToIndex: ["title", "unordered(text)"].
   *                 - attributesForFaceting: (array of strings) The list of fields you want to use for faceting.
   *                 All strings in the attribute selected for faceting are extracted and added as a facet. If set to null, no attribute is used for faceting.
   *                 - ranking: (array of strings) controls the way results are sorted.
   *                 We have six available criteria:
   *                 - typo: sort according to number of typos,
   *                 - geo: sort according to decreassing distance when performing a geo-location based search,
   *                 - proximity: sort according to the proximity of query words in hits,
   *                 - attribute: sort according to the order of attributes defined by attributesToIndex,
   *                 - exact: sort according to the number of words that are matched identical to query word (and not as a prefix),
   *                 - custom: sort according to a user defined formula set in **customRanking** attribute.
   *                 The standard order is ["typo", "geo", "proximity", "attribute", "exact", "custom"]
   *                 - customRanking: (array of strings) lets you specify part of the ranking.
   *                 The syntax of this condition is an array of strings containing attributes prefixed by asc (ascending order) or desc (descending order) operator.
   *                 For example `"customRanking" => ["desc(population)", "asc(name)"]`
   *                 - queryType: Select how the query words are interpreted, it can be one of the following value:
   *                 - prefixAll: all query words are interpreted as prefixes,
   *                 - prefixLast: only the last word is interpreted as a prefix (default behavior),
   *                 - prefixNone: no query word is interpreted as a prefix. This option is not recommended.
   *                 - highlightPreTag: (string) Specify the string that is inserted before the highlighted parts in the query result (default to "<em>").
   *                 - highlightPostTag: (string) Specify the string that is inserted after the highlighted parts in the query result (default to "</em>").
   *                 - optionalWords: (array of strings) Specify a list of words that should be considered as optional when found in the query.
   */
  public JSONObject setSettings(JSONObject settings) throws AlgoliaException {
    return setSettings(settings, false);
  }

  public JSONObject setSettings(JSONObject settings, Boolean forwardToReplicas) throws AlgoliaException {
    return client.putRequest("/1/indexes/" + encodedIndexName + "/settings?forwardToReplicas=" + forwardToReplicas.toString(), settings.toString(), true);
  }

  /**
   * Deprecated: use listApiKeys
   */
  @Deprecated
  public JSONObject listUserKeys() throws AlgoliaException {
    return listApiKeys();
  }

  /**
   * List all existing api keys with their associated ACLs
   */
  public JSONObject listApiKeys() throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/keys", false);
  }

  /**
   * Deprecated: use getApiKey
   */
  @Deprecated
  public JSONObject getUserKeyACL(String key) throws AlgoliaException {
    return getApiKey(key);
  }

  /**
   * Get ACL of an api key
   */
  public JSONObject getApiKey(String key) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/keys/" + key, false);
  }

  /**
   * Deprecated: use deleteApiKey
   */
  @Deprecated
  public JSONObject deleteUserKey(String key) throws AlgoliaException {
    return deleteApiKey(key);
  }

  /**
   * Delete an existing api key
   */
  public JSONObject deleteApiKey(String key) throws AlgoliaException {
    return client.deleteRequest("/1/indexes/" + encodedIndexName + "/keys/" + key, true);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(JSONObject params) throws AlgoliaException {
    return addApiKey(params);
  }

  /**
   * Create a new api key
   *
   * @param params the list of parameters for this key. Defined by a JSONObject that
   *               can contains the following values:
   *               - acl: array of string
   *               - indices: array of string
   *               - validity: int
   *               - referers: array of string
   *               - description: string
   *               - maxHitsPerQuery: integer
   *               - queryParameters: string
   *               - maxQueriesPerIPPerHour: integer
   */
  public JSONObject addApiKey(JSONObject params) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/keys", params.toString(), true, false);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(List<String> acls) throws AlgoliaException {
    return addApiKey(acls);
  }

  /**
   * Create a new api key
   *
   * @param acls the list of ACL for this key. Defined by an array of strings that
   *             can contains the following values:
   *             - search: allow to search (https and http)
   *             - addObject: allows to add/update an object in the index (https only)
   *             - deleteObject : allows to delete an existing object (https only)
   *             - deleteIndex : allows to delete index content (https only)
   *             - settings : allows to get index settings (https only)
   *             - editSettings : allows to change index settings (https only)
   */
  public JSONObject addApiKey(List<String> acls) throws AlgoliaException {
    return addApiKey(acls, 0, 0, 0);
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, JSONObject params) throws AlgoliaException {
    return updateApiKey(key, params);
  }

  /**
   * Update a new api key
   *
   * @param params the list of parameters for this key. Defined by a JSONObject that
   *               can contains the following values:
   *               - acl: array of string
   *               - indices: array of string
   *               - validity: int
   *               - referers: array of string
   *               - description: string
   *               - maxHitsPerQuery: integer
   *               - queryParameters: string
   *               - maxQueriesPerIPPerHour: integer
   */
  public JSONObject updateApiKey(String key, JSONObject params) throws AlgoliaException {
    return client.putRequest("/1/indexes/" + encodedIndexName + "/keys/" + key, params.toString(), true);
  }

  /**
   * Deprecated: user updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, List<String> acls) throws AlgoliaException {
    return updateApiKey(key, acls);
  }

  /**
   * Update an api key
   *
   * @param acls the list of ACL for this key. Defined by an array of strings that
   *             can contains the following values:
   *             - search: allow to search (https and http)
   *             - addObject: allows to add/update an object in the index (https only)
   *             - deleteObject : allows to delete an existing object (https only)
   *             - deleteIndex : allows to delete index content (https only)
   *             - settings : allows to get index settings (https only)
   *             - editSettings : allows to change index settings (https only)
   */
  public JSONObject updateApiKey(String key, List<String> acls) throws AlgoliaException {
    return updateApiKey(key, acls, 0, 0, 0);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return addApiKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
  }

  /**
   * Create a new api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   */
  public JSONObject addApiKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    try {
      JSONObject jsonObject = generateUpdateUser(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
      return addApiKey(jsonObject);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return updateApiKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
  }

  /**
   * Update an api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   */
  public JSONObject updateApiKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    try {
      JSONObject jsonObject = generateUpdateUser(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
      return updateApiKey(key, jsonObject);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private JSONObject generateUpdateUser(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("acl", new JSONArray(acls));
    jsonObject.put("validity", validity);
    jsonObject.put("maxQueriesPerIPPerHour", maxQueriesPerIPPerHour);
    jsonObject.put("maxHitsPerQuery", maxHitsPerQuery);
    return jsonObject;
  }

  /**
   * Perform a search with disjunctive facets generating as many queries as number of disjunctive facets
   *
   * @param query             the query
   * @param disjunctiveFacets the array of disjunctive facets
   * @param refinements       Map<String, List<String>> representing the current refinements
   *                          ex: { "my_facet1" => ["my_value1", "my_value2"], "my_disjunctive_facet1" => ["my_value1", "my_value2"] }
   */
  public JSONObject searchDisjunctiveFaceting(Query query, List<String> disjunctiveFacets, Map<String, List<String>> refinements) throws AlgoliaException {
    if (refinements == null) {
      refinements = new HashMap<String, List<String>>();
    }
    HashMap<String, List<String>> disjunctiveRefinements = new HashMap<String, List<String>>();
    for (Map.Entry<String, List<String>> elt : refinements.entrySet()) {
      if (disjunctiveFacets.contains(elt.getKey())) {
        disjunctiveRefinements.put(elt.getKey(), elt.getValue());
      }
    }

    // build queries
    List<IndexQuery> queries = new ArrayList<IndexQuery>();
    // hits + regular facets query
    StringBuilder filters = new StringBuilder();
    boolean first_global = true;
    for (Map.Entry<String, List<String>> elt : refinements.entrySet()) {
      StringBuilder or = new StringBuilder();
      or.append("(");
      boolean first = true;
      for (String val : elt.getValue()) {
        if (disjunctiveRefinements.containsKey(elt.getKey())) {
          // disjunctive refinements are ORed
          if (!first) {
            or.append(',');
          }
          first = false;
          or.append(String.format("%s:%s", elt.getKey(), val));
        } else {
          if (!first_global) {
            filters.append(',');
          }
          first_global = false;
          filters.append(String.format("%s:%s", elt.getKey(), val));
        }
      }
      // Add or
      if (disjunctiveRefinements.containsKey(elt.getKey())) {
        or.append(')');
        if (!first_global) {
          filters.append(',');
        }
        first_global = false;
        filters.append(or.toString());
      }
    }

    queries.add(new IndexQuery(this.indexName, new Query(query).setFacetFilters(filters.toString())));
    // one query per disjunctive facet (use all refinements but the current one + hitsPerPage=1 + single facet
    for (String disjunctiveFacet : disjunctiveFacets) {
      filters = new StringBuilder();
      first_global = true;
      for (Map.Entry<String, List<String>> elt : refinements.entrySet()) {
        if (disjunctiveFacet.equals(elt.getKey())) {
          continue;
        }
        StringBuilder or = new StringBuilder();
        or.append("(");
        boolean first = true;
        for (String val : elt.getValue()) {
          if (disjunctiveRefinements.containsKey(elt.getKey())) {
            // disjunctive refinements are ORed
            if (!first) {
              or.append(',');
            }
            first = false;
            or.append(String.format("%s:%s", elt.getKey(), val));
          } else {
            if (!first_global) {
              filters.append(',');
            }
            first_global = false;
            filters.append(String.format("%s:%s", elt.getKey(), val));
          }
        }
        // Add or
        if (disjunctiveRefinements.containsKey(elt.getKey())) {
          or.append(')');
          if (!first_global) {
            filters.append(',');
          }
          first_global = false;
          filters.append(or.toString());
        }
      }
      List<String> facets = new ArrayList<String>();
      facets.add(disjunctiveFacet);
      queries.add(new IndexQuery(this.indexName, new Query(query).setHitsPerPage(0).enableAnalytics(false).setAttributesToRetrieve(new ArrayList<String>()).setAttributesToHighlight(new ArrayList<String>()).setAttributesToSnippet(new ArrayList<String>()).setFacets(facets).setFacetFilters(filters.toString())));
    }
    JSONObject answers = this.client.multipleQueries(queries);

    // aggregate answers
    // first answer stores the hits + regular facets
    try {
      JSONArray results = answers.getJSONArray("results");
      JSONObject aggregatedAnswer = results.getJSONObject(0);
      JSONObject disjunctiveFacetsJSON = new JSONObject();
      for (int i = 1; i < results.length(); ++i) {
        JSONObject facets = results.getJSONObject(i).getJSONObject("facets");
        @SuppressWarnings("unchecked")
        Iterator<String> keys = facets.keys();
        while (keys.hasNext()) {
          String key = keys.next();
          // Add the facet to the disjunctive facet hash
          disjunctiveFacetsJSON.put(key, facets.getJSONObject(key));
          // concatenate missing refinements
          if (!disjunctiveRefinements.containsKey(key)) {
            continue;
          }
          for (String refine : disjunctiveRefinements.get(key)) {
            if (!disjunctiveFacetsJSON.getJSONObject(key).has(refine)) {
              disjunctiveFacetsJSON.getJSONObject(key).put(refine, 0);
            }
          }
        }
      }
      aggregatedAnswer.put("disjunctiveFacets", disjunctiveFacetsJSON);
      return aggregatedAnswer;
    } catch (JSONException e) {
      throw new Error(e);
    }
  }

  public JSONObject searchDisjunctiveFaceting(Query query, List<String> disjunctiveFacets) throws AlgoliaException {
    return searchDisjunctiveFaceting(query, disjunctiveFacets, null);
  }

  /**
   * @param query the query
   */
  public JSONObject searchSynonyms(SynonymQuery query) throws AlgoliaException, JSONException {
    JSONObject body = new JSONObject().put("query", query.getQueryString());
    if (query.hasTypes()) {
      StringBuilder type = new StringBuilder();
      boolean first = true;
      for (SynonymQuery.SynonymType t : query.getTypes()) {
        if (!first) {
          type.append(",");
        }
        type.append(t.name);
        first = false;
      }
      body = body.put("type", type.toString());
    }
    if (query.getPage() != null) {
      body = body.put("page", query.getPage());
    }
    if (query.getHitsPerPage() != null) {
      body = body.put("hitsPerPage", query.getHitsPerPage());
    }

    return client.postRequest("/1/indexes/" + encodedIndexName + "/synonyms/search", body.toString(), false, true);
  }

  /**
   * Get one synonym
   *
   * @param objectID the objectId of the synonym to get
   */
  public JSONObject getSynonym(String objectID) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.getRequest("/1/indexes/" + encodedIndexName + "/synonyms/" + URLEncoder.encode(objectID, "UTF-8"), true);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete one synonym
   *
   * @param objectID          The objectId of the synonym to delete
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject deleteSynonym(String objectID, boolean forwardToReplicas) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.deleteRequest("/1/indexes/" + encodedIndexName + "/synonyms/" + URLEncoder.encode(objectID, "UTF-8") + "/?page=forwardToReplicas" + forwardToReplicas, false);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public JSONObject deleteSynonym(String objectID) throws AlgoliaException {
    return deleteSynonym(objectID, false);
  }

  /**
   * Delete all synonym set
   *
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject clearSynonyms(boolean forwardToReplicas) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/synonyms/clear?forwardToReplicas=" + forwardToReplicas, "", true, false);
  }

  public JSONObject clearSynonyms() throws AlgoliaException {
    return clearSynonyms(false);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects                 List of synonyms
   * @param forwardToReplicas       Forward the operation to the replica indices
   * @param replaceExistingSynonyms Replace the existing synonyms with this batch
   */
  public JSONObject batchSynonyms(List<JSONObject> objects, boolean forwardToReplicas, boolean replaceExistingSynonyms) throws AlgoliaException {
    JSONArray array = new JSONArray();
    for (JSONObject obj : objects) {
      array.put(obj);
    }

    return client.postRequest("/1/indexes/" + encodedIndexName + "/synonyms/batch?forwardToReplicas=" + forwardToReplicas + "&replaceExistingSynonyms=" + replaceExistingSynonyms, array.toString(), true, false);
  }

  public JSONObject batchSynonyms(List<JSONObject> objects, boolean forwardToReplicas) throws AlgoliaException {
    return batchSynonyms(objects, forwardToReplicas, false);
  }

  public JSONObject batchSynonyms(List<JSONObject> objects) throws AlgoliaException {
    return batchSynonyms(objects, false, false);
  }

  /**
   * Update one synonym
   *
   * @param objectID          The objectId of the synonym to save
   * @param content           The new content of this synonym
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject saveSynonym(String objectID, JSONObject content, boolean forwardToReplicas) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/synonyms/" + URLEncoder.encode(objectID, "UTF-8") + "?forwardToReplicas=" + forwardToReplicas, content.toString(), true);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public JSONObject saveSynonym(String objectID, JSONObject content) throws AlgoliaException {
    return saveSynonym(objectID, content, false);
  }

  /**
   * @deprecated See {@code IndexBrowser}
   */
  static class IndexBrower extends IndexBrowser {
    IndexBrower(APIClient client, String encodedIndexName, Query params, String startingCursor) throws AlgoliaException {
      super(client, encodedIndexName, params, startingCursor);
    }
  }

  /**
   * This class iterates over an index using the cursor-based browse mechanism
   */
  static public class IndexBrowser implements Iterator<JSONObject> {

    final APIClient client;
    final Query params;
    final String encodedIndexName;
    JSONObject answer;
    JSONObject hit;
    int pos;

    IndexBrowser(APIClient client, String encodedIndexName, Query params, String startingCursor) throws AlgoliaException {
      this.client = client;
      this.params = params;
      this.encodedIndexName = encodedIndexName;

      doQuery(startingCursor);
      this.pos = 0;
    }

    @Override
    public boolean hasNext() {
      try {
        return pos < answer.getJSONArray("hits").length()
          || answer.has("cursor") && !answer.getString("cursor").isEmpty();
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return false;
    }

    @Override
    public JSONObject next() {
      try {
        do {
          if (pos < answer.getJSONArray("hits").length()) {
            hit = answer.getJSONArray("hits").getJSONObject(pos);
            ++pos;
            break;
          }
          if (answer.has("cursor") && !answer.getString("cursor").isEmpty()) {
            pos = 0;
            doQuery(getCursor());
            continue;
          }
          return null;
        } while (true);
      } catch (JSONException e) {
        throw new IllegalStateException(e);
      } catch (AlgoliaException e) {
        throw new IllegalArgumentException(e);
      }
      return hit;
    }

    /**
     * @return the underlying cursor used by the enumeration
     */
    public String getCursor() {
      try {
        return answer != null && answer.has("cursor") ? answer.getString("cursor") : null;
      } catch (JSONException e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public void remove() {
      throw new IllegalStateException("Cannot remove while browsing");
    }

    private void doQuery(String cursor) throws AlgoliaException {
      String paramsString = params.getQueryString();
      if (cursor != null) {
        try {
          paramsString += (paramsString.length() > 0 ? "&" : "") + "cursor=" + URLEncoder.encode(cursor, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          throw new IllegalStateException(e);
        }
      }
      this.answer = client.getRequest("/1/indexes/" + encodedIndexName + "/browse" + ((paramsString.length() > 0) ? ("?" + paramsString) : ""), true);
    }
  }

}
