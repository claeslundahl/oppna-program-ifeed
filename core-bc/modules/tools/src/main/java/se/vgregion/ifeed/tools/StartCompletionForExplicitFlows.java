package se.vgregion.ifeed.tools;

import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;

import java.util.List;

public class StartCompletionForExplicitFlows extends SofiaFlowCompletion {

    {
        database = DatabaseApi.getRemoteProdDatabaseApi();
    }

    /**
     * After running this - the hibernate sequence must be updated!
     *
     * @param args
     */
    public static void main(String[] args) {
        StartCompletionForExplicitFlows sofia = new StartCompletionForExplicitFlows();
        System.out.println(sofia.database.getUrl());
        // if (true) return;

        // Table table = sofia.database.getAllTables().stream().filter(t -> t.getTableName().equals("")).findFirst().get();
        /*List<Tuple> stuff = sofia.database.query("select * from vgr_ifeed where id in (-4173301, -4554404)");
        List<Feed> feeds = Feed.toFeeds(stuff);
        for (Feed feed : feeds) {
            feed.fill(sofia.database);
            feed.delete(sofia.database);
        }*/
/*        sofia.database.rollback();
        if (true) return;*/
        sofia.main();
        SequenceUtil.checkAndOrFixHibernateIndex(sofia.database);
        sofia.database.commit();
    }

    @Override
    public void generateFlows() {
        // ID: 2019 APT BMS ( Id: 129597 ) och APT 2020 BMS ( Id: 130138 )  till Sharepointytan https://vgregion.sharepoint.com/sites/sy-sv-bemanningsservice-alingsas
        generateFlows(" and f.id in (437592519, 131326, 437594573, 437586425, 437592487, 437585755, 4326810, 118866, 128800, 437585259, 437585003, 437585242, 437585249, 437585254, 437585237, 437585065, 437585009, 437600622\n)\n");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
