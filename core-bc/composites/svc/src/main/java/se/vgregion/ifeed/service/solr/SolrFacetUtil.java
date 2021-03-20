package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.StringUtils;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by clalu4 on 2014-06-27.
 */
public class SolrFacetUtil {

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    /**
     * Calls the solr server to get facet result of a certain IFeed and field.
     *
     * @param solrBaseUrl the location of the server to use.
     * @param feed        the query to run as condition.
     * @param field       what property to get facets for.
     * @return the top 10 results of the search, sorted descending.
     */
    public static List<String> fetchFacets(String solrBaseUrl, IFeed feed, FieldInf field, String starFilter) {
        try {
            return fetchFacetsImpl(solrBaseUrl, feed, field, starFilter);
        } catch (Exception e) {
            try {
                Files.write(Paths.get(System.getProperty("user.home"), "feed.json"), se.vgregion.common.utils.Json.toJson(feed).getBytes());
            } catch (Exception iff) {
                iff.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static List<String> fetchFacetsImpl(String solrBaseUrl, IFeed feed, FieldInf fi, String starFilter) throws Exception {
        String field = fi.getId();
        if (feed == null) {
            throw new NullPointerException();
        }
        final String question = feed.toQuery(client.fetchFields());
        System.out.println("Type ahead: " + question);
        Result result = client.query(
                question,
                0,
                10,
                "asc",
                fi,
                fi.getId()
        );
        if (result.getResponse() == null) {
            /*result = client.query(
                    question,
                    0,
                    10_000,
                    null,
                    null
            );*/
            throw new RuntimeException(); // Remove this later...
        }
        List<String> things = new ArrayList<>(SolrHttpClient.toFacetSet(result, field, starFilter));
        things = things.subList(0, Math.min(10, things.size()));
        return things;
    }

    private static String facetUrl(String solrBaseUrl, IFeed feed, String field) {
        String settings = "select?wt=json&fl=%s&rows=10&facet=true&facet.mincount=1&q=published:true";
        settings = String.format(settings, field);

        IFeedSolrQuery.FeedFilterBag bag = new IFeedSolrQuery.FeedFilterBag();
        StringBuilder sb = new StringBuilder(solrBaseUrl + (solrBaseUrl.endsWith("/") ? "" : "/") + settings);
        for (IFeedFilter f : feed.getFilters()) {
            bag.get(f.getFilterKey() + getFirstNonBlank(f.getOperator(), "matching")).add(f);
        }
        for (List<IFeedFilter> iFeedFilters : bag.values()) {
            sb.append("&fq=");
            String oq = SolrQueryBuilder.createOrQuery(iFeedFilters);
            if ("(())".equals(oq)) {
                throw new RuntimeException("(())");
            }//TODO: Consider removing this before next deploy.
            sb.append(oq);
        }
        return sb.toString().replaceAll(Pattern.quote(" "), "%20");
    }

    static String getFirstNonBlank(String first, String second) {
        if (StringUtils.isBlank(first)) {
            return second;
        } else {
            return first;
        }
    }


}
