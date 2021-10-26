package se.vgregion.ifeed.tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Http {

    public static HttpClient client = HttpClient.newHttpClient();

    public static String get(String url) {
        try {
            return getImpl(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getImpl(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        return response;
    }

}
