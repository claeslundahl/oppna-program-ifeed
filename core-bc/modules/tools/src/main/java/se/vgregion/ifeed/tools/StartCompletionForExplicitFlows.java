package se.vgregion.ifeed.tools;

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

        /*List<Tuple> stuff = sofia.database.query("select * from vgr_ifeed where id in (-90019) ");
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
        generateFlows(" and f.id in (437585821, 4296853, 4297439, 65293, 117688, 118505\n" +
                "  ,130715, 130709,130703,130722,130727,130746,130733,4436572,4436577\n" +
                "  ,4281434 ,4524994 ,120865,119209 ,4298033,4298043,4288819, 4265395)");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
