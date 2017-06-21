package com.algolia.search.saas;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NetworkTest {

  private String applicationID;
  private String apiKey;

  @Before
  public void init() {
    applicationID = System.getenv("ALGOLIA_APPLICATION_ID");
    apiKey = System.getenv("ALGOLIA_API_KEY");
    Assume.assumeFalse("You must set environement variables ALGOLIA_APPLICATION_ID and ALGOLIA_API_KEY to run the tests.", applicationID == null || apiKey == null);
  }

  @Test
  public void shouldHandleTimeoutsInDns() throws AlgoliaException {
    List<String> hosts = new ArrayList<String>();
    hosts.add("java-dsn.algolia.biz");
    hosts.add(applicationID + "-1." + APIClient.getFallbackDomain());

    APIClient client = new APIClient(applicationID, apiKey, hosts, hosts);

    Long start = System.currentTimeMillis();
    assertNotNull(client.listIndexes());
    assertTrue((System.currentTimeMillis() - start) < 3 * 1000);
  }

  @Test
  public void shouldHandleConnectTimeout() throws AlgoliaException {
    List<String> hosts = new ArrayList<String>();
    String fallbackDomain = APIClient.getFallbackDomain();
    hosts.add("notcp-xx-1.algolia.net");
    hosts.add(applicationID + "-1." + fallbackDomain);

    APIClient client = new APIClient(applicationID, apiKey, hosts, hosts);
    client.setTimeout(1000, 1000);

    long start = System.currentTimeMillis();
    assertNotNull(client.listIndexes());
    long totalTime = System.currentTimeMillis() - start;
    assertTrue("connect timeout should be < 3.5s, was " + totalTime, totalTime < 3500);
  }

  @Test
  public void shouldHandleMultipleConnectTimeout() {
    List<String> hosts = new ArrayList<String>();
    hosts.add("notcp-xx-1.algolia.net");
    hosts.add("notcp-xx-1." + APIClient.getFallbackDomain());

    APIClient client = new APIClient(applicationID, apiKey, hosts, hosts);
    client.setTimeout(1000, 1000);

    long start = System.currentTimeMillis();
    try {
      client.listIndexes();
    } catch (Exception e) {
      //To be sure we get here
      assertTrue(e instanceof AlgoliaException);
    }
    assertTrue((System.currentTimeMillis() - start) < 3 * 1000);
  }

  @Test
  public void shouldHandleConnectionResetException() throws IOException, AlgoliaException {
    Thread runnable = new Thread() {
      @Override
      public void run() {
        try {
          ServerSocket serverSocket = new ServerSocket(8080);
          Socket socket = serverSocket.accept();
          socket.setSoLinger(true, 0);
          socket.close();
        } catch (IOException ignored) {
          ignored.printStackTrace();
        }
      }
    };

    runnable.start();

    List<String> hosts = new ArrayList<String>();
    hosts.add("localhost:8080");
    hosts.add(applicationID + "-1." + APIClient.getFallbackDomain());

    APIClient client = new APIClient(applicationID, apiKey, hosts, hosts);
    client.setTimeout(1000, 1000);

    Long start = System.currentTimeMillis();
    assertNotNull(client.listIndexes());
    long end = System.currentTimeMillis() - start;
    assertTrue(end < 2 * 1000);
  }
}
