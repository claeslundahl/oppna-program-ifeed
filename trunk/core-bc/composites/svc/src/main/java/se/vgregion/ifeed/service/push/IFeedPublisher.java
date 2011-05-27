/**
 * 
 */
package se.vgregion.ifeed.service.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import se.vgregion.ifeed.types.IFeed;

/**
 * @author Anders Asplund - Callista Enterprise
 * 
 */
public class IFeedPublisher {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedPublisher.class);
    private static final int DEFAULT_TIMEOUT = 5000;
    private StringBuilder content = new StringBuilder();
    private int ifeedCount = 0;

    private URL pushServerUrl;
    private HttpURLConnection conn = null;
    private int timeout = DEFAULT_TIMEOUT;

    @Value("${ifeed.feed}")
    private String ifeedAtomFeed;

    public IFeedPublisher(URL pushServerUrl) {
        super();
        this.pushServerUrl = pushServerUrl;
    }

    public void addIFeed(IFeed iFeed) {
        try {
            content.append("&hub.url=").append(URLEncoder.encode(String.format(ifeedAtomFeed, iFeed.getId()), "UTF-8"));
            ++ifeedCount;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public int getIFeedCount() {
        return ifeedCount;
    }

    public void setIfeedAtomFeed(String ifeedAtomFeed) {
        this.ifeedAtomFeed = ifeedAtomFeed;
    }

    public String getRequestBody() {
        return content.toString();
    }

    public void publish() {
        try {
            LOGGER.info("Publishing {} ifeeds to push server.", ifeedCount);
            content.insert(0, "hub.mode=" + URLEncoder.encode("publish", "UTF-8"));
            LOGGER.debug("Open a connection to: {}", pushServerUrl);
            conn = (HttpURLConnection) pushServerUrl.openConnection();

            sendPost();

            if (failedPost() && LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error when publishing feeds to push server: {}", getResponseBody());
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Published feeds to push server: {}", getResponseBody());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                content = new StringBuilder();
                conn.disconnect();
            }
        }
    }

    private boolean failedPost() throws IOException {
        return conn.getResponseCode() != 204;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private void sendPost() throws IOException {
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(this.timeout);
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(content.toString());
        LOGGER.debug("Posting request to push server with the following body: {}", content);
        out.flush();
    }

    private String getResponseBody() throws IOException {
        int size = conn.getHeaderFieldInt(CONTENT_LENGTH, 0);
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder responseBody = new StringBuilder(size);
        while ((line = rd.readLine()) != null) {
            responseBody.append(line);
        }
        return responseBody.toString();
    }

}
