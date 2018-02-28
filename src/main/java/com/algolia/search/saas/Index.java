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
    return this.addObject(obj, RequestOptions.empty);
  }

  /**
   * Add an object in this index
   *
   * @param obj            the object to add
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addObject(JSONObject obj, RequestOptions requestOptions) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName, obj.toString(), true, false, requestOptions);
  }

  /**
   * Add an object in this index with a uniq identifier
   *
   * @param obj      the object to add
   * @param objectID the objectID associated to this object
   *                 (if this objectID already exist the old object will be overriden)
   */
  public JSONObject addObject(JSONObject obj, String objectID) throws AlgoliaException {
    return this.addObject(obj, objectID, RequestOptions.empty);
  }

  /**
   * Add an object in this index with a uniq identifier
   *
   * @param obj            the object to add
   * @param objectID       the objectID associated to this object
   *                       (if this objectID already exist the old object will be overriden)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addObject(JSONObject obj, String objectID, RequestOptions requestOptions) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), obj.toString(), requestOptions);
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
    return postBatch(actions, RequestOptions.empty);
  }

  /**
   * Custom batch
   *
   * @param actions        the array of actions
   * @param requestOptions Options to pass to this request
   */
  public JSONObject batch(JSONArray actions, RequestOptions requestOptions) throws AlgoliaException {
    return postBatch(actions, requestOptions);
  }

  /**
   * Custom batch
   *
   * @param actions the array of actions
   */
  public JSONObject batch(List<JSONObject> actions) throws AlgoliaException {
    return postBatch(actions, RequestOptions.empty);
  }

  /**
   * Custom batch
   *
   * @param actions        the array of actions
   * @param requestOptions Options to pass to this request
   */
  public JSONObject batch(List<JSONObject> actions, RequestOptions requestOptions) throws AlgoliaException {
    return postBatch(actions, requestOptions);
  }

  private JSONObject postBatch(Object actions, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONObject content = new JSONObject();
      content.put("requests", actions);
      return client.postRequest("/1/indexes/" + encodedIndexName + "/batch", content.toString(), true, false, requestOptions);
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
    return this.addObjects(objects, RequestOptions.empty);
  }

  /**
   * Add several objects
   *
   * @param objects        the array of objects to add
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addObjects(List<JSONObject> objects, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (JSONObject obj : objects) {
        JSONObject action = new JSONObject();
        action.put("action", "addObject");
        action.put("body", obj);
        array.put(action);
      }
      return batch(array, requestOptions);
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
    return this.addObjects(objects, RequestOptions.empty);
  }

  /**
   * Add several objects
   *
   * @param objects        the array of objects to add
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addObjects(JSONArray objects, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (int n = 0; n < objects.length(); n++) {
        JSONObject action = new JSONObject();
        action.put("action", "addObject");
        action.put("body", objects.getJSONObject(n));
        array.put(action);
      }
      return batch(array, requestOptions);
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
    return this.getObject(objectID, RequestOptions.empty);
  }

  /**
   * Get an object from this index. Return null if the object doens't exist.
   *
   * @param objectID       the unique identifier of the object to retrieve
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getObject(String objectID, RequestOptions requestOptions) throws AlgoliaException {
    try {
      return client.getRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), false, requestOptions);
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
    return this.getObject(objectID, attributesToRetrieve, RequestOptions.empty);
  }

  /**
   * Get an object from this index
   *
   * @param objectID             the unique identifier of the object to retrieve
   * @param attributesToRetrieve contains the list of attributes to retrieve.
   * @param requestOptions       Options to pass to this request
   */
  public JSONObject getObject(String objectID, List<String> attributesToRetrieve, RequestOptions requestOptions) throws AlgoliaException {
    try {
      String params = encodeAttributes(attributesToRetrieve, true);
      return client.getRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8") + params.toString(), false, requestOptions);
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
    return getObjects(objectIDs, null, RequestOptions.empty);
  }

  /**
   * Get several objects from this index
   *
   * @param objectIDs      the array of unique identifier of objects to retrieve
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getObjects(List<String> objectIDs, RequestOptions requestOptions) throws AlgoliaException {
    return getObjects(objectIDs, null, requestOptions);
  }

  /**
   * Get several objects from this index
   *
   * @param objectIDs            the array of unique identifier of objects to retrieve
   * @param attributesToRetrieve contains the list of attributes to retrieve.
   */
  public JSONObject getObjects(List<String> objectIDs, List<String> attributesToRetrieve) throws AlgoliaException {
    return this.getObjects(objectIDs, attributesToRetrieve, RequestOptions.empty);
  }

  /**
   * Get several objects from this index
   *
   * @param objectIDs            the array of unique identifier of objects to retrieve
   * @param attributesToRetrieve contains the list of attributes to retrieve.
   * @param requestOptions       Options to pass to this request
   */
  public JSONObject getObjects(List<String> objectIDs, List<String> attributesToRetrieve, RequestOptions requestOptions) throws AlgoliaException {
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
      return client.postRequest("/1/indexes/*/objects", body.toString(), false, false, requestOptions);
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
    return partialUpdateObject(partialObject, objectID, true, RequestOptions.empty);
  }

  /**
   * Update partially an object (only update attributes passed in argument), create the object if it does not exist
   *
   * @param partialObject  the object to override
   * @param requestOptions Options to pass to this request
   */
  public JSONObject partialUpdateObject(JSONObject partialObject, String objectID, RequestOptions requestOptions) throws AlgoliaException {
    return partialUpdateObject(partialObject, objectID, true, requestOptions);
  }

  /**
   * Update partially an object (only update attributes passed in argument), do nothing if object does not exist
   *
   * @param partialObject the object to override
   */
  public JSONObject partialUpdateObjectNoCreate(JSONObject partialObject, String objectID) throws AlgoliaException {
    return partialUpdateObject(partialObject, objectID, false, RequestOptions.empty);
  }

  /**
   * Update partially an object (only update attributes passed in argument), do nothing if object does not exist
   *
   * @param partialObject  the object to override
   * @param requestOptions Options to pass to this request
   */
  public JSONObject partialUpdateObjectNoCreate(JSONObject partialObject, String objectID, RequestOptions requestOptions) throws AlgoliaException {
    return partialUpdateObject(partialObject, objectID, false, requestOptions);
  }

  private JSONObject partialUpdateObject(JSONObject partialObject, String objectID, Boolean createIfNotExists, RequestOptions requestOptions) throws AlgoliaException {
    String parameters = "";
    if (!createIfNotExists) {
      parameters = "?createIfNotExists=false";
    }
    try {
      return client.postRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8")
        + "/partial" + parameters, partialObject.toString(), true, false, requestOptions);
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
    return this.partialUpdateObjects(objects, RequestOptions.empty);
  }

  /**
   * Partially Override the content of several objects
   *
   * @param objects        the array of objects to update (each object must contains an objectID attribute)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject partialUpdateObjects(JSONArray objects, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (int n = 0; n < objects.length(); n++) {
        array.put(partialUpdateObject(objects.getJSONObject(n)));
      }
      return batch(array, requestOptions);
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

  /**
   * Partially Override the content of several objects
   *
   * @param objects        the array of objects to update (each object must contains an objectID attribute)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject partialUpdateObjects(List<JSONObject> objects, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (JSONObject obj : objects) {
        array.put(partialUpdateObject(obj));
      }
      return batch(array, requestOptions);
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
    return this.saveObject(object, objectID, RequestOptions.empty);
  }

  /**
   * Override the content of object
   *
   * @param object         the object to update
   * @param requestOptions Options to pass to this request
   */
  public JSONObject saveObject(JSONObject object, String objectID, RequestOptions requestOptions) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), object.toString(), requestOptions);
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
    return this.saveObjects(objects, RequestOptions.empty);
  }

  /**
   * Override the content of several objects
   *
   * @param objects        the array of objects to update (each object must contains an objectID attribute)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject saveObjects(List<JSONObject> objects, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONArray array = new JSONArray();
      for (JSONObject obj : objects) {
        JSONObject action = new JSONObject();
        action.put("action", "updateObject");
        action.put("objectID", obj.getString("objectID"));
        action.put("body", obj);
        array.put(action);
      }
      return batch(array, requestOptions);
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
    return this.saveObjects(objects, RequestOptions.empty);
  }

  /**
   * Override the content of several objects
   *
   * @param objects        the array of objects to update (each object must contains an objectID attribute)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject saveObjects(JSONArray objects, RequestOptions requestOptions) throws AlgoliaException {
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
      return batch(array, requestOptions);
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
    return this.deleteObject(objectID, RequestOptions.empty);
  }

  /**
   * Delete an object from the index
   *
   * @param objectID       the unique identifier of object to delete
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteObject(String objectID, RequestOptions requestOptions) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.deleteRequest("/1/indexes/" + encodedIndexName + "/" + URLEncoder.encode(objectID, "UTF-8"), requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete all objects matching a query
   *
   * @param query the query string
   */
  public JSONObject deleteBy(Query query) throws AlgoliaException {
    return deleteBy(query, RequestOptions.empty);
  }

  /**
   * Delete all objects matching a query
   *
   * @param query          the query string
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteBy(Query query, RequestOptions requestOptions) throws AlgoliaException {
    String paramsString = query.getQueryString();
    JSONObject body = new JSONObject();
    try {
      body.put("params", paramsString);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return client.postRequest("/1/indexes/" + encodedIndexName + "/deleteByQuery", body.toString(), false, false, requestOptions);
  }

  /**
   * Delete all objects matching a query
   *
   * @param query the query string
   * @deprecated use deleteBy
   */
  @Deprecated
  public void deleteByQuery(Query query) throws AlgoliaException {
    deleteByQuery(query, 100000);
  }

  /**
   * Delete all objects matching a query
   *
   * @param query          the query string
   * @param requestOptions Options to pass to this request
   * @deprecated use deleteBy
   */
  @Deprecated
  public void deleteByQuery(Query query, RequestOptions requestOptions) throws AlgoliaException {
    this.deleteByQuery(query, 100000, requestOptions);
  }

  /**
   * @throws AlgoliaException
   * @deprecated use deleteBy
   */
  @Deprecated
  public void deleteByQuery(Query query, int batchLimit, RequestOptions requestOptions) throws AlgoliaException {
    List<String> attributesToRetrieve = new ArrayList<String>();
    attributesToRetrieve.add("objectID");
    query.setAttributesToRetrieve(attributesToRetrieve);
    query.setAttributesToHighlight(new ArrayList<String>());
    query.setAttributesToSnippet(new ArrayList<String>());
    query.setHitsPerPage(1000);
    query.enableDistinct(false);

    IndexBrowser it = this.browse(query, requestOptions);
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
        this.waitTask(task.getLong("taskID"));
        if (!it.hasNext())
          break;
      }
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  /**
   * @throws AlgoliaException
   * @deprecated use deleteBy
   */
  @Deprecated
  public void deleteByQuery(Query query, int batchLimit) throws AlgoliaException {
    this.deleteByQuery(query, batchLimit, RequestOptions.empty);
  }

  /**
   * Search inside the index
   *
   * @param the query to search
   */
  public JSONObject search(Query params) throws AlgoliaException {
    return this.search(params, RequestOptions.empty);
  }

  /**
   * Search inside the index
   *
   * @param the            query to search
   * @param requestOptions Options to pass to this request
   */
  public JSONObject search(Query params, RequestOptions requestOptions) throws AlgoliaException {
    String paramsString = params.getQueryString();
    JSONObject body = new JSONObject();
    try {
      body.put("params", paramsString);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return client.postRequest("/1/indexes/" + encodedIndexName + "/query", body.toString(), false, true, requestOptions);
  }

  /**
   * Search into a facet value
   */
  public JSONObject searchInFacetValues(String facetName, String facetQuery, Query params) throws AlgoliaException {
    return this.searchInFacetValues(facetName, facetQuery, params, RequestOptions.empty);
  }

  /**
   * Search into a facet value
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject searchInFacetValues(String facetName, String facetQuery, Query params, RequestOptions requestOptions) throws AlgoliaException {
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
    return client.postRequest("/1/indexes/" + encodedIndexName + "/facets/" + encodedFacetName + "/query", body.toString(), false, true, requestOptions);
  }

  /**
   * Delete several objects
   *
   * @param objects the array of objectIDs to delete
   */
  public JSONObject deleteObjects(List<String> objects) throws AlgoliaException {
    return this.deleteObjects(objects, RequestOptions.empty);
  }

  /**
   * Delete several objects
   *
   * @param objects        the array of objectIDs to delete
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteObjects(List<String> objects, RequestOptions requestOptions) throws AlgoliaException {
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
      return batch(array, requestOptions);
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
    return client.getRequest("/1/indexes/" + encodedIndexName + "/browse?page=" + page, false, RequestOptions.empty);
  }

  /**
   * Browse all index content
   */
  public IndexBrowser browse(Query params) throws AlgoliaException {
    return new IndexBrowser(client, encodedIndexName, params, null, RequestOptions.empty);
  }

  /**
   * Browse all index content
   *
   * @param requestOptions Options to pass to this request
   */
  public IndexBrowser browse(Query params, RequestOptions requestOptions) throws AlgoliaException {
    return new IndexBrowser(client, encodedIndexName, params, null, requestOptions);
  }

  /**
   * Browse all index content starting from a cursor
   */
  public IndexBrowser browseFrom(Query params, String cursor) throws AlgoliaException {
    return new IndexBrowser(client, encodedIndexName, params, cursor, RequestOptions.empty);
  }

  /**
   * Browse all index content starting from a cursor
   *
   * @param requestOptions Options to pass to this request
   */
  public IndexBrowser browseFrom(Query params, String cursor, RequestOptions requestOptions) throws AlgoliaException {
    return new IndexBrowser(client, encodedIndexName, params, cursor, requestOptions);
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
    return this.browse(page, hitsPerPage, RequestOptions.empty);
  }

  /**
   * Browse all index content
   *
   * @param page           Pagination parameter used to select the page to retrieve.
   *                       Page is zero-based and defaults to 0. Thus, to retrieve the 10th page you need to set page=9
   * @param hitsPerPage    Pagination parameter used to select the number of hits per page. Defaults to 1000.
   * @param requestOptions Options to pass to this request
   */
  public JSONObject browse(int page, int hitsPerPage, RequestOptions requestOptions) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/browse?page=" + page + "&hitsPerPage=" + hitsPerPage, false, requestOptions);
  }

  /**
   * Wait the publication of a task on the server.
   * All server task are asynchronous and you can check with this method that the task is published.
   *
   * @param taskID     the id of the task returned by server
   * @param timeToWait time to sleep seed
   */
  public void waitTask(String taskID, long timeToWait) throws AlgoliaException {
    this.waitTask(taskID, timeToWait, RequestOptions.empty);
  }

  /**
   * Wait the publication of a task on the server.
   * All server task are asynchronous and you can check with this method that the task is published.
   *
   * @param taskID         the id of the task returned by server
   * @param timeToWait     time to sleep seed
   * @param requestOptions Options to pass to this request
   */
  public void waitTask(String taskID, long timeToWait, RequestOptions requestOptions) throws AlgoliaException {
    try {
      while (true) {
        JSONObject obj = client.getRequest("/1/indexes/" + encodedIndexName + "/task/" + URLEncoder.encode(taskID, "UTF-8"), false, requestOptions);
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
   * Wait the publication of a task on the server.
   * All server task are asynchronous and you can check with this method that the task is published.
   *
   * @param taskID the id of the task returned by server
   */
  public void waitTask(Long taskID) throws AlgoliaException {
    waitTask(taskID.toString(), 100);
  }

  /**
   * Get settings of this index
   */
  public JSONObject getSettings() throws AlgoliaException {
    return this.getSettings(RequestOptions.empty);
  }

  /**
   * Get settings of this index
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getSettings(RequestOptions requestOptions) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/settings?getVersion=2", false, requestOptions);
  }

  /**
   * Delete the index content without removing settings and index specific API keys.
   */
  public JSONObject clearIndex() throws AlgoliaException {
    return this.clearIndex(RequestOptions.empty);
  }

  /**
   * Delete the index content without removing settings and index specific API keys.
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject clearIndex(RequestOptions requestOptions) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/clear", "", true, false, requestOptions);
  }

  /**
   * Set settings for this index
   *
   * @param settings the settings for an index
   */
  public JSONObject setSettings(JSONObject settings) throws AlgoliaException {
    return setSettings(settings, false);
  }

  /**
   * Set settings for this index
   *
   * @param settings       the settings for an index
   * @param requestOptions Options to pass to this request
   */
  public JSONObject setSettings(JSONObject settings, RequestOptions requestOptions) throws AlgoliaException {
    return setSettings(settings, false, requestOptions);
  }


  public JSONObject setSettings(JSONObject settings, Boolean forwardToReplicas) throws AlgoliaException {
    return this.setSettings(settings, forwardToReplicas, RequestOptions.empty);
  }

  public JSONObject setSettings(JSONObject settings, Boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    return client.putRequest("/1/indexes/" + encodedIndexName + "/settings?forwardToReplicas=" + forwardToReplicas.toString(), settings.toString(), requestOptions);
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
    return this.listApiKeys(RequestOptions.empty);
  }

  /**
   * List all existing api keys with their associated ACLs
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject listApiKeys(RequestOptions requestOptions) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/keys", false, requestOptions);
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
    return this.getApiKey(key, RequestOptions.empty);
  }

  /**
   * Get ACL of an api key
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getApiKey(String key, RequestOptions requestOptions) throws AlgoliaException {
    return client.getRequest("/1/indexes/" + encodedIndexName + "/keys/" + key, false, requestOptions);
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
    return this.deleteApiKey(key, RequestOptions.empty);
  }

  /**
   * Delete an existing api key
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteApiKey(String key, RequestOptions requestOptions) throws AlgoliaException {
    return client.deleteRequest("/1/indexes/" + encodedIndexName + "/keys/" + key, requestOptions);
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
    return this.addApiKey(params, RequestOptions.empty);
  }

  /**
   * Create a new api key
   *
   * @param params         the list of parameters for this key. Defined by a JSONObject that
   *                       can contains the following values:
   *                       - acl: array of string
   *                       - indices: array of string
   *                       - validity: int
   *                       - referers: array of string
   *                       - description: string
   *                       - maxHitsPerQuery: integer
   *                       - queryParameters: string
   *                       - maxQueriesPerIPPerHour: integer
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addApiKey(JSONObject params, RequestOptions requestOptions) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/keys", params.toString(), true, false, requestOptions);
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
    return this.updateApiKey(key, params, RequestOptions.empty);
  }

  /**
   * Update a new api key
   *
   * @param params         the list of parameters for this key. Defined by a JSONObject that
   *                       can contains the following values:
   *                       - acl: array of string
   *                       - indices: array of string
   *                       - validity: int
   *                       - referers: array of string
   *                       - description: string
   *                       - maxHitsPerQuery: integer
   *                       - queryParameters: string
   *                       - maxQueriesPerIPPerHour: integer
   * @param requestOptions Options to pass to this request
   */
  public JSONObject updateApiKey(String key, JSONObject params, RequestOptions requestOptions) throws AlgoliaException {
    return client.putRequest("/1/indexes/" + encodedIndexName + "/keys/" + key, params.toString(), requestOptions);
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
    return this.addApiKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, RequestOptions.empty);
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
   * @param requestOptions         Options to pass to this request
   */
  public JSONObject addApiKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONObject jsonObject = generateUpdateUser(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
      return addApiKey(jsonObject, requestOptions);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return updateApiKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, RequestOptions.empty);
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
   * @param requestOptions         Options to pass to this request
   */
  public JSONObject updateApiKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONObject jsonObject = generateUpdateUser(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
      return updateApiKey(key, jsonObject, requestOptions);
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
   * Search for synonyms
   *
   * @param query the query
   */
  public JSONObject searchSynonyms(SynonymQuery query) throws AlgoliaException, JSONException {
    return this.searchSynonyms(query, RequestOptions.empty);
  }

  /**
   * Search for synonyms
   *
   * @param query          the query
   * @param requestOptions Options to pass to this request
   */
  public JSONObject searchSynonyms(SynonymQuery query, RequestOptions requestOptions) throws AlgoliaException, JSONException {
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

    return client.postRequest("/1/indexes/" + encodedIndexName + "/synonyms/search", body.toString(), false, true, requestOptions);
  }

  /**
   * Get one synonym
   *
   * @param objectID the objectId of the synonym to get
   */
  public JSONObject getSynonym(String objectID) throws AlgoliaException {
    return this.getSynonym(objectID, RequestOptions.empty);
  }

  /**
   * Get one synonym
   *
   * @param objectID       the objectId of the synonym to get
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getSynonym(String objectID, RequestOptions requestOptions) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.getRequest("/1/indexes/" + encodedIndexName + "/synonyms/" + URLEncoder.encode(objectID, "UTF-8"), true, requestOptions);
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
    return this.deleteSynonym(objectID, forwardToReplicas, RequestOptions.empty);
  }

  /**
   * Delete one synonym
   *
   * @param objectID          The objectId of the synonym to delete
   * @param forwardToReplicas Forward the operation to the replica indices
   * @param requestOptions    Options to pass to this request
   */
  public JSONObject deleteSynonym(String objectID, boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.deleteRequest("/1/indexes/" + encodedIndexName + "/synonyms/" + URLEncoder.encode(objectID, "UTF-8") + "/?page=forwardToReplicas" + forwardToReplicas, requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete one synonym
   *
   * @param objectID The objectId of the synonym to delete
   */
  public JSONObject deleteSynonym(String objectID) throws AlgoliaException {
    return deleteSynonym(objectID, false);
  }


  /**
   * Delete one synonym
   *
   * @param objectID       The objectId of the synonym to delete
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteSynonym(String objectID, RequestOptions requestOptions) throws AlgoliaException {
    return deleteSynonym(objectID, false, requestOptions);
  }

  /**
   * Delete all synonym set
   *
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject clearSynonyms(boolean forwardToReplicas) throws AlgoliaException {
    return this.clearSynonyms(forwardToReplicas, RequestOptions.empty);
  }

  /**
   * Delete all synonym set
   *
   * @param forwardToReplicas Forward the operation to the replica indices
   * @param requestOptions    Options to pass to this request
   */
  public JSONObject clearSynonyms(boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/synonyms/clear?forwardToReplicas=" + forwardToReplicas, "", true, false, requestOptions);
  }

  /**
   * Delete all synonym set
   */
  public JSONObject clearSynonyms() throws AlgoliaException {
    return clearSynonyms(false);
  }

  /**
   * Delete all synonym set
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject clearSynonyms(RequestOptions requestOptions) throws AlgoliaException {
    return clearSynonyms(false, requestOptions);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects                 List of synonyms
   * @param forwardToReplicas       Forward the operation to the replica indices
   * @param replaceExistingSynonyms Replace the existing synonyms with this batch
   */
  public JSONObject batchSynonyms(List<JSONObject> objects, boolean forwardToReplicas, boolean replaceExistingSynonyms) throws AlgoliaException {
    return this.batchSynonyms(objects, forwardToReplicas, replaceExistingSynonyms, RequestOptions.empty);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects                 List of synonyms
   * @param forwardToReplicas       Forward the operation to the replica indices
   * @param replaceExistingSynonyms Replace the existing synonyms with this batch
   * @param requestOptions          Options to pass to this request
   */
  public JSONObject batchSynonyms(List<JSONObject> objects, boolean forwardToReplicas, boolean replaceExistingSynonyms, RequestOptions requestOptions) throws AlgoliaException {
    JSONArray array = new JSONArray();
    for (JSONObject obj : objects) {
      array.put(obj);
    }

    return client.postRequest("/1/indexes/" + encodedIndexName + "/synonyms/batch?forwardToReplicas=" + forwardToReplicas + "&replaceExistingSynonyms=" + replaceExistingSynonyms, array.toString(), true, false, requestOptions);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects           List of synonyms
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject batchSynonyms(List<JSONObject> objects, boolean forwardToReplicas) throws AlgoliaException {
    return batchSynonyms(objects, forwardToReplicas, false);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects           List of synonyms
   * @param forwardToReplicas Forward the operation to the replica indices
   * @param requestOptions    Options to pass to this request
   */
  public JSONObject batchSynonyms(List<JSONObject> objects, boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    return batchSynonyms(objects, forwardToReplicas, false, requestOptions);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects List of synonyms
   */
  public JSONObject batchSynonyms(List<JSONObject> objects) throws AlgoliaException {
    return batchSynonyms(objects, false, false);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param objects        List of synonyms
   * @param requestOptions Options to pass to this request
   */
  public JSONObject batchSynonyms(List<JSONObject> objects, RequestOptions requestOptions) throws AlgoliaException {
    return batchSynonyms(objects, false, false, requestOptions);
  }

  /**
   * Update one synonym
   *
   * @param objectID          The objectId of the synonym to save
   * @param content           The new content of this synonym
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject saveSynonym(String objectID, JSONObject content, boolean forwardToReplicas) throws AlgoliaException {
    return this.saveSynonym(objectID, content, forwardToReplicas, RequestOptions.empty);
  }

  /**
   * Update one synonym
   *
   * @param objectID          The objectId of the synonym to save
   * @param content           The new content of this synonym
   * @param forwardToReplicas Forward the operation to the replica indices
   * @param requestOptions    Options to pass to this request
   */
  public JSONObject saveSynonym(String objectID, JSONObject content, boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/synonyms/" + URLEncoder.encode(objectID, "UTF-8") + "?forwardToReplicas=" + forwardToReplicas, content.toString(), requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Update one synonym
   *
   * @param objectID The objectId of the synonym to save
   * @param content  The new content of this synonym
   */
  public JSONObject saveSynonym(String objectID, JSONObject content) throws AlgoliaException {
    return saveSynonym(objectID, content, false);
  }

  /**
   * Update one synonym
   *
   * @param objectID       The objectId of the synonym to save
   * @param content        The new content of this synonym
   * @param requestOptions Options to pass to this request
   */
  public JSONObject saveSynonym(String objectID, JSONObject content, RequestOptions requestOptions) throws AlgoliaException {
    return saveSynonym(objectID, content, false, requestOptions);
  }

  /**
   * Save a query rule
   *
   * @param objectID          the objectId of the query rule to save
   * @param rule              the content of this query rule
   * @param forwardToReplicas Forward this operation to the replica indices
   */
  public JSONObject saveRule(String objectID, JSONObject rule, boolean forwardToReplicas) throws AlgoliaException {
    return this.saveRule(objectID, rule, forwardToReplicas, RequestOptions.empty);
  }

  /**
   * Save a query rule
   *
   * @param objectID          the objectId of the query rule to save
   * @param rule              the content of this query rule
   * @param forwardToReplicas Forward this operation to the replica indices
   * @param requestOptions    Options to pass to this request
   */
  public JSONObject saveRule(String objectID, JSONObject rule, boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    try {
      return client.putRequest("/1/indexes/" + encodedIndexName + "/rules/" + URLEncoder.encode(objectID, "UTF-8") + "?forwardToReplicas=" + forwardToReplicas, rule.toString(), requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Save a query rule
   *
   * @param objectID the objectId of the query rule to save
   * @param rule     the content of this query rule
   */
  public JSONObject saveRule(String objectID, JSONObject rule) throws AlgoliaException {
    return saveRule(objectID, rule, false);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param rules              the list of rules to add/replace
   * @param forwardToReplicas  Forward this operation to the replica indices
   * @param clearExistingRules Replace the existing query rules with this batch
   */
  public JSONObject batchRules(List<JSONObject> rules, boolean forwardToReplicas, boolean clearExistingRules) throws AlgoliaException {
    return this.batchRules(rules, forwardToReplicas, clearExistingRules, RequestOptions.empty);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param rules              the list of rules to add/replace
   * @param forwardToReplicas  Forward this operation to the replica indices
   * @param clearExistingRules Replace the existing query rules with this batch
   * @param requestOptions     Options to pass to this request
   */
  public JSONObject batchRules(List<JSONObject> rules, boolean forwardToReplicas, boolean clearExistingRules, RequestOptions requestOptions) throws AlgoliaException {
    JSONArray array = new JSONArray();
    for (JSONObject obj : rules) {
      array.put(obj);
    }

    return client.postRequest("/1/indexes/" + encodedIndexName + "/rules/batch?forwardToReplicas=" + forwardToReplicas + "&clearExistingRules=" + clearExistingRules, array.toString(), true, false, requestOptions);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param rules             the list of rules to add/replace
   * @param forwardToReplicas Forward this operation to the replica indices
   */
  public JSONObject batchRules(List<JSONObject> rules, boolean forwardToReplicas) throws AlgoliaException {
    return batchRules(rules, forwardToReplicas, false);
  }

  /**
   * Add or Replace a list of synonyms
   *
   * @param rules the list of rules to add/replace
   */
  public JSONObject batchRules(List<JSONObject> rules) throws AlgoliaException {
    return batchRules(rules, false, false);
  }

  /**
   * Get a query rule
   *
   * @param objectID the objectID of the query rule to get
   */
  public JSONObject getRule(String objectID) throws AlgoliaException {
    return this.getRule(objectID, RequestOptions.empty);
  }

  /**
   * Get a query rule
   *
   * @param objectID       the objectID of the query rule to get
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getRule(String objectID, RequestOptions requestOptions) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.getRequest("/1/indexes/" + encodedIndexName + "/rules/" + URLEncoder.encode(objectID, "UTF-8"), true, requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete a query rule
   *
   * @param objectID the objectID of the query rule to delete
   */
  public JSONObject deleteRule(String objectID) throws AlgoliaException {
    return this.deleteRule(objectID, RequestOptions.empty);
  }

  /**
   * Delete a query rule
   *
   * @param objectID       the objectID of the query rule to delete
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteRule(String objectID, RequestOptions requestOptions) throws AlgoliaException {
    if (objectID == null || objectID.length() == 0) {
      throw new AlgoliaException("Invalid objectID");
    }
    try {
      return client.deleteRequest("/1/indexes/" + encodedIndexName + "/rules/" + URLEncoder.encode(objectID, "UTF-8"), requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete all query rules
   *
   * @param forwardToReplicas Forward the operation to the replica indices
   */
  public JSONObject clearRules(boolean forwardToReplicas) throws AlgoliaException {
    return this.clearRules(forwardToReplicas, RequestOptions.empty);
  }

  /**
   * Delete all query rules
   *
   * @param forwardToReplicas Forward the operation to the replica indices
   * @param requestOptions    Options to pass to this request
   */
  public JSONObject clearRules(boolean forwardToReplicas, RequestOptions requestOptions) throws AlgoliaException {
    return client.postRequest("/1/indexes/" + encodedIndexName + "/rules/clear?forwardToReplicas=" + forwardToReplicas, "", true, false, requestOptions);
  }

  /**
   * Delete all query rules
   */
  public JSONObject clearRules() throws AlgoliaException {
    return clearRules(false);
  }

  /**
   * Search for query rules
   *
   * @param query the query
   */
  public JSONObject searchRules(RuleQuery query) throws AlgoliaException, JSONException {
    return this.searchRules(query, RequestOptions.empty);
  }

  /**
   * Search for query rules
   *
   * @param query          the query
   * @param requestOptions Options to pass to this request
   */
  public JSONObject searchRules(RuleQuery query, RequestOptions requestOptions) throws AlgoliaException, JSONException {
    JSONObject body = new JSONObject();
    if (query.getQuery() != null) {
      body = body.put("query", query.getQuery());
    }
    if (query.getAnchoring() != null) {
      body = body.put("anchoring", query.getAnchoring());
    }
    if (query.getContext() != null) {
      body = body.put("context", query.getContext());
    }
    if (query.getPage() != null) {
      body = body.put("page", query.getPage());
    }
    if (query.getHitsPerPage() != null) {
      body = body.put("hitsPerPage", query.getHitsPerPage());
    }

    return client.postRequest("/1/indexes/" + encodedIndexName + "/rules/search", body.toString(), false, true, requestOptions);
  }

  /**
   * @deprecated See {@code IndexBrowser}
   */
  static class IndexBrower extends IndexBrowser {
    IndexBrower(APIClient client, String encodedIndexName, Query params, String startingCursor) throws AlgoliaException {
      super(client, encodedIndexName, params, startingCursor, RequestOptions.empty);
    }
  }

  /**
   * This class iterates over an index using the cursor-based browse mechanism
   */
  static public class IndexBrowser implements Iterator<JSONObject> {

    final APIClient client;
    final Query params;
    final String encodedIndexName;
    final RequestOptions requestOptions;
    JSONObject answer;
    JSONObject hit;
    int pos;

    IndexBrowser(APIClient client, String encodedIndexName, Query params, String startingCursor, RequestOptions requestOptions) throws AlgoliaException {
      this.client = client;
      this.params = params;
      this.encodedIndexName = encodedIndexName;
      this.requestOptions = requestOptions;

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
      this.answer = client.getRequest("/1/indexes/" + encodedIndexName + "/browse" + ((paramsString.length() > 0) ? ("?" + paramsString) : ""), true, requestOptions);
    }
  }

}
