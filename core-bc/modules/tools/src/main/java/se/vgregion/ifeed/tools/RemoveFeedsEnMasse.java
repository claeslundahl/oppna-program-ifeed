package se.vgregion.ifeed.tools;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class RemoveFeedsEnMasse {

    static DatabaseApi database = DatabaseApi.getLocalApi();

    public static void main(String[] args) {
        System.out.println(database.getUrl());
        // System.out.println(getItemIdsToRemove());

        if(true) return;

        long before = (long) database.query("select count(*) from vgr_ifeed").get(0).values().iterator().next();
        System.out.println("Antal i databasen före: " + before);
        int count = 0;
        for (Long id : getItemIdsToRemove()) {
            List<Map<String, Object>> tuples = database.query("select * from vgr_ifeed where id = ?", new Span(0, 1), id);
            if (!tuples.isEmpty()) {
                Feed feed = Feed.toFeed(tuples.get(0));
                feed.fill(database);
                feed.delete(database);
                System.out.println(feed.get("id"));
                count++;
            }
        }
        System.out.println("Raderade " + count + " st.");
        long after = (long) database.query("select count(*) from vgr_ifeed").get(0).values().iterator().next();
        System.out.println("Antal i databasen efter: " +
                after);
        System.out.println("Skillnaden är " + (before - after));
        database.commit();
    }


    static NavigableSet<Long> getItemIdsToRemove() {
        String raw = "4082171\n" +
                "4081908\n" +
                "4265989\n" +
                "4581110\n" +
                "125384\n" +
                "125388\n" +
                "125392\n" +
                "4580900\n" +
                "4552579\n" +
                "4552581\n" +
                "4567712\n" +
                "4093864\n" +
                "37322\n" +
                "4577815\n" +
                "4577663\n" +
                "4577667\n" +
                "4577711\n" +
                "437587158\n" +
                "65280\n" +
                "4579403\n" +
                "4579404\n" +
                "4579406\n" +
                "37255\n" +
                "37258\n" +
                "37261\n" +
                "128973\n" +
                "4577662\n" +
                "4577654\n" +
                "437599810\n" +
                "127080\n" +
                "127084\n" +
                "127627\n" +
                "4577653\n" +
                "4577101\n" +
                "4577103\n" +
                "4577105\n" +
                "4577106\n" +
                "4577107\n" +
                "4577110\n" +
                "437585202\n" +
                "437587458\n" +
                "117003\n" +
                "120573\n" +
                "4568963\n" +
                "4580714\n" +
                "4581080\n" +
                "437590315\n" +
                "4067193\n" +
                "4067193\n" +
                "4067193\n" +
                "4539040\n" +
                "4539040\n" +
                "4581112\n" +
                "4081994\n" +
                "4081994\n" +
                "66124\n" +
                "4081994\n" +
                "4085815\n" +
                "4085815\n" +
                "437593150\n" +
                "437590315\n" +
                "437590315\n" +
                "437593150";

        NavigableSet<Long> result = new TreeSet<>();
        for (String rawId : raw.split(Pattern.quote("\n"))) {
            result.add(Long.parseLong(rawId) * -1);
        }
        return result;
    }


}
