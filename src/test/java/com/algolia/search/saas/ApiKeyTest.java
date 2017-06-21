package com.algolia.search.saas;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ApiKeyTest extends AlgoliaTest {

  @Test
  public void apiKeyIndex() throws AlgoliaException, JSONException {
    JSONObject newKey = index.addApiKey(Collections.singletonList("search"));
    waitKey(index, newKey.getString("key"), null);
    assertTrue(!newKey.getString("key").equals(""));
    JSONObject res = index.listApiKeys();
    assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
    JSONObject getKey = index.getApiKey(newKey.getString("key"));
    assertEquals(newKey.getString("key"), getKey.getString("value"));
    index.updateApiKey(newKey.getString("key"), Collections.singletonList("addObject"));
    waitKey(index, newKey.getString("key"), "[\"addObject\"]");
    getKey = index.getApiKey(newKey.getString("key"));
    assertEquals(getKey.getJSONArray("acl").get(0), "addObject");
    index.deleteApiKey(getKey.getString("value"));
    waitKeyDeleted(index, newKey.getString("key"));
    JSONObject resAfter = index.listApiKeys();
    assertTrue(!isPresent(resAfter.getJSONArray("keys"), newKey.getString("key"), "value"));
  }

  @Test
  public void apiKey() throws AlgoliaException, JSONException {
    JSONObject newKey = client.addApiKey(Collections.singletonList("search"));
    waitKey(client, newKey.getString("key"), null);
    assertTrue(!newKey.getString("key").equals(""));
    JSONObject res = client.listApiKeys();
    assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
    JSONObject getKey = client.getApiKey(newKey.getString("key"));
    assertEquals(newKey.getString("key"), getKey.getString("value"));
    client.updateApiKey(newKey.getString("key"), Collections.singletonList("addObject"));
    waitKey(client, newKey.getString("key"), "[\"addObject\"]");
    getKey = client.getApiKey(newKey.getString("key"));
    assertEquals(getKey.getJSONArray("acl").get(0), "addObject");
    client.deleteApiKey(getKey.getString("value"));
    waitKeyDeleted(client, newKey.getString("key"));
    JSONObject resAfter = client.listApiKeys();
    assertTrue(!isPresent(resAfter.getJSONArray("keys"), newKey.getString("key"), "value"));
  }

  @Test
  public void apiKeyLimit() throws AlgoliaException, JSONException {
    JSONObject newKey = client.addApiKey(Collections.singletonList("search"), 0, 2, 2);
    waitKey(client, newKey.getString("key"), null);
    assertTrue(!newKey.getString("key").equals(""));
    JSONObject res = client.listApiKeys();
    assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
    index.deleteApiKey(newKey.getString("key"));
  }

  @Test
  public void apiKeyIndexLimit() throws AlgoliaException, JSONException {
    JSONObject newKey = index.addApiKey(Collections.singletonList("search"), 0, 2, 2);
    waitKey(client, newKey.getString("key"), null);
    assertTrue(!newKey.getString("key").equals(""));
    JSONObject res = index.listApiKeys();
    assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
    index.deleteApiKey(newKey.getString("key"));
  }

}
