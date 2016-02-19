package com.algolia.search.saas;

import com.algolia.search.saas.APIClient.LogType;
import com.algolia.search.saas.Query.QueryType;
import com.algolia.search.saas.Query.TypoTolerance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleTest {
    private static final String indexName = safe_name("àlgol?à-java");

    private static APIClient client;
    private static Index index;


    public static String safe_name(String name) {
        if (System.getenv("TRAVIS") != null) {
            String[] id = System.getenv("TRAVIS_JOB_NUMBER").split("\\.");
            return name + "_travis" + id[id.length - 1];
        }
        return name;

    }

    public static boolean isPresent(JSONArray array, String search, String attr) throws JSONException {
        boolean isPresent = false;
        for (int i = 0; i < array.length(); ++i) {
            isPresent = isPresent || array.getJSONObject(i).getString(attr).equals(search);
        }
        return isPresent;
    }

    private void waitForIt() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }
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

    @Test
    public void test01_deleteIndexIfExists() {
        try {
            client.deleteIndex(indexName);
        } catch (AlgoliaException e) {
            // not fatal
        }
    }

    @Test
    public void test02_pushObject() throws AlgoliaException, JSONException {
        JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
        index.waitTask(obj.getString("taskID"));
    }

    @Test
    public void test03_search() throws AlgoliaException, JSONException {
        JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
        index.waitTask(obj.getString("taskID"));
        JSONObject res = index.search(new Query("foo").setTypoTolerance(TypoTolerance.TYPO_FALSE));
        assertEquals(1, res.getJSONArray("hits").length());
        assertEquals("foo", res.getJSONArray("hits").getJSONObject(0).getString("s"));
        assertEquals(42, res.getJSONArray("hits").getJSONObject(0).getLong("i"));
        assertEquals(true, res.getJSONArray("hits").getJSONObject(0).getBoolean("b"));
    }

    @Test
    public void test04_saveObject() throws AlgoliaException, JSONException {
        JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
        index.waitTask(obj.getString("taskID"));
        JSONObject res = index.search(new Query("foo"));
        assertEquals(1, res.getJSONArray("hits").length());
        res = index.saveObject(new JSONObject().put("s", "bar"), res.getJSONArray("hits").getJSONObject(0).getString("objectID"));
        index.waitTask(res.getString("taskID"));
    }

    @Test
    public void test05_searchUpdated() throws AlgoliaException, JSONException {
        JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
        index.waitTask(obj.getString("taskID"));
        JSONObject res = index.search(new Query("foo"));
        assertEquals(1, res.getJSONArray("hits").length());
        res = index.saveObject(new JSONObject().put("s", "bar"), res.getJSONArray("hits").getJSONObject(0).getString("objectID"));
        index.waitTask(res.getString("taskID"));
        res = index.search(new Query("foo"));
        assertEquals(0, res.getJSONArray("hits").length());

        res = index.search(new Query("bar"));
        assertEquals(1, res.getJSONArray("hits").length());
        assertEquals("bar", res.getJSONArray("hits").getJSONObject(0).getString("s"));
    }

    @Test
    public void test06_searchAll() throws AlgoliaException, JSONException {
        JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
        index.waitTask(obj.getString("taskID"));
        JSONObject res = index.search(new Query("foo"));
        assertEquals(1, res.getJSONArray("hits").length());
        res = index.saveObject(new JSONObject().put("s", "bar"), res.getJSONArray("hits").getJSONObject(0).getString("objectID"));
        index.waitTask(res.getString("taskID"));
        res = index.search(new Query(""));
        assertEquals(1, res.getJSONArray("hits").length());
        res = index.search(new Query("*"));
        assertEquals(1, res.getJSONArray("hits").length());
    }

    @Test
    public void test07_addObject() throws AlgoliaException, JSONException {
        assertEquals(indexName, index.getIndexName());
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test08_saveObject() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test09_partialUpdateObject() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        task = index.partialUpdateObject(new JSONObject()
                .put("firtname", "Roger"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test09_partialUpdateObjectNoCreate_whenObjectExists() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        task = index.partialUpdateObjectNoCreate(new JSONObject()
                .put("firtname", "Roger"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test09_partialUpdateObjectNoCreate_whenObjectDoesNotExist() throws AlgoliaException, JSONException {
        JSONObject task = task = index.partialUpdateObjectNoCreate(new JSONObject()
                .put("firtname", "Jimmie"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        assertEquals(0, res.getInt("nbHits"));
    }

    @Test
    public void test10_getObject() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject object = index.getObject("a/go/?à");
        assertEquals("Jimmie", object.getString("firstname"));
    }

    @Test
    public void test11_getObjectWithAttr() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject object = index.getObject("a/go/?à", Arrays.asList("lastname", "firstname"));
        assertEquals("Barninger", object.getString("lastname"));
    }

    @Test
    public void test12_deleteObject() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        index.deleteObject("a/go/?à");
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test13_settings() throws AlgoliaException, JSONException {
        JSONObject task = index.setSettings(new JSONObject()
                .put("attributesToRetrieve", Collections.singletonList("firstname")));
        index.waitTask(Long.toString(task.getLong("taskID")));
        JSONObject settings = index.getSettings();
        assertEquals("firstname", settings.getJSONArray("attributesToRetrieve").getString(0));
    }

    @Test
    public void test14_index() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"), "a/go/?à");
        index.waitTask(task.getString("taskID"));
        JSONObject res = client.listIndexes();
        assertTrue(isPresent(res.getJSONArray("items"), indexName, "name"));
        client.deleteIndex(indexName);
        waitForIt();
        JSONObject resAfter = client.listIndexes();
        assertFalse(isPresent(resAfter.getJSONArray("items"), indexName, "name"));
    }

    @Test
    public void test15_addObjects() throws JSONException, AlgoliaException {
        List<JSONObject> array = new ArrayList<JSONObject>();
        array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
        array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach"));
        JSONObject task = index.addObjects(array);
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query(""));
        assertEquals(2, res.getInt("nbHits"));
    }

    @Test
    public void test15_deleteObjects() throws JSONException, AlgoliaException {
        List<JSONObject> array = new ArrayList<JSONObject>();
        array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "à/go/?à"));
        array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "à/go/?à2"));
        JSONObject task = index.addObjects(array);
        index.waitTask(task.getString("taskID"));
        List<String> deleted = new ArrayList<String>();
        deleted.add("à/go/?à");
        deleted.add("à/go/?à2");
        task = index.deleteObjects(deleted);
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query(""));
        assertEquals(0, res.getInt("nbHits"));
    }

    @Test
    public void test15_addObjectsList() throws JSONException, AlgoliaException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
        array.put(new JSONObject().put("firstname", "Warren").put("lastname", "Speach"));
        JSONObject task = index.addObjects(array);
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query(""));
        assertEquals(2, res.getInt("nbHits"));
    }

    @Test
    public void test16_saveObjects() throws JSONException, AlgoliaException {
        List<JSONObject> array = new ArrayList<JSONObject>();
        array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "a/go/?à"));
        array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "a/go/ià"));
        JSONObject task = index.saveObjects(array);
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query(""));
        assertEquals(2, res.getInt("nbHits"));
    }

    @Test
    public void test17_partialUpdateObjects() throws JSONException, AlgoliaException {
        List<JSONObject> array = new ArrayList<JSONObject>();
        array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "a/go/?à"));
        array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "a/go/ià"));
        JSONObject task = index.saveObjects(array);
        index.waitTask(task.getString("taskID"));
        array = new ArrayList<JSONObject>();
        array.add(new JSONObject().put("firstname", "Roger").put("objectID", "a/go/?à"));
        array.add(new JSONObject().put("firstname", "Robert").put("objectID", "a/go/ià"));
        task = index.partialUpdateObjects(array);
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("Ro"));
        assertEquals(2, res.getInt("nbHits"));
    }

    @Test
    public void test17_partialUpdateObjectsList() throws JSONException, AlgoliaException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "a/go/?à"));
        array.put(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "a/go/ià"));
        JSONObject task = index.saveObjects(array);
        index.waitTask(task.getString("taskID"));

        array = new JSONArray();
        array.put(new JSONObject().put("firstname", "Roger").put("objectID", "a/go/?à"));
        array.put(new JSONObject().put("firstname", "Robert").put("objectID", "a/go/ià"));

        task = index.partialUpdateObjects(array);
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("Ro"));
        assertEquals(2, res.getInt("nbHits"));
    }

    @Test
    public void test18_user_key_index() throws AlgoliaException, JSONException {
        JSONObject newKey = index.addUserKey(Collections.singletonList("search"));
        waitForIt();
        assertTrue(!newKey.getString("key").equals(""));
        JSONObject res = index.listUserKeys();
        assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
        JSONObject getKey = index.getUserKeyACL(newKey.getString("key"));
        assertEquals(newKey.getString("key"), getKey.getString("value"));
        index.updateUserKey(newKey.getString("key"), Collections.singletonList("addObject"));
        waitForIt();
        getKey = index.getUserKeyACL(newKey.getString("key"));
        assertEquals(getKey.getJSONArray("acl").get(0), "addObject");
        index.deleteUserKey(getKey.getString("value"));
        waitForIt();
        JSONObject resAfter = index.listUserKeys();
        assertTrue(!isPresent(resAfter.getJSONArray("keys"), newKey.getString("key"), "value"));
    }

    @Test
    public void test19_user_key() throws AlgoliaException, JSONException {
        JSONObject newKey = client.addUserKey(Collections.singletonList("search"));
        waitForIt();
        assertTrue(!newKey.getString("key").equals(""));
        JSONObject res = client.listUserKeys();
        assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
        JSONObject getKey = client.getUserKeyACL(newKey.getString("key"));
        assertEquals(newKey.getString("key"), getKey.getString("value"));
        client.updateUserKey(newKey.getString("key"), Collections.singletonList("addObject"));
        waitForIt();
        getKey = client.getUserKeyACL(newKey.getString("key"));
        assertEquals(getKey.getJSONArray("acl").get(0), "addObject");
        client.deleteUserKey(getKey.getString("value"));
        waitForIt();
        JSONObject resAfter = client.listUserKeys();
        assertTrue(!isPresent(resAfter.getJSONArray("keys"), newKey.getString("key"), "value"));
    }

    @Test
    public void test20_moveIndex() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        task = client.moveIndex(indexName, indexName + "2");
        Index newIndex = client.initIndex(indexName + "2");
        newIndex.waitTask(task.getString("taskID"));
        Query query = new Query();
        query.setQueryType(QueryType.PREFIX_ALL);
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
    public void test21_copyIndex() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        task = client.copyIndex(indexName, indexName + "2");
        Index newIndex = client.initIndex(indexName + "2");
        newIndex.waitTask(task.getString("taskID"));
        JSONObject res = newIndex.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
        res = index.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
        client.deleteIndex(indexName + "2");
    }

    @Test
    public void test22_browse() throws AlgoliaException, JSONException {
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.browse(0);
        assertEquals(1, res.getInt("nbHits"));
        res = index.browse(0, 1);
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test23_logs() throws AlgoliaException, JSONException {
        JSONObject res = client.getLogs();
        assertTrue(res.getJSONArray("logs").length() > 0);
        res = client.getLogs(0, 1);
        assertTrue(res.getJSONArray("logs").length() == 1);
        res = client.getLogs(0, 1, false);
        assertTrue(res.getJSONArray("logs").length() == 1);
        res = client.getLogs(0, 1, LogType.LOG_ALL);
        assertTrue(res.getJSONArray("logs").length() == 1);
    }

    @Test
    public void test24_EmptyAPPID() {
        try {
            new APIClient(null, "algolia");
            assertTrue(false);
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test25_EmptyAPPKEY() {
        try {
            new APIClient("algolia", null);
            assertTrue(false);
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test26_EmptyHost() {
        try {
            new APIClient("algolia", "algolia", new ArrayList<String>());
            assertTrue(false);
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test27_headerDisableRateLimit() throws AlgoliaException, JSONException {
        client.disableRateLimitForward();
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query());
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test29_user_keyLimit() throws AlgoliaException, JSONException {
        JSONObject newKey = client.addUserKey(Collections.singletonList("search"), 0, 2, 2);
        waitForIt();
        assertTrue(!newKey.getString("key").equals(""));
        JSONObject res = client.listUserKeys();
        assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
        index.deleteUserKey(newKey.getString("key"));
    }

    @Test
    public void test30_user_key_indexLimit() throws AlgoliaException, JSONException {
        JSONObject newKey = index.addUserKey(Collections.singletonList("search"), 0, 2, 2);
        waitForIt();
        assertTrue(!newKey.getString("key").equals(""));
        JSONObject res = index.listUserKeys();
        assertTrue(isPresent(res.getJSONArray("keys"), newKey.getString("key"), "value"));
        index.deleteUserKey(newKey.getString("key"));
    }

    @Test
    public void test31_InvalidKey() {
        try {
            APIClient client = new APIClient("unreach", "test");
            client.listIndexes();
            assertTrue(false);
        } catch (AlgoliaException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test32_InvalidObjectID() {
        try {
            index.deleteObject("");
            assertTrue(false);
        } catch (AlgoliaException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test33_customBatch() throws AlgoliaException, JSONException {
        assertEquals(indexName, index.getIndexName());
        JSONObject task = index.addObject(new JSONObject()
                .put("firstname", "Jimmie")
                .put("lastname", "Barninger")
                .put("followers", 93)
                .put("company", "California Paint"));
        index.waitTask(task.getString("taskID"));
        JSONObject res = index.search(new Query("jimie"));
        assertEquals(1, res.getInt("nbHits"));
        JSONArray actions = new JSONArray();
        JSONObject action = new JSONObject();
        action.put("action", "deleteObject");
        action.put("objectID", "a/go/?à");
        actions.put(action);
        task = index.batch(actions);
        index.waitTask(task.getString("taskID"));
    }

    @Test
    public void test34_newSecuredApiKeys() throws InvalidKeyException, NoSuchAlgorithmException {
        assertEquals("MDZkNWNjNDY4M2MzMDA0NmUyNmNkZjY5OTMzYjVlNmVlMTk1NTEwMGNmNTVjZmJhMmIwOTIzYjdjMTk2NTFiMnRhZ0ZpbHRlcnM9JTI4cHVibGljJTJDdXNlcjElMjk=", client.generateSecuredApiKey("182634d8894831d5dbce3b3185c50881", "(public,user1)"));
        assertEquals("MDZkNWNjNDY4M2MzMDA0NmUyNmNkZjY5OTMzYjVlNmVlMTk1NTEwMGNmNTVjZmJhMmIwOTIzYjdjMTk2NTFiMnRhZ0ZpbHRlcnM9JTI4cHVibGljJTJDdXNlcjElMjk=", client.generateSecuredApiKey("182634d8894831d5dbce3b3185c50881", new Query().setTagFilters("(public,user1)")));
        assertEquals("OGYwN2NlNTdlOGM2ZmM4MjA5NGM0ZmYwNTk3MDBkNzMzZjQ0MDI3MWZjNTNjM2Y3YTAzMWM4NTBkMzRiNTM5YnRhZ0ZpbHRlcnM9JTI4cHVibGljJTJDdXNlcjElMjkmdXNlclRva2VuPTQy", client.generateSecuredApiKey("182634d8894831d5dbce3b3185c50881", new Query().setTagFilters("(public,user1)").setUserToken("42")));
    }

    @Test
    public void test34_multipleQueries() throws AlgoliaException, JSONException {
        JSONObject obj = index.addObject(new JSONObject().put("i", 42).put("s", "foo").put("b", true));
        index.waitTask(obj.getString("taskID"));
        List<APIClient.IndexQuery> queries = new ArrayList<APIClient.IndexQuery>();
        queries.add(new APIClient.IndexQuery(safe_name("àlgol?à-java"), new Query("")));
        JSONObject res = client.multipleQueries(queries);
        assertEquals(1, res.getJSONArray("results").length());
        assertEquals(1, res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").length());
        assertEquals("foo", res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").getJSONObject(0).getString("s"));
        assertEquals(42, res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").getJSONObject(0).getLong("i"));
        assertEquals(true, res.getJSONArray("results").getJSONObject(0).getJSONArray("hits").getJSONObject(0).getBoolean("b"));
    }

    @Test
    public void test35_getObjects() throws AlgoliaException, JSONException {
        JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
                .put("name", "Los Angeles").put("objectID", "1")).put(new JSONObject()
                .put("name", "San Francisco").put("objectID", "2")));
        index.waitTask(task.getString("taskID"));
        List<String> objectIDs = new ArrayList<String>();
        objectIDs.add("1");
        objectIDs.add("2");
        JSONObject object = index.getObjects(objectIDs);
        assertEquals("Los Angeles", object.getJSONArray("results").getJSONObject(0).getString("name"));
        assertEquals("San Francisco", object.getJSONArray("results").getJSONObject(1).getString("name"));
    }

    @Test
    public void test36_deleteByQuery() throws JSONException, AlgoliaException {
        JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
                .put("name", "Washington"))
                .put(new JSONObject().put("name", "San Francisco"))
                .put(new JSONObject().put("name", "San Jose")));
        index.waitTask(task.getString("taskID"));
        index.deleteByQuery(new Query("San"));
        JSONObject res = index.search(new Query(""));
        assertEquals(1, res.getInt("nbHits"));
    }

    @Test
    public void test37_disjunctiveFaceting() throws AlgoliaException, JSONException {
        index.setSettings(new JSONObject("{\"attributesForFaceting\":[\"city\", \"stars\", \"facilities\"]}"));
        JSONObject task = index.addObjects(new JSONArray()
                .put(new JSONObject("{\"name\":\"Hotel A\", \"stars\":\"*\", \"facilities\":[\"wifi\", \"bath\", \"spa\"], \"city\":\"Paris\"}"))
                .put(new JSONObject("{\"name\":\"Hotel B\", \"stars\":\"*\", \"facilities\":[\"wifi\"], \"city\":\"Paris\"}"))
                .put(new JSONObject("{\"name\":\"Hotel C\", \"stars\":\"**\", \"facilities\":[\"bath\"], \"city\":\"San Fancisco\"}"))
                .put(new JSONObject("{\"name\":\"Hotel D\", \"stars\":\"****\", \"facilities\":[\"spa\"], \"city\":\"Paris\"}"))
                .put(new JSONObject("{\"name\":\"Hotel E\", \"stars\":\"****\", \"facilities\":[\"spa\"], \"city\":\"New York\"}")));
        index.waitTask(task.getString("taskID"));
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
    public void test38_keepAlive() throws AlgoliaException, JSONException {
        JSONObject task = index.addObjects(new JSONArray().put(new JSONObject()
                .put("name", "Los Angeles").put("objectID", "1")).put(new JSONObject()
                .put("name", "San Francisco").put("objectID", "2")));
        index.waitTask(task.getString("taskID"));
        waitForIt();
        // Redefine a client to break the current keep alive
        String applicationID = System.getenv("ALGOLIA_APPLICATION_ID");
        String apiKey = System.getenv("ALGOLIA_API_KEY");
        client = new APIClient(applicationID, apiKey);
        index = client.initIndex(indexName);

        double firstDSNQuery = 0;
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
        assertTrue(2.0 < firstDSNQuery / avgDSNQuery);
    }

    @Test
    public void test39_customBatchIndexes() throws AlgoliaException, JSONException {
        List<JSONObject> array = new ArrayList<JSONObject>();
        array.add(new JSONObject().put("action", "addObject").put("indexName", index.getIndexName()).put("body", new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger")));
        array.add(new JSONObject().put("action", "addObject").put("indexName", index.getIndexName()).put("body", new JSONObject().put("firstname", "Warren").put("lastname", "Speach")));
        JSONObject task = client.batch(array);
        index.waitTask(task.getJSONObject("taskID").getString(index.getIndexName()));
        JSONObject res = index.search(new Query(""));
        assertEquals(2, res.getInt("nbHits"));
    }

    @Test
    public void test40_checkFallBack() throws AlgoliaException, JSONException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
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
    public void test41_browseWithCursor() throws AlgoliaException, JSONException {
        List<JSONObject> objects = new ArrayList<JSONObject>();
        for (int i = 0; i < 1500; ++i) {
            objects.add(new JSONObject().put("objectID", i).put("i", i));
        }
        JSONObject task = index.addObjects(objects);
        index.waitTask(task.getString("taskID"));
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
