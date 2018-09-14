package se.vgregion.ifeed.service.solr;

import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by clalu4 on 2016-11-10.
 */
public class SolrFacetUtilTest {

    public static void main(String[] args) throws IOException {
        /*IFeed feed = new IFeed();
        IFeedFilter filter = new IFeedFilter("*test*", "dc.title");
        feed.addFilter(filter);
        String baseUrl = "http://vgas1499.vgregion.se:9090/solr/ifeed";
        List<String> result = SolrFacetUtil.fetchFacets(baseUrl, feed, "dc.title");
        System.out.println(result);*/
        String json = new String(Files.readAllBytes(Paths.get(System.getProperty("user.home"), "feed.json")));
        IFeed feed = Json.toObject(IFeed.class, json);
        List<String> result = SolrFacetUtil.fetchFacets(null, feed, "dc.title");
        for (String r : result) {
            System.out.println(r);
        }
    }

}