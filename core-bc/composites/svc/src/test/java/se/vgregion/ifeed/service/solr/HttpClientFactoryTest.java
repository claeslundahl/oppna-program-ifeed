package se.vgregion.ifeed.service.solr;

import org.apache.http.client.HttpClient;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by clalu4 on 2016-04-08.
 */
public class HttpClientFactoryTest {

    @Test
    public void getHttpsClient() throws Exception {
        HttpClientFactory factory = new HttpClientFactory();
        HttpClient result = factory.getHttpsClient();
        assertNotNull(result);
    }

}