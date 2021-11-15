package se.vgregion.ifeed.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.common.utils.Props;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        DatabaseApi database = DatabaseApi.getDatabaseApi();
        FeedDocumentIndexSupport feedDocumentIndexSupport = new FeedDocumentIndexSupport(database);
        try {
            System.out.println("Create table if not present.");
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
        // database.execute("delete from feed_document_index");
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
                database.commit();
            }
            if (i % 1000 == 0) {
                deleteItemsNotHavingFeed();
                System.out.println();
            }
        }
        deleteItemsNotHavingFeed();
        database.commit();
    }

    private void deleteItemsNotHavingFeed() {
        database.update("delete from feed_document_index where ifeed_id not in (select id from vgr_ifeed)");
    }

    public static void start() {
        running = true;
        DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println("Dropping and creating the table.");
        final FeedDocumentIndexSupport feedDocumentIndexSupport = new FeedDocumentIndexSupport(database);
        feedDocumentIndexSupport.createTableIfNotThere();
        System.out.println("Getting work-list from db.");
        final List<Feed> work = feedDocumentIndexSupport.getRelevantFeeds();
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        final Runnable runnable = new Runnable() {
            final Runnable self = this;

            @Override
            public void run() {
                try {
                    List<Feed> batch = work.subList(0, work.size() > 100 ? 100 : work.size());
                    System.out.println();
                    feedDocumentIndexSupport.copyDocumentIdAndFeedIdToTable(batch);
                    work.removeAll(batch);
                    System.out.print(" " + String.format("%0" + (work.size() + "").length() + "d", work.size()));
                    if (work.size() % 1000 == 0) {
                        System.out.println();
                    }
                    if (work.isEmpty()) {
                        running = false;
                        System.out.println();
                    } else {
                        scheduledExecutorService.schedule(self, 20, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    database.rollback();
                    throw new RuntimeException(e);
                }
            }
        };

        System.out.println("Starts writing to index.");
        scheduledExecutorService.schedule(runnable, 20, TimeUnit.SECONDS);
    }

    public void copyDocumentIdAndFeedIdToTable(List<Feed> items) {
        for (Feed feed : items) {
            copyDocumentIdAndFeedIdToTable(feed);
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

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    private void foo(Feed feed) {
        // Feed feed = Feed.toFeed(item);
        feed.fill(database);
        String q = (feed.toIFeed().toQuery(client.fetchFields()));
        System.out.println(q);
        Result r = client.query(q, 0, 1_000_000, "ASC", null,
                "dc.source.documentid", "vgr:VgrExtension.vgr:Source.id");
        if (r != null && r.getResponse() != null && r.getResponse().getDocs() != null) {

        }
    }

    private void copyDocumentIdAndFeedIdToTableImpl(Feed feed) {
        Long ifeedId = (Long) feed.get("id");
        Set<String> documentIds = new HashSet<>();

        feed.fill(database);
        String q = (feed.toIFeed().toQuery(client.fetchFields()));
        // System.out.println(q);
        Result r = client.query(q, 0, 1_000_000, "ASC", null,
                "dc.source.documentid", "vgr:VgrExtension.vgr:Source.id");
        if (r != null && r.getResponse() != null && r.getResponse().getDocs() != null) {
            for (Map<String, Object> doc : r.getResponse().getDocs()) {
                documentIds.add(
                        (String) (doc.containsKey("dc.source.documentid") ?
                                doc.get("dc.source.documentid") : doc.get("vgr:VgrExtension.vgr:Source.id"))
                );
            }
        }

        database.execute("delete from feed_document_index where ifeed_id = ?", ifeedId);
        for (String documentId : documentIds) {
            Map<String, Object> item = new HashMap<>();
            item.put("document_id", documentId);
            item.put("ifeed_id", ifeedId);
            database.insert("feed_document_index", item);
        }
    }

    private void copyDocumentIdAndFeedIdToTableImplOld(Feed feed) {
        Long ifeedId = (Long) feed.get("id");
        String template = getProperties().getProperty("ifeed.web.script.json.url") +
                "/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc"; //&f=dc.source.documentid&f=vgr:VgrExtension.vgr:Source.id";

        final String serviceUrl = String.format(template, ifeedId);

        List result = null;
        try {
            String raw = Http.get(serviceUrl);
            result = gson.fromJson(raw, List.class);
        } catch (Exception e) {
            System.out.println("Response faulty for this feed: " + feed);
            return;
        }
        HashSet<String> documentIds = new HashSet<>();
        for (Object o : result) {
            Map map = (Map) o;
            Object v = map.get("vgr:VgrExtension.vgr:Source.id");
            if (v == null) {
                v = map.get("dc.source.documentid");
            }
            documentIds.add((String) v);
        }
        database.execute("delete from feed_document_index where ifeed_id = ?", ifeedId);
        for (String documentId : documentIds) {
            Map<String, Object> item = new HashMap<>();
            item.put("document_id", documentId);
            item.put("ifeed_id", ifeedId);
            database.insert("feed_document_index", item);
        }
    }

}
