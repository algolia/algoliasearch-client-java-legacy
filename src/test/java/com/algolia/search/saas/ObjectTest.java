package com.algolia.search.saas;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ObjectTest extends AlgoliaTest {

  @Test
  public void pushObject() throws AlgoliaException, JSONException {
    JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
    index.waitTask(obj.getLong("taskID"));
  }

  @Test
  public void addObject() throws AlgoliaException, JSONException {
    assertEquals(indexName, index.getIndexName());
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void saveObject() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void getObject() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject object = index.getObject("a/go/?à");
    assertEquals("Jimmie", object.getString("firstname"));
  }

  @Test
  public void getObjectWithAttr() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject object = index.getObject("a/go/?à", Arrays.asList("lastname", "firstname"));
    assertEquals("Barninger", object.getString("lastname"));
  }

  @Test
  public void deleteObject() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    index.deleteObject("a/go/?à");
    assertEquals(1, res.getInt("nbHits"));
  }


}
