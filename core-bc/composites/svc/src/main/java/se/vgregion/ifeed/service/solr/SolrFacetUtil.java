package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.StringUtils;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    public static List<String> fetchFacets(String solrBaseUrl, IFeed feed, String field) {
        try {
            return fetchFacetsImpl(solrBaseUrl, feed, field);
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

    private static List<String> fetchFacetsImpl(String solrBaseUrl, IFeed feed, String field) throws Exception {

        // System.out.println("solrBaseUrl: " + solrBaseUrl);
        // System.out.println("Facet find with query " + feed.toQuery());

        if (true) {
            if (feed == null) throw new NullPointerException();
            System.out.println("The facet-question to ask: " + feed.toQuery());
            Result result = client.query(
                    feed.toQuery(),
                    0,
                    10,
                    field + "%20asc"
            );
            return new ArrayList<>(SolrHttpClient.toFacetSet(result, field));
        }

        String fu = facetUrl(solrBaseUrl, feed, field);
        URL url = new URL(fu);
        InputStream stream = url.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = stream.read();
        while (i != -1) {
            baos.write(i);
            i = stream.read();
        }
        String result = new String(baos.toByteArray());
        Map<String, Object> root = (Map<String, Object>) Json.parse(result);
        if (true) {
            Map<String, Object> response = (Map<String, Object>) root.get("response");
            Map<String, Map> docs = (Map) response.get("docs");
            List<String> sugestions = new ArrayList<>();
            for (String s : docs.keySet()) {
                sugestions.addAll(docs.get(s).values());
            }
            return sugestions;
            //return docs.subList(0, Math.min(10, docs.size()));
        }

        Object tree = Json.parse(result);


        Map facetCounts = (Map) ((Map) tree).get("facet_counts");
        Map facetFields = (Map) ((Map) facetCounts).get("facet_fields");
        Map facetFieldsMap = (Map) facetFields;
        Object mapOrList = facetFieldsMap.get(field);
        if (mapOrList instanceof Map) {
            Map<String, Object> map = (Map) mapOrList;
            List<String> list = (List<String>) map.get(field);
            if (list == null) {
                list = new ArrayList<>();
                NavigableMap<String, String> om = new TreeMap(map);
                for (String s : om.keySet()) {
                    list.add(om.get(s));
                }
            }
            if (map.isEmpty()) {
                return Collections.emptyList();
            }
            return list.subList(0, 10);
        } else {
            List<String> values = new ArrayList<String>();
            List<Object> resultWithWeight = (List<Object>) mapOrList;
            // first value is the string, then comes the weight - number of usages...
            for (int j = 0, k = Math.min(resultWithWeight.size(), 10); j < k; j += 2) {
                String value = (String) resultWithWeight.get(j);
                values.add(value);
            }
            return values;
        }
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
