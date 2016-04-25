package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.util.NamedList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by clalu4 on 2016-04-06.
 * For some reason the SolrServer-class always tries to establish a ssl connection (even it´s not needed). Then it
 * must have a key-store for valid servers to connect to. If not, it throws an exception. Often the jvm does have a
 * default such store. But in case it does not the application itself must provide this.
 */
public class SolrServerFactory {

    /**
     * Factory method.
     *
     * @return produces an new instance of the {@link SolrServer}. Settings (what url to use for the actual server) is
     * fetched from the file [user.home]/.hotell/ifeed/config.properties and its property 'solr.service'.
     */
    public static SolrServer create() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(getPropertiesPath()));
            String url = properties.getProperty("solr.service");

            HttpClientFactory factory = new HttpClientFactory();
            HttpClient client = factory.getHttpsClient();
            HttpSolrServer result = new HttpSolrServer(url, client);
            result.setRequestWriter(new BinaryRequestWriter());

            return result;
        } catch (final Exception e) {
            e.printStackTrace();

            return new SolrServer() {
                @Override
                public NamedList<Object> request(SolrRequest request) throws SolrServerException, IOException {
                    throw new RuntimeException("solrServer failed to initialize during init-phase - when the portlet" +
                            " starts or when it is deployed.", e);
                }

                @Override
                public void shutdown() {

                }
            };

        }
    }

    static String getPropertiesPath() {
        return path(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties");
    }

    static String path(String... parts) {
        return StringUtils.join(parts, File.separator);
    }

}
