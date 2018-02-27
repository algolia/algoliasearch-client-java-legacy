package com.algolia.search.saas;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IndexCRUDTest extends AlgoliaTest {

  @Test
  public void index() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"), "a/go/?à");
    index.waitTask(task.getLong("taskID"));
    JSONObject res = client.listIndexes();
    assertTrue(isPresent(res.getJSONArray("items"), indexName, "name"));
    task = client.deleteIndex(indexName);
    index.waitTask(task.getLong("taskID"));
    JSONObject resAfter = client.listIndexes();
    assertFalse(isPresent(resAfter.getJSONArray("items"), indexName, "name"));
  }


  @Test
  public void moveIndex() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    task = client.moveIndex(indexName, indexName + "2");
    Index newIndex = client.initIndex(indexName + "2");
    newIndex.waitTask(task.getLong("taskID"));
    Query query = new Query();
    query.setQueryType(Query.QueryType.PREFIX_ALL);
    query.setQueryString("jimye");
    query.setAttributesToRetrieve(Collections.singletonList("firstname"));
    query.setAttributesToHighlight(new ArrayList<String>());
    query.setAttributesToSnippet(new ArrayList<String>());
    query.enableDistinct(false);
    query.setMinWordSizeToAllowOneTypo(1);
    query.setMinWordSizeToAllowTwoTypos(2);
    query.getRankingInfo(true);
    query.setPage(0);
    query.setHitsPerPage(1);
    assertTrue(!query.getQueryString().equals(""));
    JSONObject res = newIndex.search(query);
    assertEquals(1, res.getInt("nbHits"));
    try {
      index.search(new Query("jimie"));
      assertTrue(false);
    } catch (AlgoliaException e) {
      assertTrue(true);
    }
    client.deleteIndex(indexName + "2");
  }

  @Test
  public void copyIndex() throws AlgoliaException, JSONException {
    JSONObject task = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
    index.waitTask(task.getLong("taskID"));
    task = client.copyIndex(indexName, indexName + "2");
    Index newIndex = client.initIndex(indexName + "2");
    newIndex.waitTask(task.getLong("taskID"));
    JSONObject res = newIndex.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
    res = index.search(new Query("jimie"));
    assertEquals(1, res.getInt("nbHits"));
    client.deleteIndex(indexName + "2");
  }

}
