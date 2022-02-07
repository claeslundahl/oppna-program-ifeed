package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpecialGovDocRun {

    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println(database.getUrl());

        if (true) return;

        SortedSet<Number> foundAndProcessed = new TreeSet<>();
        SortedSet<Number> notProcessed = new TreeSet<>();
        SortedSet<Number> notFound = new TreeSet<>();
        int i = 0;
        try {
            GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(database);
            final List<Number> ids = getFeedIdsToRun();
            System.out.println("Antal flöden att undersöka och möjligen ändra: " + ids.size());

            for (Number id : ids) {
                i++;
                if (i % 10 == 0) System.out.print(" " + String.format("%03d", i));
                if (i % 100 == 0) System.out.println();
                List<Tuple> items = database.query("select * from vgr_ifeed vi where vi.id = ?", id);
                for (Tuple item : items) {
                    if (null != gdc.makeComplement(id))
                        foundAndProcessed.add(id);
                    else
                        notProcessed.add(id);
                }
                if (items.isEmpty())
                    notFound.add(id);
            }
            System.out.println();
            System.out.println("Antal om-kompletterade: " + foundAndProcessed.size());
            System.out.println(foundAndProcessed);
            System.out.println("Antalet ej omkompletterade: " + notProcessed.size());
            System.out.println(notProcessed);
            System.out.println("Antalet ej funna: " + notFound.size());
            System.out.println(notFound);
            database.commit();
        } catch (Exception e) {
            e.printStackTrace();
            database.rollback();
        }
    }

    static List<Number> getFeedIdsToRun() {
        String asText =
                "4444360\n" +
                        "437588342\n" +
                        "437598264\n" +
                        "437618646\n" +
                        "437586802\n" +
                        "437589353\n\n" +
                        "437594648\n" +
                        "437595054\n" +
                        "437595001\n" +
                        "4232677\n" +
                        "437593904\n" +
                        "36341\n" +
                        "36352\n" +
                        "36355\n" +
                        "36358\n" +
                        "36361\n" +
                        "36364\n" +
                        "36367\n" +
                        "36370\n" +
                        "36373\n" +
                        "36376\n" +
                        "36379\n" +
                        "36380\n" +
                        "36383\n" +
                        "36386\n" +
                        "36389\n" +
                        "36390\n" +
                        "36393\n" +
                        "36396\n" +
                        "36399\n" +
                        "36402\n" +
                        "36405\n" +
                        "36408\n" +
                        "36411\n" +
                        "36414\n" +
                        "36417\n" +
                        "36421\n" +
                        "36426\n" +
                        "36427\n" +
                        "36431\n" +
                        "36434\n" +
                        "36437\n" +
                        "36440\n" +
                        "36443\n" +
                        "36446\n" +
                        "36449\n" +
                        "36452\n" +
                        "36457\n" +
                        "36462\n" +
                        "36465\n" +
                        "36468\n" +
                        "36471\n" +
                        "36493\n" +
                        "36496\n" +
                        "36499\n" +
                        "36502\n" +
                        "36506\n" +
                        "36509\n" +
                        "36512\n" +
                        "36515\n" +
                        "36521\n" +
                        "36524\n" +
                        "36527\n" +
                        "36530\n" +
                        "36533\n" +
                        "36536\n" +
                        "36539\n" +
                        "36600\n" +
                        "37004\n" +
                        "52960\n" +
                        "53271\n" +
                        "53754\n" +
                        "53758\n" +
                        "53761\n" +
                        "53775\n" +
                        "53776\n" +
                        "53815\n" +
                        "57048\n" +
                        "57051\n" +
                        "57054\n" +
                        "57058\n" +
                        "57061\n" +
                        "57064\n" +
                        "57067\n" +
                        "57070\n" +
                        "57073\n" +
                        "57893\n" +
                        "58689\n" +
                        "62807\n" +
                        "63641\n" +
                        "66089\n" +
                        "66089\n" +
                        "92449\n" +
                        "92452\n" +
                        "92455\n" +
                        "104691\n" +
                        "113622\n" +
                        "114035\n" +
                        "114037\n" +
                        "114857\n" +
                        "114859\n" +
                        "114860\n" +
                        "114861\n" +
                        "114864\n" +
                        "114866\n" +
                        "114872\n" +
                        "114874\n" +
                        "114887\n" +
                        "114889\n" +
                        "114922\n" +
                        "114923\n" +
                        "114937\n" +
                        "115322\n" +
                        "115367\n" +
                        "115373\n" +
                        "115398\n" +
                        "115603\n" +
                        "115606\n" +
                        "115616\n" +
                        "115636\n" +
                        "115683\n" +
                        "116459\n" +
                        "116461\n" +
                        "116839\n" +
                        "116842\n" +
                        "116845\n" +
                        "116850\n" +
                        "116975\n" +
                        "116978\n" +
                        "116982\n" +
                        "116990\n" +
                        "117053\n" +
                        "117062\n" +
                        "117536\n" +
                        "117862\n" +
                        "118379\n" +
                        "119033\n" +
                        "119037\n" +
                        "119066\n" +
                        "119068\n" +
                        "119405\n" +
                        "121050\n" +
                        "4305913\n" +
                        "4305921\n" +
                        "4305926\n" +
                        "4305932\n" +
                        "4307768\n" +
                        "4340108\n" +
                        "4340115\n" +
                        "4340120\n" +
                        "4340135\n" +
                        "4340140\n" +
                        "4340145\n" +
                        "4424520\n" +
                        "4539649\n" +
                        "4539653\n" +
                        "4539657\n" +
                        "4539663\n" +
                        "4539667\n" +
                        "4539672\n" +
                        "4539677\n" +
                        "4539678\n" +
                        "4541461\n" +
                        "4556325\n" +
                        "4556328\n" +
                        "4556329\n" +
                        "4556335\n" +
                        "4556338\n" +
                        "4556339\n" +
                        "4556342\n" +
                        "4568968\n" +
                        "4568976\n" +
                        "4577273\n" +
                        "4578360\n" +
                        "4579093\n" +
                        "4579343\n" +
                        "4579348\n" +
                        "4579351\n" +
                        "4579354\n" +
                        "4579355\n" +
                        "4579356\n" +
                        "4579361\n" +
                        "4579366\n" +
                        "4579369\n" +
                        "4579372\n" +
                        "4579375\n" +
                        "4581132\n" +
                        "122376\n" +
                        "122389\n" +
                        "122398\n" +
                        "122412\n" +
                        "122426\n" +
                        "122440\n" +
                        "122488\n" +
                        "122955\n" +
                        "122996\n" +
                        "123139\n" +
                        "122308\n" +
                        "123384\n" +
                        "123393\n" +
                        "123840\n" +
                        "124426\n" +
                        "127353\n" +
                        "127435\n" +
                        "127453\n" +
                        "127465\n" +
                        "127477\n" +
                        "127489\n" +
                        "127499\n" +
                        "127508\n" +
                        "127517\n" +
                        "127526\n" +
                        "127535\n" +
                        "127544\n" +
                        "127553\n" +
                        "127565\n" +
                        "127859\n" +
                        "128650\n" +
                        "128799\n" +
                        "129078\n" +
                        "129919\n" +
                        "127443\n" +
                        "437599541\n" +
                        "437599252\n" +
                        "437599547\n" +
                        "437599634\n" +
                        "437599489\n" +
                        "437599505\n" +
                        "437599835\n" +
                        "437599889\n" +
                        "437600402\n" +
                        "437601158\n" +
                        "437585862\n" +
                        "437585881\n" +
                        "437585893\n" +
                        "437585907\n" +
                        "437585939\n" +
                        "437585945\n" +
                        "437588130\n" +
                        "437588444\n" +
                        "437588458\n" +
                        "437588480\n" +
                        "437588488\n" +
                        "437588603\n" +
                        "437588612\n" +
                        "437588621\n" +
                        "437588630\n" +
                        "437588639\n" +
                        "437588648\n" +
                        "437588657\n" +
                        "437588666\n" +
                        "437588678\n" +
                        "437588691\n" +
                        "437588700\n" +
                        "437588709\n" +
                        "437588719\n" +
                        "437588729\n" +
                        "437588739\n" +
                        "437588749\n" +
                        "437588758\n" +
                        "437589507\n" +
                        "437589160\n" +
                        "437589607\n" +
                        "437589623\n" +
                        "437590272\n" +
                        "437590280\n";
        asText = asText.trim();
        String[] parts = asText.split(Pattern.quote("\n"));
        List<Number> ids = Arrays.stream(parts).filter(f -> !"".equals(f.trim())).map(s -> Integer.parseInt(s)).collect(Collectors.toList());
        return ids;
    }

}
