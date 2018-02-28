package com.algolia.search.saas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ObjectsTest extends AlgoliaTest {
  @Test
  public void addObjects() throws JSONException, AlgoliaException {
    List<JSONObject> array = new ArrayList<JSONObject>();
    array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
    array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach"));
    JSONObject task = index.addObjects(array);
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query(""));
    assertEquals(2, res.getInt("nbHits"));
  }

  @Test
  public void deleteObjects() throws JSONException, AlgoliaException {
    List<JSONObject> array = new ArrayList<JSONObject>();
    array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "à/go/?à"));
    array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "à/go/?à2"));
    JSONObject task = index.addObjects(array);
    index.waitTask(task.getLong("taskID"));
    List<String> deleted = new ArrayList<String>();
    deleted.add("à/go/?à");
    deleted.add("à/go/?à2");
    task = index.deleteObjects(deleted);
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query(""));
    assertEquals(0, res.getInt("nbHits"));
  }

  @Test
  public void addObjectsList() throws JSONException, AlgoliaException {
    JSONArray array = new JSONArray();
    array.put(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
    array.put(new JSONObject().put("firstname", "Warren").put("lastname", "Speach"));
    JSONObject task = index.addObjects(array);
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query(""));
    assertEquals(2, res.getInt("nbHits"));
  }

  @Test
  public void saveObjects() throws JSONException, AlgoliaException {
    List<JSONObject> array = new ArrayList<JSONObject>();
    array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "a/go/?à"));
    array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "a/go/ià"));
    JSONObject task = index.saveObjects(array);
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query(""));
    assertEquals(2, res.getInt("nbHits"));
  }

  @Test
  public void getObjects() throws AlgoliaException, JSONException {
    JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
      .put("name", "Los Angeles").put("objectID", "1")).put(new JSONObject()
      .put("name", "San Francisco").put("objectID", "2")));
    index.waitTask(task.getLong("taskID"));
    List<String> objectIDs = new ArrayList<String>();
    objectIDs.add("1");
    objectIDs.add("2");
    JSONObject object = index.getObjects(objectIDs);
    assertEquals("Los Angeles", object.getJSONArray("results").getJSONObject(0).getString("name"));
    assertEquals("San Francisco", object.getJSONArray("results").getJSONObject(1).getString("name"));
  }

  @Test
  public void getObjectsWithAttr() throws AlgoliaException, JSONException {
    JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
      .put("name", "Los Angeles").put("objectID", "1")).put(new JSONObject()
      .put("name", "San Francisco").put("objectID", "2")));
    index.waitTask(task.getLong("taskID"));
    List<String> objectIDs = new ArrayList<String>();
    objectIDs.add("1");
    objectIDs.add("2");
    JSONObject object = index.getObjects(objectIDs, Collections.singletonList("objectID"));
    assertEquals("The retrieved object should have only one attribute.", 1, object.getJSONArray("results").getJSONObject(0).names().length());
    assertEquals("The retrieved object should have only one attribute.", 1, object.getJSONArray("results").getJSONObject(1).names().length());
  }


}
