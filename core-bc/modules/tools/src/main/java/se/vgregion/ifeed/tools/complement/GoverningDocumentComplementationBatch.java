package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GoverningDocumentComplementationBatch {

    // http://localhost:7070/iFeed-web/documentlists/1168/metadata.json?by=&dir=asc

    public static void main(String[] args) {
        DatabaseApi local = DatabaseApi.getLocalApi();
        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(local);
        List<Feed> items = gdc.findFeedsWithRelevantFilters();
        System.out.println("Kollar " + items.size() + " efter Barium-dokument.");
        int count = 0;
        for (Feed item : items) {
            // System.out.println(item);
            if (hasBariumDocs(item.get("id"))) {
                // System.out.println(item);
                count++;
            }
        }
        System.out.println("Hittade " + count + " antal med Barium i.");
    }

    static boolean hasBariumDocs(Object id) {
        try {
            return hasBariumDocsImpl(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static HttpClient client = HttpClient.newHttpClient();

    static boolean hasBariumDocsImpl(Object id) throws IOException, InterruptedException {

        final String serviceUrl = String.format("http://localhost:7070/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=SourceSystem", id.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        return response.contains("Barium");
    }

}
