package se.vgregion.varnish;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpRequest;

import java.io.IOException;

/**
 * Created by clalu4 on 2017-03-06.
 */
public class OldVarnishClient {

    public static void main(String[] args) throws IOException {
        String url = "/iFeed-web/meta.json?instance=1594612&by=dc.title&dir=asc&startBy=0&endBy=200&callback=__gwt_jsonp__.P0.onSuccess";
        callVarnish("vgas1784.vgregion.se", 8088,
                url);

        // http://vgas1784.vgregion.se:8088/sub/url
    }

    public static void callVarnish(String hostname, int port, String url2removeFromCache) throws IOException {
        HttpHost host = new HttpHost(hostname, port);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        BasicHttpRequest purgeRequest = new BasicHttpRequest("PURGE", "/sub/url" + url2removeFromCache);
        // Host:ifeed-stage.vgregion.se X-Cache-Group:Staging Connection:close X-Purge-Secret:taufThyed0
        purgeRequest.addHeader("Host", "ifeed-stage.vgregion.se");
        purgeRequest.addHeader("X-Cache-Group", "Staging");
        purgeRequest.addHeader("Connection", "close");
        purgeRequest.addHeader("X-Purge-Secret", "taufThyed0");

        HttpResponse response = httpclient.execute(host, purgeRequest);
        for (Header header : response.getAllHeaders()) {
            System.out.println(header);
        }
        httpclient.close();
    }

}
