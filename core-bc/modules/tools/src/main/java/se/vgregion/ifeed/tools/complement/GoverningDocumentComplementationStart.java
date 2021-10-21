package se.vgregion.ifeed.tools.complement;

import org.apache.commons.lang.mutable.MutableInt;
import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Tuple;

import java.util.List;
import java.util.stream.Collectors;

public class GoverningDocumentComplementationStart {


    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println("Database: " + database.getUrl());
        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(database);
        try {
            List<Tuple> items = database.query("select * from vgr_ifeed vi where vi.name like ?", "Kompletterande flöde%");
            List<Feed> feeds = Feed.toFeeds(items);
            for (Feed feed : feeds) {
                feed.fill(database);
                System.out.println(feed.get("name") + " " + feed.get("creation_time") + " " + feed.get("userid"));
                System.out.println(feed.toText());
                System.out.println();
                System.out.println(gdc.makeComplement(feed).toText());
                System.out.println("-----------------------------------------------------------------------------------");
            }
            gdc.rollback();
        } catch (Exception e) {
            gdc.rollback();
            throw new RuntimeException(e);
        }
    }

    /*public static void main(String[] args) {
        DatabaseApi source = DatabaseApi.getRemoteProdDatabaseApi();
        DatabaseApi target = DatabaseApi.getRemoteStageDatabaseApi();
        try {
            copyHiddenFields(source, target);
            // SequenceUtil.checkAndOrFixHibernateIndex(target);
            target.commit();
        } catch (Exception e) {
            target.rollback();
            throw new RuntimeException(e);
        } finally {
            source.rollback();
            // target.rollback();
        }
    }*/

    private static void copyHiddenFields(DatabaseApi from, DatabaseApi to) {
        System.out.println("Source: " + from.getUrl());
        System.out.println("Target: " + to.getUrl());

        //  if (true) return;

        List<Tuple> roots = from.query("select * from field_inf where name = ?", "Gömda");
        if (roots.size() != 1)
            throw new RuntimeException();
        FieldInf root = FieldInf.toFieldInf(roots.get(0));
        List<Tuple> branches = from.query("select * from field_inf where parent_pk = ?", root.get("pk"));
        if (branches.size() != 1)
            throw new RuntimeException();
        FieldInf branch = FieldInf.toFieldInf(branches.get(0));

        to.insert("field_inf", root);
        to.insert("field_inf", branch);

        String sql = "select leaf.*\n" +
                "from field_inf leaf\n" +
                "join field_inf branch on leaf.parent_pk = branch.pk\n" +
                "join field_inf trunk on branch.parent_pk = trunk.pk\n" +
                "where trunk.name = 'Gömda'\n" +
                "order by 1, 2, 3";

        List<Tuple> newFilters = from.query(sql);

        List<FieldInf> items = newFilters.stream().map(t -> FieldInf.toFieldInf(t)).collect(Collectors.toList());

        final MutableInt insertCount = new MutableInt(0);

        items.forEach(i -> insertCount.setValue(to.insert("field_inf", i) + insertCount.intValue()));
        System.out.println("Lade till " + insertCount + " antal löv.");
    }

}
