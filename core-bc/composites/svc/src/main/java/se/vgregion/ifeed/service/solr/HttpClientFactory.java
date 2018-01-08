package se.vgregion.ifeed.service.solr;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.KeyManagementException;

/**
 * Helper class to create a https client.
 */
public class HttpClientFactory {

    private HttpClient client;

    private final String keyStorePassWord = "changeit";

    /**
     * Creates the https-client.
     * @return the resulting https client. Always returns the same instance.
     * @throws Exception if something goes awry.
     */
    public HttpClient getHttpsClient() throws Exception {
        if (client != null) {
            return client;
        }
        SSLContext sslcontext = getSslContext();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        client = HttpClients.custom().setSSLSocketFactory(factory).build();

        return client;
    }

    private SSLContext getSslContext() throws KeyStoreException,
    NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream keyStoreStream = HttpClientFactory.class.getResourceAsStream("/keystore");

        try {
            trustStore.load(keyStoreStream, keyStorePassWord.toCharArray());
        } finally {
            keyStoreStream.close();
        }

        return SSLContexts.custom()
                .loadTrustMaterial(trustStore)
                .build();
    }
}