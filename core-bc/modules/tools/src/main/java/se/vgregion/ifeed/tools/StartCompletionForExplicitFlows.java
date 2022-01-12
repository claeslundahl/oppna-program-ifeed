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

        /*List<Tuple> stuff = sofia.database.query("select * from vgr_ifeed where id in (-437594114, -437589991)");
        List<Feed> feeds = Feed.toFeeds(stuff);
        for (Feed feed : feeds) {
            feed.fill(sofia.database);
            feed.delete(sofia.database);
        }
        sofia.database.commit();*/

        sofia.main();
        SequenceUtil.checkAndOrFixHibernateIndex(sofia.database);
        sofia.database.commit();
    }

    @Override
    public void generateFlows() {
        generateFlows(" and f.id in (437594114, 437589991)");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
