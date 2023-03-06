package se.vgregion.ifeed;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

public class HttpCertificateClient {

    protected HttpClient client;
    protected SSLContext sslContext;
    protected KeyManagerFactory keyManagerFactory;
    protected KeyStore keyStore;

    public static void main(String[] args) throws Exception {
        System.out.println("Anropar test.");
        HttpCertificateClient hcc = new HttpCertificateClient(Consts.Test.clientCertificateFile, Consts.Test.clientCertificatePassword);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        /*for (String domain : ds) {
            System.out.print(i++ + " ");
            if (i % 10 == 0)
                System.out.println();


            System.out.println(domain);
            System.out.println();
            HttpResponse<String> result = hcc.get(Consts.Test.address, "glossary", domain);
            System.out.println(result.body());
            System.out.println();

            sb.append(domain);
            sb.append("\n");
            sb.append(result.body());
            sb.append("\n");
        }*/

        HttpResponse<String> sm = hcc.get(Consts.Test.address, "glossary", "SweMeSH - Swedish Terms");
        sb.append(sm.body());
        System.out.println(sm.body());

        // Files.writeString(Paths.get("C:\\Users\\clalu4\\Temp\\20230127", "datakataloganrop.txt"), sb.toString());
    }

    public HttpCertificateClient(String pfxFile, String pfxPassword) {
        try {
            init(pfxFile, pfxPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void init(String pfxFile, String pfxPassword) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        // Ladda in PFX-filen med certifikatet och nyckeln som ska användas för klientverifiering
        FileInputStream pfxInput = new FileInputStream(pfxFile);
        char[] password = pfxPassword.toCharArray();
        keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(pfxInput, password);

        // Skapa en nyckelmanager
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);

        // Skapa en SSL-context och använd den för att skapa en HttpClient
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), getTrustManagers(), null);
        client = HttpClient.newBuilder().sslContext(sslContext).build();
    }

    public HttpResponse<String> get(String url, String ... headers) {
        try {

            // Använd HttpClient för att skicka en förfrågan till tjänsten
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(url));
            if (headers != null && headers.length > 0) {
                for (int i = 0; i < headers.length; i += 2) {
                    builder.header(headers[i], headers[i + 1]);
                }
            }
            HttpRequest request = builder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*public static HttpResponse<String> call(String pfxFile, String pfxPassword, String url) throws Exception {
        // Ladda in PFX-filen med certifikatet och nyckeln som ska användas för klientverifiering
        FileInputStream pfxInput = new FileInputStream(pfxFile);
        char[] password = pfxPassword.toCharArray();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(pfxInput, password);

        // Skapa en nyckelmanager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);

        // Skapa en SSL-context och använd den för att skapa en HttpClient
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), getTrustManagers(), null);
        HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

        // Använd HttpClient för att skicka en förfrågan till tjänsten
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        System.out.println(response);
        return response;
    }*/

    static TrustManager[] getTrustManagers() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        };
        return trustAllCerts;
    }

}
