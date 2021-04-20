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
    }

    @Override
    public void generateFlows() {
        // ID: 2019 APT BMS ( Id: 129597 ) och APT 2020 BMS ( Id: 130138 )  till Sharepointytan https://vgregion.sharepoint.com/sites/sy-sv-bemanningsservice-alingsas

        generateFlows(" and f.id in (124741\n" +
                ", 120943\n" +
                ", 120707\n" +
                ", 437590100\n" +
                ", 437589933\n" +
                ", 120661\n" +
                ", 120697\n" +
                ", 120709\n" +
                ", 120701\n" +
                ", 120702\n" +
                ", 124742\n" +
                ", 120703\n" +
                ", 120705\n" +
                ", 120706) ");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
        database.commit();
    }

}
