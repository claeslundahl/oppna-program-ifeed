package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GoverningDocumentComplementationBatch {

    // http://localhost:7070/iFeed-web/documentlists/1168/metadata.json?by=&dir=asc

    public static void main(String[] args) {
        DatabaseApi local = DatabaseApi.getLocalApi();
        System.out.println(local.getUrl());
        // if (true) return;
        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(local);
        List<Number> diffingAfter = new ArrayList<>();
        try {
            List<Feed> items = gdc.findFeedsWithRelevantFilters();
            System.out.println("Kollar " + items.size() + " efter Barium-dokument.");
            int count = 0, allCount = 0;
            for (Feed item : items) {
                // System.out.println(item);
                String itemsBefore = get(item.get("id"));
                if (get(item.get("id")).contains("Barium")) {
                    // System.out.println(item);
                    Feed result = gdc.makeComplement(item);
                    if (result != null) {
                        count++;

                        gdc.commit();
                        String itemsAfter = get(item.get("id"));
                        if (!itemsAfter.equals(itemsBefore)) {
                            diffingAfter.add((Number) item.get("id"));
                        }
                    }
                }

                allCount++;
                if (allCount % 100 == 0) {
                    System.out.print(" " + allCount + "(" + count + ")");
                }
                if (allCount % 1000 == 0) {
                    System.out.println();
                }
            }
            gdc.commit();
            System.out.println("Hittade " + count + " antal med Barium i.");
        } catch (Exception e) {
            e.printStackTrace();
            gdc.rollback();
        }
        System.out.println();
        System.out.println("Diffande flöden: " + diffingAfter);
    }

/*    static boolean hasBariumDocs(Object id) {
        try {
            return hasBariumDocsImpl(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    static boolean hasBariumDocsImpl(Object id) {
        String response = get(id);
        return response.contains("Barium");
    }*/

    static HttpClient client = HttpClient.newHttpClient();

    static String get(Object id) {
        try {
            final String serviceUrl = String.format("http://localhost:7070/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=SourceSystem", id.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serviceUrl))
                    .build();
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
