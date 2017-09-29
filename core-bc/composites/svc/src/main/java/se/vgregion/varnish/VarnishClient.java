package se.vgregion.varnish;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private String cacheGroup;

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
        result.setCacheGroup(fromTheseProperties.getProperty("varnish.cache.group"));
        return result;
    }

    public void clear(String thatUrlFromCache) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    clearImp(thatUrlFromCache);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }

    public void clearImp(String thatUrlFromCache) throws IOException {
        String original = thatUrlFromCache;
        if (thatUrlFromCache.startsWith("http://") || thatUrlFromCache.startsWith("https://")) {
            String url = thatUrlFromCache;
            url = url.replace("://", "");
            url = url.substring(url.indexOf('/'));
            thatUrlFromCache = url;
        }
        if (thatUrlFromCache.startsWith("/")) {
            thatUrlFromCache = thatUrlFromCache.substring(1);
        }
        thatUrlFromCache = "/" + thatUrlFromCache;
        HttpHost host = new HttpHost(getServer(), getPort());
        HttpPurge purgeRequest = new HttpPurge(thatUrlFromCache);
        LOGGER.debug("clearImp purge url " + thatUrlFromCache);
        purgeRequest.addHeader("Host", contentOriginHost);
        purgeRequest.addHeader("X-Cache-Group", getCacheGroup());
        purgeRequest.addHeader("Connection", "close");
        purgeRequest.addHeader("X-Purge-Secret", secret);
        purgeRequest.addHeader("X-Hard-Purge", "1");

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(host, purgeRequest);

        /*for (Header header : response.getAllHeaders()) {
            System.out.println(header);
        }*/
    }


    public void clearJson(final String thatUrlFromCache) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    clearJsonImp(thatUrlFromCache);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }

    private void clearJsonImp_old(String thatUrlFromCache) throws IOException {
        HttpHost host = new HttpHost(getServer(), getPort());
        HttpPurge purgeRequest = new HttpPurge("" + thatUrlFromCache);
        purgeRequest.addHeader("Host", contentOriginHost);
        purgeRequest.addHeader("X-Cache-Group", "Staging");
        purgeRequest.addHeader("Connection", "close");
        purgeRequest.addHeader("X-Purge-Secret", secret);

        // Makes Varnish do a 'hard' purge. Immediately removing cached content.
        purgeRequest.addHeader("X-Hard-Purge", "1");

        String subUrl = thatUrlFromCache.replace("://", "");
        subUrl = thatUrlFromCache.substring(subUrl.indexOf('/') + 1);

        String json = toText(new URL(thatUrlFromCache));
        ArrayList<HashMap<String, Object>> result =
            new ObjectMapper().readValue(json, ArrayList.class);

        for (HashMap<String, Object> map : result) {
            String id = (String) map.get("id");
            String[] idFrags = id.split(Pattern.quote(":"));
            String uuid = idFrags[idFrags.length - 1];
            purgeRequest.addHeader("xkey", "alfresco/" + uuid);
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse response = httpClient.execute(host, purgeRequest);

        /*for (Header header : response.getAllHeaders()) {
            System.out.println(header);
        }*/
    }

    public void clearJsonImp(String thatUrlFromCache) throws IOException {
        String url = thatUrlFromCache;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            url = url.replace("://", "");
            url = url.substring(url.indexOf('/'));
        }
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        url = "/" + url;
        HttpHost host = new HttpHost(server, port);
        HttpClient httpclient = HttpClientBuilder.create().build();
        BasicHttpRequest purgeRequest = new BasicHttpRequest("PURGE", url);

        purgeRequest.addHeader("Host", contentOriginHost);
        purgeRequest.addHeader("X-Cache-Group", cacheGroup);
        purgeRequest.addHeader("Connection", "close");
        purgeRequest.addHeader("X-Purge-Secret", secret);
        // Makes Varnish do a 'hard' purge. Immediately removing cached content.
        purgeRequest.addHeader("X-Hard-Purge", "1");

    /* This should be done whenever the ifeed-server delivers a feed - on that feed, those headers 'xkey'. */
        String json = toText(new URL(thatUrlFromCache));
        ArrayList<HashMap<String, Object>> result =
            new ObjectMapper().readValue(json, ArrayList.class);

        for (HashMap<String, Object> map : result) {
            String id = (String) map.get("id");
            String[] idFrags = id.split(Pattern.quote(":"));
            String uuid = idFrags[idFrags.length - 1];
            purgeRequest.addHeader("xkey", "alfresco/" + uuid);
        }
        HttpResponse response = httpclient.execute(host, purgeRequest);
    }

    static String toText(URL url) {
        try {
            URLConnection conn = url.openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
            return "[]";
        }
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

    public void setCacheGroup(String cacheGroup) {
        this.cacheGroup = cacheGroup;
    }

    public String getCacheGroup() {
        return cacheGroup;
    }
}
