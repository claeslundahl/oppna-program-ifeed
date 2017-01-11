package se.vgregion.ifeed.service.solr;

import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.util.List;

/**
 * Created by clalu4 on 2016-11-10.
 */
public class SolrFacetUtilTest {

    public static void main(String[] args) {
        IFeed feed = new IFeed();
        IFeedFilter filter = new IFeedFilter("*t*", "dc.title");
        feed.addFilter(filter);
        String baseUrl = "http://vgas1499.vgregion.se:9090/solr/ifeed";
        List<String> result = SolrFacetUtil.fetchFacets(baseUrl, feed, "dc.title");
        System.out.println(result);
    }

}