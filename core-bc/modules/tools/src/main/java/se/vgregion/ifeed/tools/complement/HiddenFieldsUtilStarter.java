package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.Tuple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HiddenFieldsUtilStarter {

    static DatabaseApi database;

    public static void main(String[] args) {
        // copyNewHiddenFieldsIntoDatabase();
        // fixHiddenFieldsConnection();
        fixHiddenFieldsConnectionForAll();
    }

    private static void copyNewHiddenFieldsIntoDatabase() {
        database = DatabaseApi.getRemoteProdDatabaseApi();
        HiddenFieldsUtil hiddenFieldsUtil = new HiddenFieldsUtil(database);
        hiddenFieldsUtil.copyNewHiddenFieldsIntoDatabase();
        database.commit();
    }

    private static void fixHiddenFieldsConnection() {
        database = DatabaseApi.getRemoteProdDatabaseApi();
        System.out.println("Target of fixHiddenFieldsConnection is " + database.getUrl());
        // if (true) return;
        copyNewHiddenFieldsIntoDatabase();

        List<Long> ids = Arrays.asList(
                121911, 437590915, 4560565, 437598226, 4569570, 66089, 130753, 130765, 4524938, 116957, 437590936, 4580964, 4568965, 129115, 121778, 116547, 4400853, 437590929, 121772, 84385, 116894, 121771, 4580864, 129174, 116532
        ).stream().map(id -> Long.valueOf((id * -1))).collect(Collectors.toList());
        fixHiddenFieldsConnection(ids.toArray(new Long[ids.size()]));
        database.commit();
    }

    private static void fixHiddenFieldsConnectionForAll() {
        try {
            database = DatabaseApi.getRemoteProdDatabaseApi();
            // database = DatabaseApi.getLocalApi();
            HiddenFieldsUtil hiddenFieldsUtil = new HiddenFieldsUtil(database);
            hiddenFieldsUtil.fixHiddenFieldsConnection();
            database.commit();
        } catch (Exception e) {
            database.rollback();
            throw new RuntimeException(e);
        }
    }


    private static void fixHiddenFieldsConnection(Long... ids) {
        HiddenFieldsUtil hiddenFieldsUtil = new HiddenFieldsUtil(database);
        List<Tuple> items = database.query(
                String.format("select * from vgr_ifeed where id in %s",
                        Collections.nCopies(ids.length, "?")
                                .toString().replace("[", "(")
                                .replace("]", ")")), ids);

        for (Tuple item : items) {
            Feed feed = Feed.toFeed(item);
            feed.fill(database);
            hiddenFieldsUtil.fixHiddenFieldsConnection(feed);
        }
    }

}
