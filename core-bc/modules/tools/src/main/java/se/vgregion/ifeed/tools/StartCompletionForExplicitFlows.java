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
        if (true) return;
        sofia.main();
        sofia.database.commit();
    }

    @Override
    public void generateFlows() {
        // ID: 2019 APT BMS ( Id: 129597 ) och APT 2020 BMS ( Id: 130138 )  till Sharepointytan https://vgregion.sharepoint.com/sites/sy-sv-bemanningsservice-alingsas

        generateFlows(" and f.id in (130398, 130420, 130401, 130410, 130413, 130424, 130427, 130430, 130433, " +
                "130436, 130439, 130442, 437585875, 130493, 130496, 130499, 130502, 130505, 130508, 130511, 130514, 130564, " +
                "130567, 130570, 130573, 130576, 130579, 130582, 130585, 130588, 130591, 130597, 130600, 130603, 130606, " +
                "130609, 130612, 130618, 130621, 130624, 130627, 130630, 130615, 437595126, 437595089, 437594802, " +
                "437594805, 437594808, 437594812) ");
    }

    @Override
    public void main() {
        // deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
    }

}
