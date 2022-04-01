package se.vgregion.ifeed.tools;

import javax.xml.crypto.Data;
import java.util.List;

public class StartCompletionForExplicitFlows extends SofiaFlowCompletion {



    protected DatabaseApi initDatabase() {
        return DatabaseApi.getRemoteProdDatabaseApi();
    }

    {
        database = initDatabase();
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
        generateFlows(" and f.id in (437593303)");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
