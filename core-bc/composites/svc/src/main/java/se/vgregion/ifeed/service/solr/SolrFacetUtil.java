package se.vgregion.ifeed.service.solr;

import com.liferay.portal.kernel.json.JSONObject;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.SolrQueryBuilder;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * Created by clalu4 on 2014-06-27.
 */
public class SolrFacetUtil {

    public static List<String> fetchFacets(String solrBaseUrl, IFeed feed, String field) {
        try {
            return fetchFacetsImpl(solrBaseUrl, feed, field);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> fetchFacetsImpl(String solrBaseUrl, IFeed feed, String field) throws Exception {
        List<String> values = new ArrayList<String>();
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
        System.out.println(result);

        Object tree = Json.parse(result);

        Map facetCounts = (Map) ((Map) tree).get("facet_counts");
        Map facetFields = (Map) ((Map) facetCounts).get("facet_fields");
        Map facetFieldsMap = (Map) facetFields;
        List<Object> resultWithWeight = (List<Object>) facetFieldsMap.get(field);
        // first value is the string, then comes the weight - number of usages...
        for (int j = 0, k = Math.min(resultWithWeight.size(), 10); j < k; j += 2) {
            String value = (String) resultWithWeight.get(j);
            values.add(value);
        }
        return values;
    }

    public static String facetUrl(String solrBaseUrl, IFeed feed, String field) {
        String settings = "select?fl=*&rows=10&debugQuery=true&facet=on&wt=json&facet.mincount=1&facet.field=" + field;
        IFeedSolrQuery.FeedFilterBag bag = new IFeedSolrQuery.FeedFilterBag();
        StringBuilder sb = new StringBuilder(solrBaseUrl + settings);
        for (IFeedFilter f : feed.getFilters()) {
            bag.get(f.getFilterKey()).add(f);
        }
        for (List<IFeedFilter> iFeedFilters : bag.values()) {
            sb.append("&fq=");
            sb.append(SolrQueryBuilder.createOrQuery(iFeedFilters));
        }
        return sb.toString();
    }


}
