package se.vgregion.ifeed.lookup;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.FeedDocumentIndexSupport;
import se.vgregion.ifeed.tools.Tuple;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static se.vgregion.common.utils.Props.fetchProperties;

public class App implements Serializable {

    static long idIndex = 0;

    private long id = idIndex++;

    private List<Feed> result;

    public static DatabaseApi getDatabase() {
        try {
            if (database == null || database.getConnection().isClosed())
                initConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return database;
    }

    private static DatabaseApi database;

    private static String adminUrl;

    static void initConnection() {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties");
            if (Files.exists(path)) {
                System.out.println("Path to settings " + path);
                Properties props = fetchProperties(path);
                adminUrl = props.getProperty("admin.url");
                for (Object key : props.keySet()) {
                    props.getProperty((String) key);
                }
                database = DatabaseApi.getLocalApi();
            } else {
                System.out.println("Hittar inte filen!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Feed> fetch(String forThat) {
        if (forThat == null || "".equals(forThat.trim())) {
            result = null;
            return new ArrayList<>();
        }
        forThat = forThat.toLowerCase(Locale.ROOT);

        final String sql = "select vif.* from feed_document_index ind \n" +
                "join vgr_ifeed vif on vif.id = ind.ifeed_id\n" +
                "where lower(ind.document_id) = ?";
        List<Tuple> result = getDatabase().query(sql, forThat);
        List<Feed> feeds = Feed.toFeeds(result);
        for (Feed feed : feeds) {
            feed.fill(getDatabase());
        }
        setResult(feeds);
        return feeds;
    }

    public List<Feed> getResult() {
        return result;
    }

    public void setResult(List<Feed> result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String reindex() {
        if (!FeedDocumentIndexSupport.isRunning()) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule((Runnable) () -> FeedDocumentIndexSupport.main(), 20l, TimeUnit.SECONDS);
            return "Startar körning.";
        } else {
            return "Indexering körs på "
                    + FeedDocumentIndexSupport.totalCount
                    + " flöden. Är på flöde #"
                    + FeedDocumentIndexSupport.runCounter + ".";
        }
    }

    public String getAdminUrl() {
        return adminUrl;
    }

}
