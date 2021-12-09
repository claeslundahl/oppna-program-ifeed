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
        /*List<Tuple> stuff = sofia.database.query("select * from vgr_ifeed where id in (-4577959, -4545824)");
        List<Feed> feeds = Feed.toFeeds(stuff);
        for (Feed feed : feeds) {
            feed.fill(sofia.database);
            feed.delete(sofia.database);
        }
        sofia.database.commit();*/
        // if (true) return;
        sofia.main();
        SequenceUtil.checkAndOrFixHibernateIndex(sofia.database);
        sofia.database.commit();
    }

    @Override
    public void generateFlows() {
        // ID: 2019 APT BMS ( Id: 129597 ) och APT 2020 BMS ( Id: 130138 )  till Sharepointytan https://vgregion.sharepoint.com/sites/sy-sv-bemanningsservice-alingsas
        generateFlows(" and f.id in (4331739)");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
