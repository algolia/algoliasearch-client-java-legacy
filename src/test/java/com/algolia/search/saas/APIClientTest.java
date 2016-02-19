package com.algolia.search.saas;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class APIClientTest {

    private APIClient client;

    @Before
    public void before() {
        client = new APIClient("appId", "apiKey");
    }

    @Test
    public void generatesSecuredApiKey() throws InvalidKeyException, NoSuchAlgorithmException {
        String secureApiKey = client.generateSecuredApiKey("PRIVATE_API_KEY", new Query().setTagFilters("user_42"));
        assertEquals("ZWRjMDQyY2Y0MDM1OThiZjM0MmEyM2VlNjVmOWY2YTczYzc3YWJiMzdhMjIzMDY5M2VmY2RjNmQ0MmI5NWU3NHRhZ0ZpbHRlcnM9dXNlcl80Mg==", secureApiKey);
    }

    @Test
    public void generatesSecuredApiKeyWithUserKey() throws InvalidKeyException, NoSuchAlgorithmException {
        String secureApiKey = client.generateSecuredApiKey("PRIVATE_API_KEY", new Query().setTagFilters("user_42"), "userToken");
        assertEquals("MDc3N2VlNzkwNDY1MjRjOGFmNGJhYmVmOWI1YTM1YzYxOGQ1NWMzNjBlYWMwM2FmODY0N2VmNjMyOTE5YTAwYnRhZ0ZpbHRlcnM9dXNlcl80MiZ1c2VyVG9rZW49dXNlclRva2Vu", secureApiKey);
    }

}
