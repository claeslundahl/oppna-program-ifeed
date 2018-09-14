package se.vgregion.ifeed.service.solr;

import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class SolrHttpClientTest {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"))));

        System.out.println("solr.service: " + properties.getProperty("solr.service"));

        String json = new String(Files.readAllBytes(Paths.get(System.getProperty("user.home"), "feed.json")));
        IFeed feed = Json.toObject(IFeed.class, json);

        System.out.println(feed.toQuery());

        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

        SolrHttpClient.Result result = client.query(feed.toQuery(), null, null, "dc.title%20asc");
        for (Map<String, Object> doc : result.getResponse().getDocs()) {
            System.out.println(doc);
        }
    }


}