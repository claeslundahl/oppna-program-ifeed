package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.Tuple;

import java.util.List;

public class HiddenFieldsUtilStarter {

    public static void main(String[] args) {
        copyNewHiddenFieldsIntoDatabase();
    }

    private static void copyNewHiddenFieldsIntoDatabase() {
        DatabaseApi database = DatabaseApi.getLocalApi();
        HiddenFieldsUtil hiddenFieldsUtil = new HiddenFieldsUtil(database);
        hiddenFieldsUtil.copyNewHiddenFieldsIntoDatabase();
        database.commit();
    }

    private static void fixHiddenFieldsConnection() {
        DatabaseApi database = DatabaseApi.getLocalApi();
        HiddenFieldsUtil hiddenFieldsUtil = new HiddenFieldsUtil(database);
        List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", -437592645l);
        for (Tuple item : items) {
            Feed feed = Feed.toFeed(item);
            feed.fill(database);
            hiddenFieldsUtil.fixHiddenFieldsConnection(feed);
        }
        database.rollback();
    }

}
