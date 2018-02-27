package com.algolia.search.saas;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RulesTest extends AlgoliaTest {

  private JSONObject generateRule(String objectID) throws JSONException {
    JSONObject condition = new JSONObject()
      .put("pattern", "my pattern")
      .put("anchoring", "is");
    JSONObject consequence = new JSONObject()
      .put("userData", new JSONObject().put("a", "b"));

    return new JSONObject()
      .put("objectID", objectID)
      .put("condition", condition)
      .put("consequence", consequence);
  }

  @Before
  public void before() throws AlgoliaException, JSONException {
    JSONObject clearRules = index.clearRules();
    index.waitTask(clearRules.getLong("taskID"));
  }

  @Test
  public void saveAndGetRule() throws JSONException, AlgoliaException {
    String ruleId = "rule1";

    JSONObject res = index.saveRule(ruleId, generateRule(ruleId));
    index.waitTask(res.getLong("taskID"));

    res = index.getRule(ruleId);
    assertEquals(ruleId, res.getString("objectID"));
  }

  @Test
  public void deleteRule() throws JSONException, AlgoliaException {
    String ruleId = "rule2";

    JSONObject res = index.saveRule(ruleId, generateRule(ruleId));
    index.waitTask(res.getLong("taskID"));

    res = index.deleteRule(ruleId);
    index.waitTask(res.getLong("taskID"));

    res = index.searchRules(new RuleQuery(""));
    assertEquals(0, res.getInt("nbHits"));
  }

  @Test
  public void clearRules() throws JSONException, AlgoliaException {
    String ruleId = "rule3";

    JSONObject res = index.saveRule(ruleId, generateRule(ruleId));
    index.waitTask(res.getLong("taskID"));


    res = index.clearRules();
    index.waitTask(res.getLong("taskID"));

    res = index.searchRules(new RuleQuery(""));
    assertEquals(0, res.getInt("nbHits"));
  }

  @Test
  public void batchSaveRules() throws JSONException, AlgoliaException {
    List<JSONObject> c = new ArrayList<JSONObject>();
    c.add(generateRule("rule4"));
    c.add(generateRule("rule5"));

    JSONObject res = index.batchRules(c);
    index.waitTask(res.getLong("taskID"));

    res = index.searchRules(new RuleQuery());
    assertEquals(2, res.getInt("nbHits"));
  }
}
