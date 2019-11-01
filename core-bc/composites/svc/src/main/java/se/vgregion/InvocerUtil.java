package se.vgregion;

import org.apache.solr.client.solrj.SolrServer;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.service.solr.IFeedResults;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.SolrServerFactory;
import se.vgregion.ifeed.service.solr.client.Field;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Run this to test functionality of this svc.
 * Example of start command would be:
 * java -cp "*" se.vgregion.IntegrationTest http://vgas1499.vgregion.se:9090/solr/ifeed *test*
 * The following jar:s have to be present in the class-path.
 * <pre>
 * cglib-2.2.jar              httpcore-4.3.3.jar
 * cglib-nodep-2.2.jar        httpmime-4.2.3.jar
 * commons-lang-2.4.jar       iFeed-core-bc-composite-svc-1.12-SNAPSHOT.jar
 * commons-logging-1.1.1.jar  iFeed-core-bc-composite-types-1.12-SNAPSHOT.jar
 * dao-framework-3.5.jar      noggit-0.5.jar
 * gson-2.3.1.jar             slf4j-api-1.6.1.jar
 * httpclient-4.3.6.jar       solr-solrj-4.5.1.jar
 * </pre>
 */
public class InvocerUtil {

    private final String url;
    private int maxResultCount = 10;

    public InvocerUtil(String url) {
        this.url = url;
    }

    public InvocerUtil() {
        String userHome = System.getProperty("user.home");
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(Paths.get(userHome, ".hotell", "ifeed", "config.properties"))) {
            properties.load((is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.url = properties.getProperty("solr.service");
    }

    public IFeedResults findByDocumentName(String solrPattern, final List<FieldsInf> fieldsToUseInTheSearch) {
        return find("dc.title", solrPattern, fieldsToUseInTheSearch);
    }

    public IFeedResults find(String key, String value, final List<FieldsInf> fieldsToUseInTheSearch) {
        SolrServer solrServer = SolrServerFactory.create(url);
        IFeedService iFeedService = new IFeedServiceImpl() {
            @Override
            public List<FieldsInf> getFieldsInfs() {
                /*List<FieldsInf> result = new ArrayList<FieldsInf>();
                FieldsInf item = new FieldsInf();
                item.setText(getFieldsConfig());
                result.add(item);
                return result;*/
                return fieldsToUseInTheSearch;
            }
        };

        IFeedSolrQuery iFeedSolrQuery = new IFeedSolrQuery(solrServer, iFeedService);
        IFeed feed = new IFeed();
        IFeedFilter filter = new IFeedFilter(value, key);
        feed.addFilter(filter);
        IFeedResults result = iFeedSolrQuery.getIFeedResults(feed, null);

        FieldsInf fieldsInf = new FieldsInf();
        fieldsInf.setText(getFieldsConfig());
        return result;
    }

    public static String getFieldsConfig() {
        return FieldsInf.getDataFromCache();
    }

    public int getMaxResultCount() {
        return maxResultCount;
    }

    public void setMaxResultCount(int maxResultCount) {
        this.maxResultCount = maxResultCount;
    }

    public static Set<String> getKeysWithMultiValues(final List<FieldsInf> fieldsToUseInTheSearch) {
        try {

            List<Field> fields = SolrHttpClient.newInstanceFromConfig().fetchFields();

            return fields.stream()
                    .filter(field -> {
                        /*boolean sortable = field.getType().equalsIgnoreCase("string")
                                || field.getType().equalsIgnoreCase("long")
                                || field.getType().equalsIgnoreCase("tdate");*/

                        return /*!sortable ||*/ field.getMultiValued();
                    })
                    .map(Field::getName)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Fel i getKeysWithMultiValues");
            throw new RuntimeException("Fel i getKeysWithMultiValues", e);
        }
    }

    public static Set<String> getKeysWithMultiValues(Map<String, Object> fromThisSample) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Object> entry : fromThisSample.entrySet()) {
            if (entry.getValue() instanceof Collection) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

}
