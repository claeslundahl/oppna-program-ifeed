package se.vgregion.ifeed.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Use this to populate table feed_document_index with information about what document exists within what
 * flows result.
 */
public class FeedDocumentIndexSupport {

    private final DatabaseApi database;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FeedDocumentIndexSupport(DatabaseApi database) {
        this.database = database;
    }

    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        FeedDocumentIndexSupport feedDocumentIndexSupport = new FeedDocumentIndexSupport(database);
        feedDocumentIndexSupport.createTableIfNotThere();
        feedDocumentIndexSupport.copyDocumentIdAndFeedIdToTable();
        //System.out.println(feedDocumentIndexSupport.getRelevantFeeds().size());
    }

    private List<Feed> getRelevantFeeds() {
        String sql = "select distinct vi.* from vgr_ifeed vi where vi.id in (select ifeed_id from vgr_ifeed_filter) and vi.id > 0";
        List<Tuple> items = database.query(sql);
        return Feed.toFeeds(items);
    }

    private void createTableIfNotThere() {
        String ddl = "create table if not exists feed_document_index\n" +
                "(\n" +
                "    document_id character varying(255) NOT NULL,\n" +
                "    ifeed_id bigint NOT NULL,\n" +
                "    CONSTRAINT feed_document_index_pkey PRIMARY KEY (document_id, ifeed_id)\n" +
                ")";
        database.execute(ddl);
        database.commit();
    }

    public void copyDocumentIdAndFeedIdToTable() {
        database.execute("delete from feed_document_index");
        List<Feed> items = getRelevantFeeds();
        System.out.println("Hittade " + items.size() + " flöden.");
        // copyDocumentIdAndFeedIdToTable(Feed.toFeed(Map.of("id", -3505813l)));

        int i = 0;
        for (Feed feed : items) {
            i++;
            copyDocumentIdAndFeedIdToTable(feed);
            if (i % 100 == 0) {
                System.out.print(" " + String.format("%0" + (items.size() + "").length() + "d", i));
            }
            if (i % 1000 == 0) {
                System.out.println();
            }
        }
        database.commit();
    }

    public void copyDocumentIdAndFeedIdToTable(Feed feed) {
        try {
            copyDocumentIdAndFeedIdToTableImpl(feed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void copyDocumentIdAndFeedIdToTableImpl(Feed feed) {
        Long ifeedId = (Long) feed.get("id");
        final String serviceUrl =
                String.format("http://localhost:7070/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=id",
                        ifeedId);
        String raw = Http.get(serviceUrl);
        List result = gson.fromJson(raw, List.class);

        HashSet<String> documentIds = new HashSet<>();
        for (Object o : result) {
            Map map = (Map) o;
            documentIds.add((String) map.get("id"));
        }
        for (String documentId : documentIds) {
            Map<String, Object> item = new HashMap<>();
            item.put("document_id", documentId);
            item.put("ifeed_id", ifeedId);
            database.insert("feed_document_index", item);
        }
    }

}
