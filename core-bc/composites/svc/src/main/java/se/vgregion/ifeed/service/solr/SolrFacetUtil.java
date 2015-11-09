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

    // queryUrl = queryUrl + '&fq=' + filterTypeId.value + '%3Aquery*&json.wrf=callback&facet=on&wt=json&facet.field=' + filterTypeId.value + '&facet.mincount=1';
    //http://solr-index.vgregion.se:8080/solr/ifeed/

    // http://solr-index.vgregion.se:8080/solr/ifeed/select?fl=*&rows=10&debugQuery=true&facet=on&wt=json&facet.field=dc.title&facet.mincount=1

    public static void main(String[] args) throws Exception {
        IFeed feed = new IFeed();
        IFeedFilter filter = new IFeedFilter(null, "*est*", "foo");
        filter.setFilterKey("dc.title");
        feed.addFilter(filter);

        filter = new IFeedFilter(null, "*est*", "foo2");
        filter.setFilterKey("dc.title");
        feed.addFilter(filter);

        filter = new IFeedFilter(null, "*", "foo3");
        filter.setFilterKey("document.id");
        // feed.addFilter(filter);

        String q = SolrQueryBuilder.createOrQuery(feed.getFilters());

        IFeedServiceImpl iFeedService = new IFeedServiceImpl() {
            @Override
            public List<FieldsInf> getFieldsInfs() {
                try {
                    FileReader file = new FileReader("C:\\workspace\\oppna-program-ifeed\\core-bc\\composites\\types\\src\\test\\resources\\fields-config.txt");
                    FieldsInf fi = new FieldsInf();
                    fi.setText(file.toString());
                    return Arrays.asList(fi);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };

        List<String> result = fetchFacets("http://solr-index.vgregion.se:8080/solr/ifeed/", feed, "dc.title");
        System.out.println(result);

/*
        StringBuilder sb = new StringBuilder();
        sb.append("http://solr-index.vgregion.se:8080/solr/ifeed/select?fl=*&rows=10&debugQuery=true&facet=on&wt=json&facet.field=dc.title&facet.mincount=1");

        IFeedSolrQuery.FeedFilterBag bag = new IFeedSolrQuery.FeedFilterBag();

        for (IFeedFilter f : feed.getFilters()) {
            bag.get(f.getFilterKey()).add(f);
        }

        for (List<IFeedFilter> iFeedFilters : bag.values()) {
            sb.append("&fq=");
            //sb.append(SolrQueryBuilder.createQuery(f, iFeedService.mapFieldInfToId()));
            sb.append(SolrQueryBuilder.createOrQuery(iFeedFilters));
        }

        System.out.println(sb);
*/
/*
        String fu = facetUrl("http://solr-index.vgregion.se:8080/solr/ifeed/", feed, "dc.title");
        System.out.println(fu);


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
        List<Object> dcTitles = (List<Object>) ((Map) facetFields).get("dc.title");*/

        //System.out.println(tree);
    }

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
        List<Object> resultWithWeight = (List<Object>) ((Map) facetFields).get(field);
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
            //sb.append(SolrQueryBuilder.createQuery(f, iFeedService.mapFieldInfToId()));
            sb.append(SolrQueryBuilder.createOrQuery(iFeedFilters));
        }

        return sb.toString();
    }


}
