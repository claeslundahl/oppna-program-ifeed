package se.vgregion.varnish;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by clalu4 on 2017-02-24.
 */
public class VarnishClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(VarnishClient.class);

    /**
     * Host name of the Varnish machine.
     */
    private String server;

    /**
     * What host the original content, that now is cached in the Varnish server, comes from.
     */
    private String contentOriginHost;

    /**
     * What port the Varnish instance listens to.
     */
    private int port;

    private String secret;

    public static void main(String[] args) throws IOException {
        // String url = "/iFeed-web/meta.json?instance=1594612&by=dc.title&dir=asc&startBy=0&endBy=200&callback=__gwt_jsonp__.P0.onSuccess";
        String url = "/iFeed-web/meta.json?instance=1594612";

        VarnishClient client = newVarnishClient();
        client.clear(url);
    }

    static private String path(String... parts) {
        return StringUtils.join(parts, File.separator);
    }

    public static VarnishClient newVarnishClient() {
        final Path path = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties");
        try (InputStream is = Files.newInputStream(path)) {
            Properties config = new Properties();
            config.load(is);
            if (!config.containsKey("varnish.server")) {
                return null;
            }
            return newVarnishClient(config);
        } catch (IOException e) {
            LOGGER.error("Make sure to have a config file with url " + path + ". And within that properties like this:\n" +
                    "varnish.secret=doNotSpreadThisAround\n" +
                    "varnish.server=name.of.varnish.server.to.use.com\n" +
                    "varnish.port=8088 as example of varnish port.\n" +
                    "varnish.content.origin.host=Origin host-name of the content that the Varnish caches.", e);
            throw new RuntimeException(e);
        }
    }

    public static VarnishClient newVarnishClient(Properties fromTheseProperties) {
        VarnishClient result = new VarnishClient();
        result.setServer(fromTheseProperties.getProperty("varnish.server"));
        result.setPort(Integer.parseInt(fromTheseProperties.getProperty("varnish.port")));
        result.setSecret(fromTheseProperties.getProperty("varnish.secret"));
        result.setContentOriginHost(fromTheseProperties.getProperty("varnish.content.origin.host"));
        return result;
    }

    public void clear(String thatUrlFromCache) {
        try {
            clearImp(thatUrlFromCache);
        } catch (IOException e) {
            // throw new RuntimeException(e);
        }
    }

    public void clearImp(String thatUrlFromCache) throws IOException {
        HttpHost host = new HttpHost(getServer(), getPort());
        // CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        /*BasicHttpRequest purgeRequest = new BasicHttpRequest(
                "PURGE", "" + thatUrlFromCache);*/
        HttpPurge purgeRequest = new HttpPurge("" + thatUrlFromCache);
        //HttpPurge purgeRequest = new HttpPurge()
        // Host:ifeed-stage.vgregion.se X-Cache-Group:Staging Connection:close X-Purge-Secret:
        purgeRequest.addHeader("Host", contentOriginHost);
        purgeRequest.addHeader("X-Cache-Group", "Staging");
        purgeRequest.addHeader("Connection", "close");
        purgeRequest.addHeader("X-Purge-Secret", secret);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(host, purgeRequest);

        for (Header header : response.getAllHeaders()) {
            System.out.println(header);
        }
        //    httpclient.close();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    public String getContentOriginHost() {
        return contentOriginHost;
    }

    public void setContentOriginHost(String contentOriginHost) {
        this.contentOriginHost = contentOriginHost;
    }
}
