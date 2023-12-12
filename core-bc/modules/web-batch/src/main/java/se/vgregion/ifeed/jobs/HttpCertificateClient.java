package se.vgregion.ifeed.jobs;

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
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpCertificateClient {

    protected HttpClient client;
    protected SSLContext sslContext;
    protected KeyManagerFactory keyManagerFactory;
    protected KeyStore keyStore;

    public Iterable<String> getDomainCollections() {
        String raw = "Grunddata\n" +
                "                Bildelar\n" +
                "                Dokumentstatus\n" +
                "                HosPersKat\n" +
                "                DokumenttypVGR\n" +
                "                Legitimerade yrken\n" +
                "                Maritima klustret\n" +
                "                Nyhetskategorier\n" +
                "                Systemkategorier\n" +
                "                Verksamhetskod Icke-Vård\n" +
                "                Västfastigheter\n" +
                "                Fastigheter\n" +
                "                Regional medicinsk ämnesindelning\n" +
                "                Stopwords\n" +
                "                Viktors test\n" +
                "                Ricardos Test Dictionary\n" +
                "                Davids Test\n" +
                "                Test Veronica\n" +
                "                Test Martin\n" +
                "                Avrådade termer HoS\n" +
                "                VGR Informatik\n" +
                "                VGRs Ordlista\n" +
                "                Informationsteknologi\n" +
                "                Socialstyrelsen\n" +
                "                Stemming\n" +
                "                Koncernstab digitalisering\n" +
                "                VGR Verksamhetsnivå\n" +
                "                VGR Tillämpad nivå\n" +
                "                SweMeSH2015 - Swedish Terms\n" +
                "                SweMeSH2015 - English Terms\n" +
                "                VGR Apelon\n" +
                "                KiV\n" +
                "                VGR Akademin\n" +
                "                Kommunnamn\n" +
                "                Specialistutbildningar\n" +
                "                Myndigheter VGR\n" +
                "                SweMeSH - Swedish Terms\n" +
                "                SweMeSH - English Terms\n" +
                "                Verksamhetskod\n" +
                "                Legitimerad Yrkesgrupp\n" +
                "                KV Kommun";

        return Arrays.stream(raw.split(Pattern.quote("\n"))).map(s -> s.trim()).collect(Collectors.toSet());
    }

    public HttpCertificateClient(String pfxFile, String pfxPassword) {
        try {
            init(pfxFile, pfxPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void init(String pfxFile, String pfxPassword) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        if (pfxFile==null) throw new NullPointerException("pfxFile-parameter is null.");
        if (pfxPassword==null) throw new NullPointerException("pfxPassword-parameter is null.");
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

    public HttpResponse<String> get(String url, String... headers) {
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
