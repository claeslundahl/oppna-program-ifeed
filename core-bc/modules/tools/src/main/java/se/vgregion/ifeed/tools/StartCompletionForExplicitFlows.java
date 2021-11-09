package se.vgregion.ifeed.tools;

import static se.vgregion.ifeed.tools.DatabaseApi.getDatabaseApi;
import static se.vgregion.ifeed.tools.DatabaseApi.getRemoteProdDatabaseApi;

public class StartCompletionForExplicitFlows extends SofiaFlowCompletion {

    {
        database = DatabaseApi.getRemoteProdDatabaseApi();
    }

    /**
     * After running this - the hibernate sequence must be updated!
     * @param args
     */
    public static void main(String[] args) {
        StartCompletionForExplicitFlows sofia = new StartCompletionForExplicitFlows();
        System.out.println(sofia.database.getUrl());
        // if (true) return;
        sofia.main();
        SequenceUtil.checkAndOrFixHibernateIndex(sofia.database);
        sofia.database.commit();
    }

    @Override
    public void generateFlows() {
        // ID: 2019 APT BMS ( Id: 129597 ) och APT 2020 BMS ( Id: 130138 )  till Sharepointytan https://vgregion.sharepoint.com/sites/sy-sv-bemanningsservice-alingsas

        generateFlows(" and f.id in (48878, \n" +
                "4491673, \n" +
                "38278, \n" +
                "123943, \n" +
                "37837, \n" +
                "37840) ");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
