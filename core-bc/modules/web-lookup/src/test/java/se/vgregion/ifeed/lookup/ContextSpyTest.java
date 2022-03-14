package se.vgregion.ifeed.lookup;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.FeedDocumentIndexSupport;
import se.vgregion.ifeed.tools.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContextSpyTest {

    static DatabaseApi database = DatabaseApi.getDatabaseApi();

    public static void main(String[] args) {
        FeedDocumentIndexSupport.main();
        try {
            System.out.println("Nytt försök...");

            // removeAllBut(130904L);
        } catch (Exception e) {
            database.rollback();
            throw new RuntimeException(e);
        }
    }

    static void removeAllBut(Long... these) {
        String sql = String.format("select * from vgr_ifeed where id not in (%s)",
                Arrays.asList(these).stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
        System.out.println(sql);
        List<Tuple> items = database.query(sql);
        System.out.println("Deletes " + items.size() + " items. From " + database.getUrl());
        int i = 0;
        for (Tuple that : items) {
            i++;
            Feed existing = getFeed(((Long) that.get("id")));
            existing.fill(database);
            existing.delete(database);

            if (i % 100 == 0) System.out.print(" " + String.format("%05d", i));
            if (i % 1000 == 0){
                System.out.println();
                database.commit();
            }
        }
        database.commit();
    }

    static Feed getFeed(Number withId) {
        List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", withId);
        if (items.isEmpty()) return null;
        return Feed.toFeed(items.get(0));
    }

}