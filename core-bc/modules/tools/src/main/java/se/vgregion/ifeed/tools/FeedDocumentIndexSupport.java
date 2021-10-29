package se.vgregion.ifeed.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.common.utils.Props;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

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

    private static boolean running = false;

    public static boolean isRunning() {
        return running;
    }

    public static long runCounter = 0;
    public static long totalCount = 0;

    public static void main(String[] args) {
        main();
    }

    public static void main() {
        running = true;
        System.out.println("Getting the db.");
        DatabaseApi database = DatabaseApi.getLocalApi();
        FeedDocumentIndexSupport feedDocumentIndexSupport = new FeedDocumentIndexSupport(database);
        try {
            System.out.println("Dropping and creating the table.");
            feedDocumentIndexSupport.createTableIfNotThere();
            System.out.println("Starting to copy.");
            feedDocumentIndexSupport.copyDocumentIdAndFeedIdToTable();
            //System.out.println(feedDocumentIndexSupport.getRelevantFeeds().size());.
            running = false;
        } catch (Exception e) {
            database.rollback();
            throw new RuntimeException(e);
        }
    }

    private List<Feed> getRelevantFeeds() {
        String sql = "select distinct vi.* from vgr_ifeed vi where vi.id in (select ifeed_id from vgr_ifeed_filter) and vi.id > 0";
        System.out.println(String.format("Försöker hitta %s rader.",
                database.query("select count(c.*) from (" + sql + ") c").get(0).values().iterator().next().toString()));
        List<Tuple> items = database.query(sql);
        return Feed.toFeeds(items);
    }

    private void createTableIfNotThere() {
        try {
            /*database.execute("drop table if exists feed_document_index");
            database.commit();*/
            String ddl = "create table if not exists feed_document_index\n" +
                    "(\n" +
                    "    document_id character varying(255) NOT NULL,\n" +
                    "    ifeed_id bigint NOT NULL,\n" +
                    "    CONSTRAINT feed_document_index_pkey PRIMARY KEY (document_id, ifeed_id)\n" +
                    ")";
            database.execute(ddl);
            database.commit();
        } catch (Exception e) {
            System.out.println("Tabellen kanske redan finns!: " + e.getMessage());
        }
    }

    public void copyDocumentIdAndFeedIdToTable() {
        database.execute("delete from feed_document_index");
        List<Feed> items = getRelevantFeeds();
        System.out.println("Hittade " + items.size() + " flöden.");
        totalCount = items.size();
        // copyDocumentIdAndFeedIdToTable(Feed.toFeed(Map.of("id", -3505813l)));

        int i = 0;
        for (Feed feed : items) {
            i++;
            runCounter = i;
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

    private Properties properties;

    public Properties getProperties() {
        if (properties == null) {
            properties = Props.fetchProperties();
        }
        return properties;
    }



    private void copyDocumentIdAndFeedIdToTableImpl(Feed feed) {
        Long ifeedId = (Long) feed.get("id");
        String template = getProperties().getProperty("ifeed.web.script.json.url") +
                "/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc"; //&f=dc.source.documentid&f=vgr:VgrExtension.vgr:Source.id";

        /*final String serviceUrl =
                String.format("http://localhost:7070/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=id",
                        ifeedId);*/
        final String serviceUrl = String.format(template, ifeedId);

        String raw = Http.get(serviceUrl);
        List result = gson.fromJson(raw, List.class);

        HashSet<String> documentIds = new HashSet<>();
        for (Object o : result) {
            Map map = (Map) o;
            Object v = map.get("vgr:VgrExtension.vgr:Source.id");
            if (v == null) {
                v = map.get("dc.source.documentid");
            }
            documentIds.add((String) v);
        }
        for (String documentId : documentIds) {
            Map<String, Object> item = new HashMap<>();
            item.put("document_id", documentId);
            item.put("ifeed_id", ifeedId);
            database.insert("feed_document_index", item);
        }
    }

}
