package com.algolia.search;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.algolia.search.Defaults.*;

@SuppressWarnings("WeakerAccess")
public abstract class APIClientBuilder {

  private final String applicationId;
  private final String apiKey;
  private String forwardAdminAPIKey;
  private String forwardEndUserIP;
  private String forwardRateLimitAPIKey;
  private String customAgent;
  private String customAgentVersion;
  private Map<String, String> customHeaders = new HashMap<>();
  private int connectTimeout = CONNECT_TIMEOUT_MS;
  private int readTimeout = READ_TIMEOUT_MS;
  private ObjectMapper objectMapper = DEFAULT_OBJECT_MAPPER;

  /**
   * Initialize this builder with the applicationId and apiKey
   *
   * @param applicationId APP_ID can be found on https://www.algolia.com/api-keys
   * @param apiKey        Algolia API_KEY can also be found on https://www.algolia.com/api-keys
   */
  public APIClientBuilder(String applicationId, String apiKey) {
    this.applicationId = applicationId;
    this.apiKey = apiKey;
  }

  /**
   * Customize the user agent
   *
   * @param customAgent        key to add to the user agent
   * @param customAgentVersion value of this key to add to the user agent
   * @return this
   */
  public APIClientBuilder setUserAgent(String customAgent, String customAgentVersion) {
    this.customAgent = customAgent;
    this.customAgentVersion = customAgentVersion;
    return this;
  }

  /**
   * @param forwardAdminAPIKey
   * @param forwardEndUserIP
   * @param forwardRateLimitAPIKey
   * @return this
   */
  public APIClientBuilder enableRateLimitForward(String forwardAdminAPIKey, String forwardEndUserIP, String forwardRateLimitAPIKey) {
    this.forwardAdminAPIKey = forwardAdminAPIKey;
    this.forwardEndUserIP = forwardEndUserIP;
    this.forwardRateLimitAPIKey = forwardRateLimitAPIKey;
    return this;
  }

  /**
   * Set extra headers to the requests
   *
   * @param key   name of the header
   * @param value value of the header
   * @return this
   */
  public APIClientBuilder setExtraHeader(String key, String value) {
    customHeaders.put(key, value);
    return this;
  }

  /**
   * Set the connect timeout of the HTTP client
   *
   * @param connectTimeout the value in ms
   * @return this
   */
  public APIClientBuilder setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
    return this;
  }

  /**
   * Set the read timeout of the HTTP client
   *
   * @param readTimeout the value in ms
   * @return this
   */
  public APIClientBuilder setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }

  /**
   * Set the Jackson ObjectMapper
   *
   * @param objectMapper the mapper
   * @return this
   */
  public APIClientBuilder setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    return this;
  }

  private String getApiClientVersion() {
    try (InputStream versionStream = getClass().getResourceAsStream("/version.properties")) {
      BufferedReader versionReader = new BufferedReader(new InputStreamReader(versionStream));
      return versionReader.readLine();
    } catch (IOException ignored) {
    }
    return "N/A";
  }

  protected abstract APIClient build(
    String applicationId,
    String apiKey,
    ObjectMapper objectMapper,
    List<String> buildHosts,
    List<String> queryHosts,
    int connectTimeout,
    HttpRequestInitializer httpRequestInitializer
  );

//  protected abstract AsyncAPIClient buildAsync(Executor executor, String applicationId, String apiKey, List<String> buildHosts, List<String> queryHosts, HttpRequestInitializer httpRequestInitializer);

  protected HttpRequestInitializer buildHttpRequestInitializer(int connectTimeout, int readTimeout, HttpHeaders headers) {
    return request -> request
      .setConnectTimeout(connectTimeout)
      .setReadTimeout(readTimeout)
      .setHeaders(headers);
  }

  protected List<String> generateBuildHosts() {
    return Arrays.asList(
      applicationId + "." + ALGOLIA_NET,
      applicationId + "-1." + ALGOLIANET_COM,
      applicationId + "-2." + ALGOLIANET_COM,
      applicationId + "-3." + ALGOLIANET_COM
    );
  }

  protected List<String> generateQueryHosts() {
    return Arrays.asList(
      applicationId + "-dsn." + ALGOLIA_NET,
      applicationId + "-1." + ALGOLIANET_COM,
      applicationId + "-2." + ALGOLIANET_COM,
      applicationId + "-3." + ALGOLIANET_COM
    );
  }

  protected HttpHeaders generateHeaders() {
    String userAgent = String.format("Algolia for Java %s API %s", System.getProperty("java.version"), getApiClientVersion());
    if (customAgent != null) {
      userAgent += " " + customAgent + " " + customAgentVersion;
    }

    HttpHeaders headers = new HttpHeaders()
      .setAcceptEncoding("gzip")
      .setContentType("application/json; charset=UTF-8")
      .setUserAgent(userAgent)
      .set("X-Algolia-Application-Id", applicationId);

    if (forwardAdminAPIKey == null) {
      headers = headers.set("X-Algolia-API-Key", apiKey);
    } else {
      headers = headers
        .set("X-Algolia-API-Key", forwardAdminAPIKey)
        .set("X-Forwarded-For", forwardEndUserIP)
        .set("X-Forwarded-API-Key", forwardRateLimitAPIKey);
    }

    headers.putAll(customHeaders);

    return headers;
  }

//  public AsyncAPIClient buildAsync() {
//    return buildAsync(
//      Executors.newFixedThreadPool(10),
//      applicationId,
//      apiKey,
//      generateBuildHosts(),
//      generateQueryHosts(),
//      buildHttpRequestInitializer(connectTimeout, readTimeout, generateHeaders())
//    );
//  }
//
//  public AsyncAPIClient buildAsync(Executor executor) {
//    return buildAsync(
//      executor,
//      applicationId,
//      apiKey,
//      generateBuildHosts(),
//      generateQueryHosts(),
//      buildHttpRequestInitializer(connectTimeout, readTimeout, generateHeaders())
//    );
//  }

  /**
   * Build the APIClient
   *
   * @return the built APIClient
   */
  public APIClient build() {
    return build(
      applicationId,
      apiKey,
      objectMapper,
      generateBuildHosts(),
      generateQueryHosts(),
      connectTimeout,
      buildHttpRequestInitializer(connectTimeout, readTimeout, generateHeaders())
    );
  }

}
