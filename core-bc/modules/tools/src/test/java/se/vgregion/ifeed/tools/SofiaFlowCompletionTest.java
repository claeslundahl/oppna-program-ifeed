package se.vgregion.ifeed.tools;

import se.vgregion.ifeed.tools.*;

import java.util.List;

import static se.vgregion.ifeed.tools.DatabaseApi.getLocalApi;
import static se.vgregion.ifeed.tools.DatabaseApi.getRemoteProdDatabaseApi;

class SofiaFlowCompletionTest {

    // static SofiaFlowCompletion sofiaFlowCompletion = new SofiaFlowCompletion();

    public static void main(String[] args) {
        // 437592645 och 437584843
      //  main(-437592645l);
    }

    /*public static void main(Long id) {
        DatabaseApi database = getLocalApi();
        try {
            sofiaFlowCompletion.setDatabase(database);
            System.out.println(sofiaFlowCompletion.database.getUrl());
            List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", id);
            for (Tuple item : items) {
                Feed feed = Feed.toFeed(item);
                feed.fill(database);
                for (Filter filter : feed.getFilters()) {
                    fix(filter);
                }
            }
            database.rollback();
        } catch (Exception e) {
            database.rollback();
            throw new RuntimeException(e);
        }

    }*/

    /*static void fix(Filter filter) {
        String key = (String) filter.get("filterkey");
        if (key != null && !key.trim().equals("")) {
            sofiaFlowCompletion.fixHiddenFieldsConnection(filter);
        } else {
            for (Filter child : filter.getChildren()) {
                fix(child);
            }
        }
    }*/

}