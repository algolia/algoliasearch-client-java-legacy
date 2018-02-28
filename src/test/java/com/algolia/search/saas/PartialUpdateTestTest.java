package com.algolia.search.saas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class PartialUpdateTestTest extends AlgoliaTest {

  @Test
  public void partialUpdateObject() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    task = index.partialUpdateObject(new JSONObject()
      .put("firtname", "Roger"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void partialUpdateObjectNoCreate_whenObjectExists() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    task = index.partialUpdateObjectNoCreate(new JSONObject()
      .put("firtname", "Roger"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void partialUpdateObjectNoCreate_whenObjectDoesNotExist() throws AlgoliaException, JSONException {
    JSONObject task = index.partialUpdateObjectNoCreate(new JSONObject().put("firtname", "Jimmie"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    assertEquals(0, res.getInt("nbHits"));
  }

  @Test
  public void partialUpdateObjects() throws JSONException, AlgoliaException {
    List<JSONObject> array = new ArrayList<JSONObject>();
    array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "a/go/?à"));
    array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "a/go/ià"));
    JSONObject task = index.saveObjects(array);
    index.waitTask(task.getLong("taskID"));
    array = new ArrayList<JSONObject>();
    array.add(new JSONObject().put("firstname", "Roger").put("objectID", "a/go/?à"));
    array.add(new JSONObject().put("firstname", "Robert").put("objectID", "a/go/ià"));
    task = index.partialUpdateObjects(array);
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("Ro"));
    assertEquals(2, res.getInt("nbHits"));
  }

  @Test
  public void partialUpdateObjectsList() throws JSONException, AlgoliaException {
    JSONArray array = new JSONArray();
    array.put(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "a/go/?à"));
    array.put(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "a/go/ià"));
    JSONObject task = index.saveObjects(array);
    index.waitTask(task.getLong("taskID"));

    array = new JSONArray();
    array.put(new JSONObject().put("firstname", "Roger").put("objectID", "a/go/?à"));
    array.put(new JSONObject().put("firstname", "Robert").put("objectID", "a/go/ià"));

    task = index.partialUpdateObjects(array);
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("Ro"));
    assertEquals(2, res.getInt("nbHits"));
  }

}
