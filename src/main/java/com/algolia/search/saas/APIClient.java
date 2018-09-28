package com.algolia.search.saas;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.VersionInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Entry point in the Java API.
 * You should instantiate a Client object with your ApplicationID, ApiKey and Hosts
 * to start using Algolia Search API
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class APIClient {
  private final static String version;
  private final static String fallbackDomain;

  static {
    String tmp = "N/A";
    try {
      InputStream versionStream = APIClient.class.getResourceAsStream("/version.properties");
      if (versionStream != null) {
        BufferedReader versionReader = new BufferedReader(new InputStreamReader(versionStream));
        tmp = versionReader.readLine();
        versionReader.close();
      }
    } catch (IOException e) {
      // not fatal
    }
    version = tmp;

    fallbackDomain = getFallbackDomain();
  }

  private final String applicationID;
  private final String apiKey;
  private final List<String> buildHostsArray;
  private final List<String> queryHostsArray;
  private final HttpClient httpClient;
  private final boolean verbose;
  private int httpSocketTimeoutMS = 20000;
  private int httpConnectTimeoutMS = 2000;
  private int httpSearchTimeoutMS = 2000;
  private int hostDownTimeoutMS = 5 * 60 * 1000; //5 minutes
  private String forwardRateLimitAPIKey;
  private String forwardEndUserIP;
  private String forwardAdminAPIKey;
  private HashMap<String, String> headers;
  private String userAgent;
  private Map<String, HostStatus> hostStatuses = new HashMap<String, HostStatus>();

  /**
   * Algolia Search initialization
   *
   * @param applicationID the application ID you have in your admin interface
   * @param apiKey        a valid API key for the service
   */
  public APIClient(String applicationID, String apiKey) {
    this(applicationID, apiKey, Arrays.asList(applicationID + "-1." + fallbackDomain,
      applicationID + "-2." + fallbackDomain,
      applicationID + "-3." + fallbackDomain));
    this.buildHostsArray.add(0, applicationID + ".algolia.net");
    this.queryHostsArray.add(0, applicationID + "-dsn.algolia.net");
  }

  /**
   * Algolia Search initialization
   *
   * @param applicationID the application ID you have in your admin interface
   * @param apiKey        a valid API key for the service
   * @param hostsArray    the list of hosts that you have received for the service
   */
  public APIClient(String applicationID, String apiKey, List<String> hostsArray) {
    this(applicationID, apiKey, hostsArray, hostsArray);
  }

  /**
   * Algolia Search initialization
   *
   * @param applicationID   the application ID you have in your admin interface
   * @param apiKey          a valid API key for the service
   * @param buildHostsArray the list of hosts that you have received for the service
   * @param queryHostsArray the list of hosts that you have received for the service
   */
  public APIClient(String applicationID, String apiKey, List<String> buildHostsArray, List<String> queryHostsArray) {
    userAgent = "Algolia for Java (" + version + "); JVM (" + System.getProperty("java.version") + ")";
    verbose = System.getenv("VERBOSE") != null;
    forwardRateLimitAPIKey = forwardAdminAPIKey = forwardEndUserIP = null;
    if (applicationID == null || applicationID.length() == 0) {
      throw new RuntimeException("AlgoliaSearch requires an applicationID.");
    }
    this.applicationID = applicationID;
    if (apiKey == null || apiKey.length() == 0) {
      throw new RuntimeException("AlgoliaSearch requires an apiKey.");
    }
    this.apiKey = apiKey;
    if (buildHostsArray == null || buildHostsArray.size() == 0 || queryHostsArray == null || queryHostsArray.size() == 0) {
      throw new RuntimeException("AlgoliaSearch requires a list of hostnames.");
    }

    this.buildHostsArray = new ArrayList<String>(buildHostsArray);
    this.queryHostsArray = new ArrayList<String>(queryHostsArray);

    HttpClientBuilder builder = HttpClientBuilder.create().disableAutomaticRetries();
    //If we are on AppEngine don't use system properties
    if (System.getProperty("com.google.appengine.runtime.version") == null) {
      builder = builder.useSystemProperties();
    }
    this.httpClient = builder.build();
    this.headers = new HashMap<String, String>();
  }

  private static String hmac(String key, String msg) {
    Mac hmac;
    try {
      hmac = Mac.getInstance("HmacSHA256");
    } catch (NoSuchAlgorithmException e) {
      throw new Error(e);
    }
    try {
      hmac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
    } catch (InvalidKeyException e) {
      throw new Error(e);
    }
    byte[] rawHmac = hmac.doFinal(msg.getBytes());
    byte[] hexBytes = new Hex().encode(rawHmac);
    return new String(hexBytes);
  }

  /**
   * Get the appropriate fallback domain depending on the current SNI support.
   * Checks Java version and Apache HTTP Client's version.
   *
   * @return algolianet.com if the current setup supports SNI, else algolia.net.
   */
  static String getFallbackDomain() {
    int javaVersion = getJavaVersion();
    boolean javaHasSNI = javaVersion >= 7;

    final VersionInfo vi = VersionInfo.loadVersionInfo
      ("org.apache.http.client", APIClient.class.getClassLoader());
    String version = vi.getRelease();
    String[] split = version.split("\\.");
    int major = Integer.parseInt(split[0]);
    int minor = Integer.parseInt(split[1]);
    int patch = Integer.parseInt(split[2]);
    boolean apacheClientHasSNI = major > 4 ||
      major == 4 && minor > 3 ||
      major == 4 && minor == 3 && patch >= 2; // if version >= 4.3.2

    if (apacheClientHasSNI && javaHasSNI) {
      return "algolianet.com";
    } else {
      return "algolia.net";
    }
  }

  static int getJavaVersion() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      version = version.substring(2);
    }
    // Allow these formats:
    // 1.8.0_72-ea
    // 9-ea
    // 9
    // 9.0.1
    int dotPos = version.indexOf('.');
    int dashPos = version.indexOf('-');
    return Integer.parseInt(version.substring(0,
            dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : 1));
  }

  /**
   * Allow to modify the user-agent in order to add the user agent of the integration
   */
  public void setUserAgent(String agent, String agentVersion) {
    userAgent = String.format("Algolia for Java (%s); JVM (%s); %s (%s)", version, System.getProperty("java.version"), agent, agentVersion);
  }

  /**
   * Allow to use IP rate limit when you have a proxy between end-user and Algolia.
   * This option will set the X-Forwarded-For HTTP header with the client IP and the X-Forwarded-API-Key with the API Key having rate limits.
   *
   * @param adminAPIKey     the admin API Key you can find in your dashboard
   * @param endUserIP       the end user IP (you can use both IPV4 or IPV6 syntax)
   * @param rateLimitAPIKey the API key on which you have a rate limit
   */
  public void enableRateLimitForward(String adminAPIKey, String endUserIP, String rateLimitAPIKey) {
    this.forwardAdminAPIKey = adminAPIKey;
    this.forwardEndUserIP = endUserIP;
    this.forwardRateLimitAPIKey = rateLimitAPIKey;
  }

  /**
   * Disable IP rate limit enabled with enableRateLimitForward() function
   */
  public void disableRateLimitForward() {
    forwardAdminAPIKey = forwardEndUserIP = forwardRateLimitAPIKey = null;
  }

  /**
   * Allow to set custom headers
   */
  public void setExtraHeader(String key, String value) {
    headers.put(key, value);
  }

  /**
   * Allow to set the timeout
   *
   * @param connectTimeout connection timeout in MS
   * @param readTimeout    socket timeout in MS
   */
  public void setTimeout(int connectTimeout, int readTimeout) {
    httpSocketTimeoutMS = readTimeout;
    httpConnectTimeoutMS = connectTimeout;
  }

  /**
   * Allow to set the timeout for a down host
   *
   * @param hostDownTimeoutMS host down timeout in MS
   */
  public void setHostDownTimeoutMS(int hostDownTimeoutMS) {
    this.hostDownTimeoutMS = hostDownTimeoutMS;
  }

  /**
   * List all existing indexes
   * return an JSON Object in the form:
   * { "items": [ {"name": "contacts", "createdAt": "2013-01-18T15:33:13.556Z"},
   * {"name": "notes", "createdAt": "2013-01-18T15:33:13.556Z"}]}
   */
  public JSONObject listIndexes() throws AlgoliaException {
    return this.listIndexes(RequestOptions.empty);
  }

  /**
   * List all existing indexes
   * return an JSON Object in the form:
   * { "items": [ {"name": "contacts", "createdAt": "2013-01-18T15:33:13.556Z"},
   * {"name": "notes", "createdAt": "2013-01-18T15:33:13.556Z"}]}
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject listIndexes(RequestOptions requestOptions) throws AlgoliaException {
    return getRequest("/1/indexes/", false, requestOptions);
  }

  /**
   * Delete an index
   *
   * @param indexName the name of index to delete
   *                  return an object containing a "deletedAt" attribute
   */
  public JSONObject deleteIndex(String indexName) throws AlgoliaException {
    return this.deleteIndex(indexName, RequestOptions.empty);
  }

  /**
   * Delete an index
   *
   * @param indexName      the name of index to delete
   *                       return an object containing a "deletedAt" attribute
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteIndex(String indexName, RequestOptions requestOptions) throws AlgoliaException {
    try {
      return deleteRequest("/1/indexes/" + URLEncoder.encode(indexName, "UTF-8"), requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    }
  }

  /**
   * Move an existing index.
   *
   * @param srcIndexName the name of index to copy.
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   */
  public JSONObject moveIndex(String srcIndexName, String dstIndexName) throws AlgoliaException {
    return operationOnIndex("move", srcIndexName, dstIndexName, null, RequestOptions.empty);
  }

  /**
   * Move an existing index.
   *
   * @param srcIndexName   the name of index to copy.
   * @param dstIndexName   the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   * @param requestOptions Options to pass to this request
   */
  public JSONObject moveIndex(String srcIndexName, String dstIndexName, RequestOptions requestOptions) throws AlgoliaException {
    return operationOnIndex("move", srcIndexName, dstIndexName, null, requestOptions);
  }

  /**
   * Copy an existing index.
   *
   * @param srcIndexName the name of index to copy.
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   */
  public JSONObject copyIndex(String srcIndexName, String dstIndexName) throws AlgoliaException {
    return operationOnIndex("copy", srcIndexName, dstIndexName, null, RequestOptions.empty);
  }

  /**
   * Copy an existing index.
   *
   * @param srcIndexName   the name of index to copy.
   * @param dstIndexName   the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   * @param requestOptions Options to pass to this request
   */
  public JSONObject copyIndex(String srcIndexName, String dstIndexName, RequestOptions requestOptions) throws AlgoliaException {
    return operationOnIndex("copy", srcIndexName, dstIndexName, null, requestOptions);
  }

  /**
   * Copy an existing index.
   *
   * @param srcIndexName the name of index to copy.
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   * @param scopes       the list of scopes to copy
   */
  public JSONObject copyIndex(String srcIndexName, String dstIndexName, List<String> scopes) throws AlgoliaException {
    return operationOnIndex("copy", srcIndexName, dstIndexName, scopes, RequestOptions.empty);
  }

  /**
   * Copy an existing index.
   *
   * @param srcIndexName   the name of index to copy.
   * @param dstIndexName   the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
   * @param scopes         the list of scopes to copy
   * @param requestOptions Options to pass to this request
   */
  public JSONObject copyIndex(String srcIndexName, String dstIndexName, List<String> scopes, RequestOptions requestOptions) throws AlgoliaException {
    return operationOnIndex("copy", srcIndexName, dstIndexName, scopes, requestOptions);
  }

  private JSONObject operationOnIndex(String operation, String srcIndexName, String dstIndexName, List<String> scopes, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONObject content = new JSONObject();
      content.put("operation", operation);
      content.put("destination", dstIndexName);
      if(scopes != null) {
        content.put("scopes", scopes);
      }
      return postRequest("/1/indexes/" + URLEncoder.encode(srcIndexName, "UTF-8") + "/operation", content.toString(), true, false, requestOptions);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage()); // $COVERAGE-IGNORE$
    }
  }

  /**
   * Return 10 last log entries.
   */
  public JSONObject getLogs() throws AlgoliaException {
    return this.getLogs(RequestOptions.empty);
  }

  /**
   * Return 10 last log entries.
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getLogs(RequestOptions requestOptions) throws AlgoliaException {
    return getRequest("/1/logs", false, requestOptions);
  }

  /**
   * Return last logs entries.
   *
   * @param offset Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   */
  public JSONObject getLogs(int offset, int length) throws AlgoliaException {
    return getLogs(offset, length, LogType.LOG_ALL);
  }

  /**
   * Return last logs entries.
   *
   * @param offset         Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length         Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getLogs(int offset, int length, RequestOptions requestOptions) throws AlgoliaException {
    return getLogs(offset, length, LogType.LOG_ALL, requestOptions);
  }

  /**
   * Return last logs entries.
   *
   * @param offset     Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length     Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   * @param onlyErrors Retrieve only logs with an httpCode different than 200 and 201
   */
  public JSONObject getLogs(int offset, int length, boolean onlyErrors) throws AlgoliaException {
    return getLogs(offset, length, onlyErrors ? LogType.LOG_ERROR : LogType.LOG_ALL);
  }

  /**
   * Return last logs entries.
   *
   * @param offset         Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length         Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   * @param onlyErrors     Retrieve only logs with an httpCode different than 200 and 201
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getLogs(int offset, int length, boolean onlyErrors, RequestOptions requestOptions) throws AlgoliaException {
    return getLogs(offset, length, onlyErrors ? LogType.LOG_ERROR : LogType.LOG_ALL, requestOptions);
  }

  /**
   * Return last logs entries.
   *
   * @param offset  Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length  Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   * @param logType Specify the type of log to retrieve
   */
  public JSONObject getLogs(int offset, int length, LogType logType) throws AlgoliaException {
    return this.getLogs(offset, length, logType, RequestOptions.empty);
  }

  /**
   * Return last logs entries.
   *
   * @param offset         Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
   * @param length         Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
   * @param logType        Specify the type of log to retrieve
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getLogs(int offset, int length, LogType logType, RequestOptions requestOptions) throws AlgoliaException {
    String type = null;
    switch (logType) {
      case LOG_BUILD:
        type = "build";
        break;
      case LOG_QUERY:
        type = "query";
        break;
      case LOG_ERROR:
        type = "error";
        break;
      case LOG_ALL:
        type = "all";
        break;
    }
    return getRequest("/1/logs?offset=" + offset + "&length=" + length + "&type=" + type, false, requestOptions);
  }

  /**
   * Get the index object initialized (no server call needed for initialization)
   *
   * @param indexName the name of index
   */
  public Index initIndex(String indexName) {
    return new Index(this, indexName);
  }

  /**
   * Deprecated: use listApiKeys
   */
  @Deprecated
  public JSONObject listUserKeys() throws AlgoliaException {
    return listApiKeys();
  }

  /**
   * List all existing api keys with their associated ACLs
   */
  public JSONObject listApiKeys() throws AlgoliaException {
    return this.listApiKeys(RequestOptions.empty);
  }

  /**
   * List all existing api keys with their associated ACLs
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject listApiKeys(RequestOptions requestOptions) throws AlgoliaException {
    return getRequest("/1/keys", false, requestOptions);
  }

  /**
   * Deprecated: use getApiKey
   */
  @Deprecated
  public JSONObject getUserKeyACL(String key) throws AlgoliaException {
    return getApiKey(key);
  }

  /**
   * Get an api key
   */
  public JSONObject getApiKey(String key) throws AlgoliaException {
    return this.getApiKey(key, RequestOptions.empty);
  }

  /**
   * Get an api key
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject getApiKey(String key, RequestOptions requestOptions) throws AlgoliaException {
    return getRequest("/1/keys/" + key, false, requestOptions);
  }

  /**
   * Deprecated: use deleteApiKey
   */
  @Deprecated
  public JSONObject deleteUserKey(String key) throws AlgoliaException {
    return deleteApiKey(key);
  }

  /**
   * Delete an existing api key
   */
  public JSONObject deleteApiKey(String key) throws AlgoliaException {
    return this.deleteApiKey(key, RequestOptions.empty);
  }

  /**
   * Delete an existing api key
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject deleteApiKey(String key, RequestOptions requestOptions) throws AlgoliaException {
    return deleteRequest("/1/keys/" + key, requestOptions);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(JSONObject params) throws AlgoliaException {
    return addApiKey(params);
  }

  /**
   * Create a new api key
   *
   * @param params the list of parameters for this key. Defined by a JSONObject that
   *               can contains the following values:
   *               - acl: array of string
   *               - indices: array of string
   *               - validity: int
   *               - referers: array of string
   *               - description: string
   *               - maxHitsPerQuery: integer
   *               - queryParameters: string
   *               - maxQueriesPerIPPerHour: integer
   */
  public JSONObject addApiKey(JSONObject params) throws AlgoliaException {
    return this.addApiKey(params, RequestOptions.empty);
  }

  /**
   * Create a new api key
   *
   * @param params         the list of parameters for this key. Defined by a JSONObject that
   *                       can contains the following values:
   *                       - acl: array of string
   *                       - indices: array of string
   *                       - validity: int
   *                       - referers: array of string
   *                       - description: string
   *                       - maxHitsPerQuery: integer
   *                       - queryParameters: string
   *                       - maxQueriesPerIPPerHour: integer
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addApiKey(JSONObject params, RequestOptions requestOptions) throws AlgoliaException {
    return postRequest("/1/keys", params.toString(), true, false, requestOptions);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(List<String> acls) throws AlgoliaException {
    return addApiKey(acls);
  }

  /**
   * Create a new api key
   *
   * @param acls the list of ACL for this key. Defined by an array of strings that
   *             can contains the following values:
   *             - search: allow to search (https and http)
   *             - addObject: allows to add/update an object in the index (https only)
   *             - deleteObject : allows to delete an existing object (https only)
   *             - deleteIndex : allows to delete index content (https only)
   *             - settings : allows to get index settings (https only)
   *             - editSettings : allows to change index settings (https only)
   */
  public JSONObject addApiKey(List<String> acls) throws AlgoliaException {
    return addApiKey(acls, 0, 0, 0, null, RequestOptions.empty);
  }

  /**
   * Create a new api key
   *
   * @param acls           the list of ACL for this key. Defined by an array of strings that
   *                       can contains the following values:
   *                       - search: allow to search (https and http)
   *                       - addObject: allows to add/update an object in the index (https only)
   *                       - deleteObject : allows to delete an existing object (https only)
   *                       - deleteIndex : allows to delete index content (https only)
   *                       - settings : allows to get index settings (https only)
   *                       - editSettings : allows to change index settings (https only)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject addApiKey(List<String> acls, RequestOptions requestOptions) throws AlgoliaException {
    return addApiKey(acls, 0, 0, 0, null, requestOptions);
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, JSONObject params) throws AlgoliaException {
    return updateApiKey(key, params);
  }

  /**
   * Update an api key
   *
   * @param params the list of parameters for this key. Defined by a JSONObject that
   *               can contains the following values:
   *               - acl: array of string
   *               - indices: array of string
   *               - validity: int
   *               - referers: array of string
   *               - description: string
   *               - maxHitsPerQuery: integer
   *               - queryParameters: string
   *               - maxQueriesPerIPPerHour: integer
   */
  public JSONObject updateApiKey(String key, JSONObject params) throws AlgoliaException {
    return this.updateApiKey(key, params, RequestOptions.empty);
  }

  /**
   * Update an api key
   *
   * @param params         the list of parameters for this key. Defined by a JSONObject that
   *                       can contains the following values:
   *                       - acl: array of string
   *                       - indices: array of string
   *                       - validity: int
   *                       - referers: array of string
   *                       - description: string
   *                       - maxHitsPerQuery: integer
   *                       - queryParameters: string
   *                       - maxQueriesPerIPPerHour: integer
   * @param requestOptions Options to pass to this request
   */
  public JSONObject updateApiKey(String key, JSONObject params, RequestOptions requestOptions) throws AlgoliaException {
    return putRequest("/1/keys/" + key, params.toString(), requestOptions);
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, List<String> acls) throws AlgoliaException {
    return updateApiKey(key, acls);
  }

  /**
   * Update an api key
   *
   * @param acls the list of ACL for this key. Defined by an array of strings that
   *             can contains the following values:
   *             - search: allow to search (https and http)
   *             - addObject: allows to add/update an object in the index (https only)
   *             - deleteObject : allows to delete an existing object (https only)
   *             - deleteIndex : allows to delete index content (https only)
   *             - settings : allows to get index settings (https only)
   *             - editSettings : allows to change index settings (https only)
   */
  public JSONObject updateApiKey(String key, List<String> acls) throws AlgoliaException {
    return this.updateApiKey(key, acls, RequestOptions.empty);
  }

  /**
   * Update an api key
   *
   * @param acls           the list of ACL for this key. Defined by an array of strings that
   *                       can contains the following values:
   *                       - search: allow to search (https and http)
   *                       - addObject: allows to add/update an object in the index (https only)
   *                       - deleteObject : allows to delete an existing object (https only)
   *                       - deleteIndex : allows to delete index content (https only)
   *                       - settings : allows to get index settings (https only)
   *                       - editSettings : allows to change index settings (https only)
   * @param requestOptions Options to pass to this request
   */
  public JSONObject updateApiKey(String key, List<String> acls, RequestOptions requestOptions) throws AlgoliaException {
    return updateApiKey(key, acls, 0, 0, 0, null, requestOptions);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return addApiKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
  }

  /**
   * Create a new api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   */
  public JSONObject addApiKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return addApiKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, null, RequestOptions.empty);
  }

  /**
   * Create a new api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   * @param requestOptions         Options to pass to this request
   */
  public JSONObject addApiKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, RequestOptions requestOptions) throws AlgoliaException {
    return addApiKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, null, requestOptions);
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return updateApiKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery);
  }

  /**
   * Update an api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   */
  public JSONObject updateApiKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
    return updateApiKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, null, RequestOptions.empty);
  }

  /**
   * Update an api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   * @param requestOptions         Options to pass to this request
   */
  public JSONObject updateApiKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, RequestOptions requestOptions) throws AlgoliaException {
    return updateApiKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, null, requestOptions);
  }

  /**
   * Deprecated: use addApiKey
   */
  @Deprecated
  public JSONObject addUserKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) throws AlgoliaException {
    return addApiKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, indexes);
  }

  /**
   * Create a new user key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   * @param indexes                the list of targeted indexes
   */
  public JSONObject addApiKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) throws AlgoliaException {
    JSONObject jsonObject = generateUserKeyJson(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, indexes);
    return addApiKey(jsonObject);
  }

  /**
   * Create a new user key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   * @param indexes                the list of targeted indexes
   * @param requestOptions         Options to pass to this request
   */
  public JSONObject addApiKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes, RequestOptions requestOptions) throws AlgoliaException {
    JSONObject jsonObject = generateUserKeyJson(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, indexes);
    return addApiKey(jsonObject, requestOptions);
  }

  /**
   * Deprecated: use updateApiKey
   */
  @Deprecated
  public JSONObject updateUserKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) throws AlgoliaException {
    return updateApiKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, indexes);
  }

  /**
   * Update an api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   * @param indexes                the list of targeted indexes
   */
  public JSONObject updateApiKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) throws AlgoliaException {
    JSONObject jsonObject = generateUserKeyJson(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, indexes);
    return updateApiKey(key, jsonObject);
  }

  /**
   * Update an api key
   *
   * @param acls                   the list of ACL for this key. Defined by an array of strings that
   *                               can contains the following values:
   *                               - search: allow to search (https and http)
   *                               - addObject: allows to add/update an object in the index (https only)
   *                               - deleteObject : allows to delete an existing object (https only)
   *                               - deleteIndex : allows to delete index content (https only)
   *                               - settings : allows to get index settings (https only)
   *                               - editSettings : allows to change index settings (https only)
   * @param validity               the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
   * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
   * @param maxHitsPerQuery        Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
   * @param indexes                the list of targeted indexes
   * @param requestOptions         Options to pass to this request
   */
  public JSONObject updateApiKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes, RequestOptions requestOptions) throws AlgoliaException {
    JSONObject jsonObject = generateUserKeyJson(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, indexes);
    return updateApiKey(key, jsonObject, requestOptions);
  }

  private JSONObject generateUserKeyJson(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) {
    JSONArray array = new JSONArray(acls);
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("acl", array);
      jsonObject.put("validity", validity);
      jsonObject.put("maxQueriesPerIPPerHour", maxQueriesPerIPPerHour);
      jsonObject.put("maxHitsPerQuery", maxHitsPerQuery);
      if (indexes != null) {
        jsonObject.put("indexes", new JSONArray(indexes));
      }
    } catch (JSONException e) {
      throw new RuntimeException(e); // $COVERAGE-IGNORE$
    }
    return jsonObject;
  }

  /**
   * Generate a secured and public API Key from a list of tagFilters and an
   * optional user token identifying the current user
   *
   * @param privateApiKey your private API Key
   * @param tagFilters    the list of tags applied to the query (used as security)
   * @deprecated Use `generateSecuredApiKey(String privateApiKey, Query query)` version
   */
  @Deprecated
  public String generateSecuredApiKey(String privateApiKey, String tagFilters) throws NoSuchAlgorithmException, InvalidKeyException {
    if (!tagFilters.contains("="))
      return generateSecuredApiKey(privateApiKey, new Query().setTagFilters(tagFilters), null);
    else {
      return Base64.encodeBase64String(String.format("%s%s", hmac(privateApiKey, tagFilters), tagFilters).getBytes(Charset.forName("UTF8")));
    }
  }

  /**
   * Generate a secured and public API Key from a query and an
   * optional user token identifying the current user
   *
   * @param privateApiKey your private API Key
   * @param query         contains the parameter applied to the query (used as security)
   */
  public String generateSecuredApiKey(String privateApiKey, Query query) throws NoSuchAlgorithmException, InvalidKeyException {
    return generateSecuredApiKey(privateApiKey, query, null);
  }

  /**
   * Generate a secured and public API Key from a list of tagFilters and an
   * optional user token identifying the current user
   *
   * @param privateApiKey your private API Key
   * @param tagFilters    the list of tags applied to the query (used as security)
   * @param userToken     an optional token identifying the current user
   * @deprecated Use `generateSecuredApiKey(String privateApiKey, Query query, String userToken)` version
   */
  @Deprecated
  public String generateSecuredApiKey(String privateApiKey, String tagFilters, String userToken) throws NoSuchAlgorithmException, InvalidKeyException, AlgoliaException {
    if (!tagFilters.contains("="))
      return generateSecuredApiKey(privateApiKey, new Query().setTagFilters(tagFilters), userToken);
    else {
      if (userToken != null && userToken.length() > 0) {
        try {
          tagFilters = String.format("%s%s%s", tagFilters, "&userToken=", URLEncoder.encode(userToken, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          throw new AlgoliaException(e.getMessage());
        }
      }
      return Base64.encodeBase64String(String.format("%s%s", hmac(privateApiKey, tagFilters), tagFilters).getBytes(Charset.forName("UTF8")));
    }
  }

  /**
   * Generate a secured and public API Key from a query and an
   * optional user token identifying the current user
   *
   * @param privateApiKey your private API Key
   * @param query         contains the parameter applied to the query (used as security)
   * @param userToken     an optional token identifying the current user
   */
  public String generateSecuredApiKey(String privateApiKey, Query query, String userToken) throws NoSuchAlgorithmException, InvalidKeyException {
    if (userToken != null && userToken.length() > 0) {
      query.setUserToken(userToken);
    }
    String queryStr = query.getQueryString();
    String key = hmac(privateApiKey, queryStr);

    return Base64.encodeBase64String(String.format("%s%s", key, queryStr).getBytes(Charset.forName("UTF8")));
  }

  protected JSONObject getRequest(String url, boolean search, RequestOptions requestOptions) throws AlgoliaException {
    return _request(Method.GET, url, null, false, search, requestOptions);
  }

  protected JSONObject deleteRequest(String url, RequestOptions requestOptions) throws AlgoliaException {
    return _request(Method.DELETE, url, null, true, false, requestOptions);
  }

  protected JSONObject postRequest(String url, String obj, boolean build, boolean search, RequestOptions requestOptions) throws AlgoliaException {
    return _request(Method.POST, url, obj, build, search, requestOptions);
  }

  protected JSONObject putRequest(String url, String obj, RequestOptions requestOptions) throws AlgoliaException {
    return _request(Method.PUT, url, obj, true, false, requestOptions);
  }

  private JSONObject _requestByHost(HttpRequestBase req, String host, String url, String json, List<AlgoliaInnerException> errors, boolean searchTimeout, RequestOptions requestOptions) throws AlgoliaException {
    req.reset();

    // set URL
    try {
      req.setURI(new URI("https://" + host + url));
    } catch (URISyntaxException e) {
      // never reached
      throw new IllegalStateException(e);
    }

    // set auth headers
    req.setHeader("Accept-Encoding", "gzip");
    req.setHeader("X-Algolia-Application-Id", this.applicationID);
    if (forwardAdminAPIKey == null) {
      req.setHeader("X-Algolia-API-Key", this.apiKey);
    } else {
      req.setHeader("X-Algolia-API-Key", this.forwardAdminAPIKey);
      req.setHeader("X-Forwarded-For", this.forwardEndUserIP);
      req.setHeader("X-Forwarded-API-Key", this.forwardRateLimitAPIKey);
    }
    for (Entry<String, String> entry : headers.entrySet()) {
      req.setHeader(entry.getKey(), entry.getValue());
    }
    for (Entry<String, String> entry : requestOptions.generateExtraHeaders().entrySet()) {
      req.setHeader(entry.getKey(), entry.getValue());
    }

    // set user agent
    req.setHeader("User-Agent", userAgent);

    // set JSON entity
    if (json != null) {
      if (!(req instanceof HttpEntityEnclosingRequestBase)) {
        throw new IllegalArgumentException("Method " + req.getMethod() + " cannot enclose entity");
      }
      req.setHeader("Content-type", "application/json");
      try {
        StringEntity se = new StringEntity(json, "UTF-8");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        ((HttpEntityEnclosingRequestBase) req).setEntity(se);
      } catch (Exception e) {
        throw new AlgoliaException("Invalid JSON Object: " + json); // $COVERAGE-IGNORE$
      }
    }

    RequestConfig config = RequestConfig.custom()
      .setSocketTimeout(searchTimeout ? httpSearchTimeoutMS : httpSocketTimeoutMS)
      .setConnectTimeout(httpConnectTimeoutMS)
      .setConnectionRequestTimeout(httpConnectTimeoutMS)
      .build();
    req.setConfig(config);

    HttpResponse response;
    try {
      response = httpClient.execute(req);
    } catch (IOException e) {
      // on error continue on the next host
      if (verbose) {
        System.out.println(String.format("%s: %s=%s", host, e.getClass().getName(), e.getMessage()));
      }
      errors.add(new AlgoliaInnerException(host, e));
      return null;
    }
    try {
      int code = response.getStatusLine().getStatusCode();
      if (code / 100 == 4) {
        String message = "";
        try {
          message = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (code == 400) {
          throw new AlgoliaException(code, message.length() > 0 ? message : "Bad request");
        } else if (code == 403) {
          throw new AlgoliaException(code, message.length() > 0 ? message : "Invalid Application-ID or API-Key");
        } else if (code == 404) {
          throw new AlgoliaException(code, message.length() > 0 ? message : "Resource does not exist");
        } else {
          throw new AlgoliaException(code, message.length() > 0 ? message : "Error");
        }
      }
      if (code / 100 != 2) {
        try {
          if (verbose) {
            System.out.println(String.format("%s: %s", host, EntityUtils.toString(response.getEntity())));
          }
          errors.add(new AlgoliaInnerException(host, EntityUtils.toString(response.getEntity())));
        } catch (IOException e) {
          if (verbose) {
            System.out.println(String.format("%s: %s", host, String.valueOf(code)));
          }
          errors.add(new AlgoliaInnerException(host, e));
        }
        // KO, continue
        return null;
      }
      try {
        InputStream istream = response.getEntity().getContent();
        String encoding = response.getEntity().getContentEncoding() != null ? response.getEntity().getContentEncoding().getValue() : null;
        if (encoding != null && encoding.contains("gzip")) {
          istream = new GZIPInputStream(istream);
        }
        InputStreamReader is = new InputStreamReader(istream, "UTF-8");
        StringBuilder jsonRaw = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = is.read(buffer)) > 0) {
          jsonRaw.append(buffer, 0, read);
        }
        is.close();
        return new JSONObject(jsonRaw.toString());
      } catch (IOException e) {
        if (verbose) {
          System.out.println(String.format("%s: %s=%s", host, e.getClass().getName(), e.getMessage()));
        }
        errors.add(new AlgoliaInnerException(host, e));
        return null;
      } catch (JSONException e) {
        throw new AlgoliaException("JSON decode error:" + e.getMessage());
      }
    } finally {
      req.releaseConnection();
    }
  }

  private List<String> queryHostsThatAreUp() {
    return hostsThatAreUp(this.queryHostsArray);
  }

  private List<String> buildHostsThatAreUp() {
    return hostsThatAreUp(this.buildHostsArray);
  }

  private List<String> hostsThatAreUp(List<String> hosts) {
    List<String> result = new ArrayList<String>(hosts.size());
    for (String host : hosts) {
      if (isHostUpOrCouldBeRetried(host)) {
        result.add(host);
      }
    }

    if (result.isEmpty()) {
      return hosts;
    } else {
      return result;
    }
  }

  private boolean isHostUpOrCouldBeRetried(String host) {
    HostStatus status = hostStatuses.get(host);
    if (status == null) {
      hostStatuses.put(host, HostStatus.up());
      return true;
    }

    return status.isUp || (new Date().getTime() - status.lastModifiedTimestamp) >= hostDownTimeoutMS;
  }

  private JSONObject _request(Method m, String url, String json, boolean build, boolean search, RequestOptions requestOptions) throws AlgoliaException {
    HttpRequestBase req;
    switch (m) {
      case DELETE:
        req = new HttpDelete();
        break;
      case GET:
        req = new HttpGet();
        break;
      case POST:
        req = new HttpPost();
        break;
      case PUT:
        req = new HttpPut();
        break;
      default:
        throw new IllegalArgumentException("Method " + m + " is not supported");
    }
    List<AlgoliaInnerException> errors = new ArrayList<AlgoliaInnerException>();
    List<String> hosts = build ? buildHostsThatAreUp() : queryHostsThatAreUp();

    // for each host
    for (String host : hosts) {
      JSONObject res = _requestByHost(req, host, url, json, errors, search, requestOptions);
      if (res != null) {
        hostStatuses.put(host, HostStatus.up());
        return res;
      } else {
        hostStatuses.put(host, HostStatus.down());
      }
    }
    throw AlgoliaException.from("Hosts unreachable", errors);
  }

  /**
   * This method allows to query multiple indexes with one API call
   */
  public JSONObject multipleQueries(List<IndexQuery> queries) throws AlgoliaException {
    return multipleQueries(queries, "none", RequestOptions.empty);
  }

  /**
   * This method allows to query multiple indexes with one API call
   *
   * @param requestOptions Options to pass to this request
   */
  public JSONObject multipleQueries(List<IndexQuery> queries, RequestOptions requestOptions) throws AlgoliaException {
    return multipleQueries(queries, "none", requestOptions);
  }

  public JSONObject multipleQueries(List<IndexQuery> queries, String strategy, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONArray requests = new JSONArray();
      for (IndexQuery indexQuery : queries) {
        String paramsString = indexQuery.getQuery().getQueryString();
        requests.put(new JSONObject().put("indexName", indexQuery.getIndex()).put("params", paramsString));
      }
      JSONObject body = new JSONObject().put("requests", requests);
      return postRequest("/1/indexes/*/queries?strategy=" + strategy, body.toString(), false, true, requestOptions);
    } catch (JSONException e) {
      throw new AlgoliaException(e);
    }
  }

  /**
   * Custom batch
   *
   * @param actions the array of actions
   */
  public JSONObject batch(JSONArray actions) throws AlgoliaException {
    return postBatch(actions, RequestOptions.empty);
  }

  /**
   * Custom batch
   *
   * @param actions        the array of actions
   * @param requestOptions Options to pass to this request
   */
  public JSONObject batch(JSONArray actions, RequestOptions requestOptions) throws AlgoliaException {
    return postBatch(actions, requestOptions);
  }

  /**
   * Custom batch
   *
   * @param actions the array of actions
   */
  public JSONObject batch(List<JSONObject> actions) throws AlgoliaException {
    return postBatch(actions, RequestOptions.empty);
  }

  /**
   * Custom batch
   *
   * @param actions        the array of actions
   * @param requestOptions Options to pass to this request
   */
  public JSONObject batch(List<JSONObject> actions, RequestOptions requestOptions) throws AlgoliaException {
    return postBatch(actions, requestOptions);
  }

  private JSONObject postBatch(Object actions, RequestOptions requestOptions) throws AlgoliaException {
    try {
      JSONObject content = new JSONObject();
      content.put("requests", actions);
      return postRequest("/1/indexes/*/batch", content.toString(), true, false, requestOptions);
    } catch (JSONException e) {
      throw new AlgoliaException(e.getMessage());
    }
  }

  public enum LogType {
    /// all query logs
    LOG_QUERY,
    /// all build logs
    LOG_BUILD,
    /// all error logs
    LOG_ERROR,
    /// all logs
    LOG_ALL
  }

  private enum Method {
    GET, POST, PUT, DELETE
  }

  private static class HostStatus {
    public boolean isUp = true;
    public long lastModifiedTimestamp = new Date().getTime();

    public static HostStatus up() {
      return new HostStatus();
    }

    public static HostStatus down() {
      return new HostStatus().setDown();
    }

    private HostStatus setDown() {
      isUp = false;
      return this;
    }
  }

  public static class IndexQuery {
    private String index;
    private Query query;

    public IndexQuery(String index, Query q) {
      this.index = index;
      this.query = q;
    }

    public String getIndex() {
      return index;
    }

    public void setIndex(String index) {
      this.index = index;
    }

    public Query getQuery() {
      return query;
    }

    public void setQuery(Query query) {
      this.query = query;
    }
  }
}
