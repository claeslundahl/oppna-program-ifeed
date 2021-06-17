package se.vgregion.ifeed.tools;

import static se.vgregion.ifeed.tools.DatabaseApi.getDatabaseApi;
import static se.vgregion.ifeed.tools.DatabaseApi.getRemoteProdDatabaseApi;

public class StartCompletionForExplicitFlows extends SofiaFlowCompletion {

    {
        database = DatabaseApi.getRemoteProdDatabaseApi();
    }

    public static void main(String[] args) {
        StartCompletionForExplicitFlows sofia = new StartCompletionForExplicitFlows();
        System.out.println(sofia.database.getUrl());
        // if (true) throw new RuntimeException("Are you certain you want to run this?");
        sofia.main();
        sofia.database.commit();
    }

    @Override
    public void generateFlows() {
        // ID: 2019 APT BMS ( Id: 129597 ) och APT 2020 BMS ( Id: 130138 )  till Sharepointytan https://vgregion.sharepoint.com/sites/sy-sv-bemanningsservice-alingsas

        generateFlows(" and f.id in (437589197,\n" +
                "437589205,\n" +
                "437589201,\n" +
                "437589193,\n" +
                "437593512,\n" +
                "130404,\n" +
                "119445,\n" +
                "119448,\n" +
                "127634,\n" +
                "437590517,\n" +
                "437586083,\n" +
                "437594191,\n" +
                "127639,\n" +
                "437590522) ");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
