package com.algolia.search.saas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SearchTest extends AlgoliaTest {

  @Test
  public void search() throws AlgoliaException, JSONException {
    JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
    index.waitTask(obj.getLong("taskID"));
    JSONObject res = index.search(new Query("foo").setTypoTolerance(Query.TypoTolerance.TYPO_FALSE));
    assertEquals(1, res.getJSONArray("hits").length());
    assertEquals("foo", res.getJSONArray("hits").getJSONObject(0).getString("s"));
    assertEquals(42, res.getJSONArray("hits").getJSONObject(0).getLong("i"));
    assertEquals(true, res.getJSONArray("hits").getJSONObject(0).getBoolean("b"));
  }

  @Test
  public void searchFacets() throws AlgoliaException, JSONException {
    final JSONObject setSettingsTask = index.setSettings(new JSONObject()
      .put("attributesForFaceting", new JSONArray()
        .put("searchable(series)")
        .put("kind"))
    );

    final JSONObject addObjectsResult = index.addObjects(new JSONArray()
      .put(new JSONObject()
        .put("objectID", "1")
        .put("name", "Snoopy")
        .put("kind", new JSONArray().put("dog").put("animal"))
        .put("born", 1950)
        .put("series", "Peanuts"))
      .put(new JSONObject()
        .put("objectID", "2")
        .put("name", "Woodstock")
        .put("kind", new JSONArray().put("bird").put("animal"))
        .put("born", 1960)
        .put("series", "Peanuts"))
      .put(new JSONObject()
        .put("objectID", "3")
        .put("name", "Charlie Brown")
        .put("kind", new JSONArray().put("human"))
        .put("born", 1950)
        .put("series", "Peanuts"))
      .put(new JSONObject()
        .put("objectID", "4")
        .put("name", "Hobbes")
        .put("kind", new JSONArray().put("tiger").put("animal").put("teddy"))
        .put("born", 1985)
        .put("series", "Calvin & Hobbes"))
      .put(new JSONObject()
        .put("objectID", "5")
        .put("name", "Calvin")
        .put("kind", new JSONArray().put("human"))
        .put("born", 1985)
        .put("series", "Calvin & Hobbes"))
    );

    index.waitTask(setSettingsTask.getLong("taskID"));
    index.waitTask(addObjectsResult.getLong("taskID"));

    JSONObject searchFacet = index.searchInFacetValues("series", "Peanutz", null);
    final JSONArray facetHits = searchFacet.optJSONArray("facetHits");
    assertTrue("The response should have facetHits.", facetHits != null);
    assertEquals("There should be one facet match.", 1, facetHits.length());
  }

  @Test
  public void searchUpdated() throws AlgoliaException, JSONException {
    JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
    index.waitTask(obj.getLong("taskID"));
    JSONObject res = index.search(new Query("foo"));
    assertEquals(1, res.getJSONArray("hits").length());
    res = index.saveObject(new JSONObject().put("s", "bar"), res.getJSONArray("hits").getJSONObject(0).getString("objectID"));
    index.waitTask(res.getLong("taskID"));
    res = index.search(new Query("foo"));
    assertEquals(0, res.getJSONArray("hits").length());

    res = index.search(new Query("bar"));
    assertEquals(1, res.getJSONArray("hits").length());
    assertEquals("bar", res.getJSONArray("hits").getJSONObject(0).getString("s"));
  }

  @Test
  public void searchAll() throws AlgoliaException, JSONException {
    JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
    index.waitTask(obj.getLong("taskID"));
    JSONObject res = index.search(new Query("foo"));
    assertEquals(1, res.getJSONArray("hits").length());
    res = index.saveObject(new JSONObject().put("s", "bar"), res.getJSONArray("hits").getJSONObject(0).getString("objectID"));
    index.waitTask(res.getLong("taskID"));
    res = index.search(new Query(""));
    assertEquals(1, res.getJSONArray("hits").length());
    res = index.search(new Query("*"));
    assertEquals(1, res.getJSONArray("hits").length());
  }
}
