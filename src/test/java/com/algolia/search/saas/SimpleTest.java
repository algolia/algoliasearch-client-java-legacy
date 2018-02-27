package com.algolia.search.saas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SimpleTest extends AlgoliaTest {

  @Test
  public void deleteIndexIfExists() {
    try {
      client.deleteIndex(indexName);
    } catch (AlgoliaException e) {
      // not fatal
    }
  }

  @Test
  public void settings() throws AlgoliaException, JSONException {
    JSONObject task = index.setSettings(new JSONObject()
      .put("attributesToRetrieve", Collections.singletonList("firstname")));
    index.waitTask(Long.toString(task.getLong("taskID")));
    JSONObject settings = index.getSettings();
    assertEquals("firstname", settings.getJSONArray("attributesToRetrieve").getString(0));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void browse() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.browse(0);
    assertEquals(1, res.getInt("nbHits"));
    res = index.browse(0, 1);
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void logs() throws AlgoliaException, JSONException {
    JSONObject res = client.getLogs();
    assertTrue(res.getJSONArray("logs").length() > 0);
    res = client.getLogs(0, 1);
    assertTrue(res.getJSONArray("logs").length() == 1);
    res = client.getLogs(0, 1, false);
    assertTrue(res.getJSONArray("logs").length() == 1);
    res = client.getLogs(0, 1, APIClient.LogType.LOG_ALL);
    assertTrue(res.getJSONArray("logs").length() == 1);
  }

  @Test
  public void emptyAPPID() {
    try {
      new APIClient(null, "algolia");
      assertTrue(false);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

  @Test
  public void emptyAPPKEY() {
    try {
      new APIClient("algolia", null);
      assertTrue(false);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

  @Test
  public void emptyHost() {
    try {
      new APIClient("algolia", "algolia", new ArrayList<String>());
      assertTrue(false);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

  @Test
  public void headerDisableRateLimit() throws AlgoliaException, JSONException {
    client.disableRateLimitForward();
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query());
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void invalidKey() {
    try {
      APIClient client = new APIClient("unreach", "test");
      client.listIndexes();
      assertTrue(false);
    } catch (AlgoliaException e) {
      assertTrue(true);
    }
  }

  @Test
  public void invalidObjectID() {
    try {
      index.deleteObject("");
      assertTrue(false);
    } catch (AlgoliaException e) {
      assertTrue(true);
    }
  }

  @Test
  public void customBatch() throws AlgoliaException, JSONException {
    assertEquals(indexName, index.getIndexName());
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
    JSONArray actions = new JSONArray();
    JSONObject action = new JSONObject();
    action.put("action", "deleteObject");
    action.put("objectID", "a/go/?à");
    actions.put(action);
    task = index.batch(actions);
    index.waitTask(task.getLong("taskID"));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void newSecuredApiKeys() throws InvalidKeyException, NoSuchAlgorithmException {
    assertEquals("MDZkNWNjNDY4M2MzMDA0NmUyNmNkZjY5OTMzYjVlNmVlMTk1NTEwMGNmNTVjZmJhMmIwOTIzYjdjMTk2NTFiMnRhZ0ZpbHRlcnM9JTI4cHVibGljJTJDdXNlcjElMjk=", client.generateSecuredApiKey("182634d8894831d5dbce3b3185c50881", "(public,user1)"));
    assertEquals("MDZkNWNjNDY4M2MzMDA0NmUyNmNkZjY5OTMzYjVlNmVlMTk1NTEwMGNmNTVjZmJhMmIwOTIzYjdjMTk2NTFiMnRhZ0ZpbHRlcnM9JTI4cHVibGljJTJDdXNlcjElMjk=", client.generateSecuredApiKey("182634d8894831d5dbce3b3185c50881", new Query().setTagFilters("(public,user1)")));
    assertEquals("OGYwN2NlNTdlOGM2ZmM4MjA5NGM0ZmYwNTk3MDBkNzMzZjQ0MDI3MWZjNTNjM2Y3YTAzMWM4NTBkMzRiNTM5YnRhZ0ZpbHRlcnM9JTI4cHVibGljJTJDdXNlcjElMjkmdXNlclRva2VuPTQy", client.generateSecuredApiKey("182634d8894831d5dbce3b3185c50881", new Query().setTagFilters("(public,user1)").setUserToken("42")));
  }

  @Test
  public void multipleQueries() throws AlgoliaException, JSONException {
    JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
    index.waitTask(obj.getLong("taskID"));
    List<APIClient.IndexQuery> queries = new ArrayList<APIClient.IndexQuery>();
    queries.add(new APIClient.IndexQuery(safe_name("àlgol?à-java"), new Query("")));
    JSONObject res = client.multipleQueries(queries);
    assertEquals(1, res.getJSONArray("results").length());
    assertEquals(1, res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").length());
    assertEquals("foo", res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").getJSONObject(0).getString("s"));
    assertEquals(42, res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").getJSONObject(0).getLong("i"));
    assertEquals(true, res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").getJSONObject(0).getBoolean("b"));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void deleteByQuery() throws JSONException, AlgoliaException {
    JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
      .put("name", "Washington"))
      .put(new JSONObject().put("name", "San Francisco"))
      .put(new JSONObject().put("name", "San Jose")));
    index.waitTask(task.getLong("taskID"));
    index.deleteByQuery(new Query("San"));
    JSONObject res = index.search(new Query(""));
    assertEquals(1, res.getInt("nbHits"));
  }

  @Test
  public void deleteBy() throws JSONException, AlgoliaException {
    JSONObject task = index.addObjects(
      new JSONArray()
        .put(new JSONObject().put("name", "Washington"))
        .put(new JSONObject().put("name", "San Francisco"))
        .put(new JSONObject().put("name", "San Jose"))
    );
    index.waitTask(task.getLong("taskID"));
    task = index.deleteBy(new Query().setTagFilters("a"));
    index.waitTask(task.getLong("taskID"));
    JSONObject res = index.search(new Query(""));
    assertEquals(3, res.getInt("nbHits"));
  }

  @Test
  public void disjunctiveFaceting() throws AlgoliaException, JSONException {
    index.setSettings(new JSONObject("{\"attributesForFaceting\":[\"city\", \"stars\", \"facilities\"]}"));
    JSONObject task = index.addObjects(new JSONArray()
      .put(new JSONObject("{\"name\":\"Hotel A\", \"stars\":\"*\", \"facilities\":[\"wifi\", \"bath\", \"spa\"], \"city\":\"Paris\"}"))
      .put(new JSONObject("{\"name\":\"Hotel B\", \"stars\":\"*\", \"facilities\":[\"wifi\"], \"city\":\"Paris\"}"))
      .put(new JSONObject("{\"name\":\"Hotel C\", \"stars\":\"**\", \"facilities\":[\"bath\"], \"city\":\"San Fancisco\"}"))
      .put(new JSONObject("{\"name\":\"Hotel D\", \"stars\":\"****\", \"facilities\":[\"spa\"], \"city\":\"Paris\"}"))
      .put(new JSONObject("{\"name\":\"Hotel E\", \"stars\":\"****\", \"facilities\":[\"spa\"], \"city\":\"New York\"}")));
    index.waitTask(task.getLong("taskID"));
    HashMap<String, List<String>> refinements = new HashMap<String, List<String>>();
    List<String> disjunctiveFacets = new ArrayList<String>();
    List<String> facets = new ArrayList<String>();
    facets.add("city");
    disjunctiveFacets.add("stars");
    disjunctiveFacets.add("facilities");
    JSONObject answer = index.searchDisjunctiveFaceting(new Query("h").setFacets(facets), disjunctiveFacets);
    assertEquals(5, answer.getInt("nbHits"));
    assertEquals(1, answer.getJSONObject("facets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").length());

    ArrayList<String> refineValue = new ArrayList<String>();
    refineValue.add("*");
    refinements.put("stars", refineValue);
    answer = index.searchDisjunctiveFaceting(new Query("h").setFacets(facets), disjunctiveFacets, refinements);
    assertEquals(2, answer.getInt("nbHits"));
    assertEquals(1, answer.getJSONObject("facets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("*"));
    assertEquals(1, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("**"));
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("****"));

    refineValue = new ArrayList<String>();
    refineValue.add("Paris");
    refinements.put("city", refineValue);
    answer = index.searchDisjunctiveFaceting(new Query("h").setFacets(facets), disjunctiveFacets, refinements);
    assertEquals(2, answer.getInt("nbHits"));
    assertEquals(1, answer.getJSONObject("facets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("*"));
    assertEquals(1, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("****"));

    refineValue = new ArrayList<String>();
    refineValue.add("*");
    refineValue.add("****");
    refinements.put("stars", refineValue);
    answer = index.searchDisjunctiveFaceting(new Query("h").setFacets(facets), disjunctiveFacets, refinements);
    assertEquals(3, answer.getInt("nbHits"));
    assertEquals(1, answer.getJSONObject("facets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").length());
    assertEquals(2, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("*"));
    assertEquals(1, answer.getJSONObject("disjunctiveFacets").getJSONObject("stars").getInt("****"));
  }

  @Test
  public void keepAlive() throws AlgoliaException, JSONException {
    JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
      .put("name", "Los Angeles").put("objectID", "1")).put(new JSONObject()
      .put("name", "San Francisco").put("objectID", "2")));
    index.waitTask(task.getLong("taskID"));

    // Redefine a client to break the current keep alive
    String applicationID = System.getenv("ALGOLIA_APPLICATION_ID");
    String apiKey = System.getenv("ALGOLIA_API_KEY");
    client = new APIClient(applicationID, apiKey);
    index = client.initIndex(indexName);

    double firstDSNQuery;
    double avgDSNQuery = 0;
    long current = System.currentTimeMillis();
    index.search(new Query());
    firstDSNQuery = System.currentTimeMillis() - current;
    int upperBound = 10;
    for (int i = 0; i < upperBound; ++i) {
      current = System.currentTimeMillis();
      index.search(new Query());
      avgDSNQuery += System.currentTimeMillis() - current;
    }
    avgDSNQuery /= upperBound;
    double timeFactor = firstDSNQuery / avgDSNQuery;
    assertTrue("KeepAlive seems disabled: firstDSNQuery / avgDSNQuery = " + timeFactor + " <= 2.0", 2.0 < timeFactor);
  }

  @Test
  public void customBatchIndexes() throws AlgoliaException, JSONException {
    List<JSONObject> array = new ArrayList<JSONObject>();
    array.add(new JSONObject().put("action", "addObject").put("indexName", index.getIndexName()).put("body", new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger")));
    array.add(new JSONObject().put("action", "addObject").put("indexName", index.getIndexName()).put("body", new JSONObject().put("firstname", "Warren").put("lastname", "Speach")));
    JSONObject task = client.batch(array);
    index.waitTask(task.getJSONObject("taskID").getLong(index.getIndexName()));
    JSONObject res = index.search(new Query(""));
    assertEquals(2, res.getInt("nbHits"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void checkFallBack() throws AlgoliaException, JSONException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String applicationID = System.getenv("ALGOLIA_APPLICATION_ID");
    String apiKey = System.getenv("ALGOLIA_API_KEY");
    APIClient client = new APIClient(applicationID, apiKey);
    Field f = client.getClass().getDeclaredField("buildHostsArray");
    Field f2 = client.getClass().getDeclaredField("queryHostsArray");
    f.setAccessible(true);
    f2.setAccessible(true);
    List<String> host = (List<String>) f.get(client);
    List<String> host2 = (List<String>) f2.get(client);
    host.set(0, applicationID + "test.algolia.net"); // Force to use algolianet.com
    host2.set(0, applicationID + "test.algolia.net"); // Force to use algolianet.com
    client.listIndexes();
  }

  @Test
  public void browseWithCursor() throws AlgoliaException, JSONException {
    List<JSONObject> objects = new ArrayList<JSONObject>();
    for (int i = 0; i < 1500; ++i) {
      objects.add(new JSONObject().put("objectID", i).put("i", i));
    }
    JSONObject task = index.addObjects(objects);
    index.waitTask(task.getLong("taskID"));
    // browse whole index
    {
      int i = 0;
      Iterator<JSONObject> it = index.browse(new Query());
      while (it.hasNext()) {
        it.next();
        ++i;
      }
      assertEquals(1500, i);
    }
    // browse with condition
    {
      int i = 0;
      Iterator<JSONObject> it = index.browse(new Query().setNumericFilters("i<42"));
      while (it.hasNext()) {
        it.next();
        ++i;
      }
      assertEquals(42, i);
    }
  }
}
