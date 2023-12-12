package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.types.IFeed;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GoverningDocumentComplementationBatch {
    static DatabaseApi local;
    // http://localhost:7070/iFeed-web/documentlists/1168/metadata.json?by=&dir=asc


    static Set<Long> getAlreadyFinishedLocalFeedIds() {
        Set<Long> result = DatabaseApi.getLocalApi().query("select (id * -1) as id_comp \n" +
                        "from vgr_ifeed vi where vi.name like '%(sty. dok. komplement)' and id < 0")
                .stream().map(item -> new Long((Long) item.get("id_comp"))).collect(Collectors.toSet());

        return result;
    }


    public static void main(String[] args) {
        local = DatabaseApi.getRemoteProdDatabaseApi();
        System.out.println(local.getUrl());
        // Set<Long> okList = getAlreadyFinishedLocalFeedIds();

        // if (true) return;
        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(local);
        List<Number> diffingAfter = new ArrayList<>();
        List<Number> hasComplementAlready = new ArrayList<>();
        try {
            List<Feed> items = gdc.findFeedsWithRelevantFilters();
            System.out.println("Kollar " + items.size() + " efter Barium-dokument.");
            int count = 0, allCount = 0;
            for (Feed item : items) {
                Number feedId = (Number) item.get("id");
                // if (okList.contains(feedId)) {
                String itemsBefore = foo(feedId);
                if (itemsBefore.contains("Barium")) {
                    if (!local.query("select * from vgr_ifeed where id = ?", feedId.longValue() * -1).isEmpty()) {
                        hasComplementAlready.add(feedId);
                    }
                    // System.out.println(item);
                    Feed result = gdc.makeComplement(item);
                    if (result != null) {
                        count++;

                        gdc.commit();
                        String itemsAfter = foo(feedId);
                        if (!itemsAfter.equals(itemsBefore)) {
                            diffingAfter.add((Number) item.get("id"));
                        }
                    }
                }
                // }

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
        System.out.println("Flöden som redan hade komplement: " + hasComplementAlready);
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

    static String foo(Object id) {
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

    static SolrHttpClient solr = SolrHttpClient.newInstanceFromConfig();

    private static List<Map<String, Object>> get(Feed feed) {
        // Feed feed = Feed.toFeed(item);
        feed.fill(local);
        String q = (feed.toJpaVersion().toQuery(solr.fetchFields()));
        // System.out.println(q);
        Result r = solr.query(q, 0, 1_000_000, "ASC", null,
                "SourceSystem");
        if (r != null && r.getDocumentList() != null && r.getDocumentList().getDocuments() != null) {
            return
                    r.getDocumentList().getDocuments();
        }
        return null;
    }

    private static String toCondition(Feed feed) {
        feed.fill(local);
        IFeed jpa = feed.toJpaVersion();
        String q = (jpa.toQuery(solr.fetchFields()));
        return q;
    }

    private static List<Map<String, Object>> get(String q) {
        Result r = solr.query(q, 0, 1_000_000, "ASC", null,
                "SourceSystem");
        if (r != null && r.getDocumentList() != null && r.getDocumentList().getDocuments() != null) {
            return
                    r.getDocumentList().getDocuments();
        }
        return null;
    }

}
