package se.vgregion.ifeed.tools;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import static se.vgregion.ifeed.tools.DatabaseApi.getLocalBackupApi;

public class SmokeTest {

    // final SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    final DatabaseApi database;

    public SmokeTest(DatabaseApi database) {
        this.database = database;
    }

    // 1229, 130950, 437585745, 437586116

    public static void main(String[] args) {
        // Run just for 1229, 130950, 437585745, 437586116
        DatabaseApi database = getLocalBackupApi();
        SmokeTest smokeTest = new SmokeTest(database);
        for (Integer id : Arrays.asList(1229, 828377)) {
            Feed item = new Feed();
            item.put("id", id);
            smokeTest.run(item);
        }
        // start();
    }

    public static void start() {
        DatabaseApi database = getLocalBackupApi();
        System.out.println(database.getUrl());
        // if(true) return;
        SmokeTest smokeTest = new SmokeTest(database);
        smokeTest.run();
        System.out.println();
        System.out.println("De här var fel: ");
        System.out.println(smokeTest.errorFeeds);
        // [1229, 130950, 437585745, 437586116]
    }

    void run() {
        String sql = "select * from vgr_ifeed where id > 0 and id in (select distinct ifeed_id from vgr_ifeed_filter)";
        List<Feed> feeds = Feed.toFeeds(database.query(sql));
        int i = 0;
        System.out.println("Antal flöden att anropa är " + feeds.size());
        for (Feed feed : feeds) {
            run(feed);
            i++;
            if (i % 100 == 0)
                System.out.print(" " + i);

            if (i % 1000 == 0) System.out.println();
        }
    }

    String documentListUrl = "http://ifeed.vgregion.se/iFeed-web/documentlists/%s/?by=&dir=asc";
    String atomUrl = "http://ifeed.vgregion.se/iFeed-intsvc/documentlists/%s/feed?by=&dir=asc";
    String jsonUrl = "http://ifeed.vgregion.se/iFeed-web/documentlists/130932/metadata.json?by=&dir=asc&f=title";

    String ajaxListUrl = "https://ifeed.vgregion.se/iFeed-web/display?v=1&columnes=title%7CTitel%20(autokomplettering)%7Cleft%7C70&fontsize=20&defaultsortcolumn=title&defaultsortorder=asc&extrasortcolumns=%5B%5D&showtableheader=yes&linkoriginaldoc=no&limit=10&hiderightcolumn=no&usepost=no&feedid=[id]&callback=q4";


    void run(Feed forThat) {
        try {
            Urlz urlz = new Urlz();
            //urlz.read(String.format(documentListUrl, forThat.get("id").toString()));
            // urlz.read(String.format(atomUrl, forThat.get("id").toString()));
            // urlz.read(String.format(jsonUrl, forThat.get("id").toString()));
            urlz.read(ajaxListUrl.replace("[id]", forThat.get("id").toString()));
        } catch (Exception e) {
            /*System.out.println(e.getMessage());
            System.out.println(forThat);
            e.printStackTrace();
            throw new RuntimeException(e);*/
            errorFeeds.add((Number) forThat.get("id"));
        }
    }

    NavigableSet<Number> errorFeeds = new TreeSet<>();

}
