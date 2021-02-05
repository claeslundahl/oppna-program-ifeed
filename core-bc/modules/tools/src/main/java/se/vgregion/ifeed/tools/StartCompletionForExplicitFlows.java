package se.vgregion.ifeed.tools;

import static se.vgregion.ifeed.tools.DatabaseApi.getDatabaseApi;
import static se.vgregion.ifeed.tools.DatabaseApi.getRemoteProdDatabaseApi;

public class StartCompletionForExplicitFlows extends SofiaFlowCompletion {

    {
        database = DatabaseApi.getRemoteStageDatabaseApi();
    }

    public static void main(String[] args) {
        StartCompletionForExplicitFlows sofia = new StartCompletionForExplicitFlows();
        System.out.println(sofia.database.getUrl());
        // if (true) throw new RuntimeException("Are you certain you want to run this?");
        sofia.main();
    }

    @Override
    public void generateFlows() {
        generateFlows(" and f.id in (450776112) ");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
        database.commit();
    }

}
