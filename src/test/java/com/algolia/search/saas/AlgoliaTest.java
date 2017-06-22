package com.algolia.search.saas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

public class AlgoliaTest {

  static final String indexName = safe_name("àlgol?à-java");

  static APIClient client;
  static Index index;

  static String safe_name(String name) {
    if (System.getenv("TRAVIS") != null) {
      String[] id = System.getenv("TRAVIS_JOB_NUMBER").split("\\.");
      return name + "_travis" + id[id.length - 1];
    }
    return name;

  }

  static boolean isPresent(JSONArray array, String search, String attr) throws JSONException {
    boolean isPresent = false;
    for (int i = 0; i < array.length(); ++i) {
      isPresent = isPresent || array.getJSONObject(i).getString(attr).equals(search);
    }
    return isPresent;
  }

  @BeforeClass
  public static void init() {
    String applicationID = System.getenv("ALGOLIA_APPLICATION_ID");
    String apiKey = System.getenv("ALGOLIA_API_KEY");
    Assume.assumeFalse("You must set environement variables ALGOLIA_APPLICATION_ID and ALGOLIA_API_KEY to run the tests.", applicationID == null || apiKey == null);
    client = new APIClient(applicationID, apiKey);
    index = client.initIndex(indexName);
  }

  @AfterClass
  public static void dispose() {
    try {
      client.deleteIndex(indexName);
    } catch (AlgoliaException e) {
      // Not fatal
    }
  }

  @Before
  public void eachInit() {
    try {
      index.clearIndex();
    } catch (AlgoliaException e) {
      //Normal
    }
  }

  protected void waitKey(APIClient client, String key, String acls) {
    for (int i = 0; i < 60; ++i) {
      try {
        JSONObject obj = client.getApiKey(key);
        if (acls != null) {
          if (!obj.getJSONArray("acl").toString().equals(acls)) {
            throw new AlgoliaException("not good ACL");
          }
        }
        return;
      } catch (Exception e) {
        // not found
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          // not fatal
        }
      }
    }
  }

  protected void waitKey(Index index, String key, String acls) {
    for (int i = 0; i < 60; ++i) {
      try {
        JSONObject obj = index.getApiKey(key);
        if (acls != null) {
          if (!obj.getJSONArray("acl").toString().equals(acls)) {
            throw new AlgoliaException("not good ACL");
          }
        }
        return;
      } catch (Exception e) {
        // not found
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          // not fatal
        }
      }
    }
  }

  protected void waitKeyDeleted(APIClient client, String key) {
    for (int i = 0; i < 60; ++i) {
      try {
        client.getApiKey(key);
        // found
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          // not fatal
        }
      } catch (Exception e) {
        return;
      }
    }
  }

  protected void waitKeyDeleted(Index index, String key) {
    for (int i = 0; i < 60; ++i) {
      try {
        index.getApiKey(key);
        // found
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          // not fatal
        }
      } catch (Exception e) {
        return;
      }
    }
  }
}
