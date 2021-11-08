package se.vgregion.ifeed.tools.complement;

import org.apache.commons.lang.mutable.MutableInt;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Tuple;
import se.vgregion.ifeed.types.IFeed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GoverningDocumentComplementationStart {

    public static void removeCompletedFlows(DatabaseApi database) {
        List<Tuple> items = database.query("select * from vgr_ifeed vi where vi.id < 0 and vi.name like ?", "Kompletterande flöde%");
        List<Feed> feeds = Feed.toFeeds(items);
        for (Feed feed : feeds) {
            feed.fill(database);
            feed.delete(database);
            System.out.println(feed);
        }
    }

    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getRemoteStageDatabaseApi();
        System.out.println("Database: " + database.getUrl());
        if (true) return;
        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(database);
        Set<Long> ids = new HashSet<>(Arrays.asList(116514l,
                3830183l,
                52112l,
                4095626l, 3970197l,
                4095626l, 3970197l,
                4095626l, 3970197l,
                4095626l, 3970197l,
                3970207l,
                3970207l,
                3970207l,
                3970197l,
                3752286l,
                3752286l
        ));
        for (Long id : ids) {
            System.out.println(gdc.makeComplement(id));
        }
        gdc.commit();
    }

    public static void main2(String[] args) {
        DatabaseApi database = DatabaseApi.getRemoteStageDatabaseApi();
        System.out.println("Database: " + database.getUrl());

        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(database);

        try {
            List<Tuple> items = database.query("select * from vgr_ifeed vi where vi.id > 0 and vi.name like ?", "Kompletterande flöde%");
            List<Feed> feeds = Feed.toFeeds(items);
            StringBuilder sb = new StringBuilder();
            for (Feed feed : feeds) {
                feed.fill(database);
                sb.append("\n");
                sb.append(feed.get("name") + " " + feed.get("creation_time") + " " + feed.get("userid"));
                sb.append("\n");
                sb.append(feed.toText());
                sb.append("\n");
                sb.append("\n");
                Feed completed = gdc.makeComplement(feed);
                if (completed == null) {
                    System.out.println("Flöde utan resultat: " + feed);
                } else {
                    completed.fill(database);
                    sb.append(completed.toText());
                    sb.append("\n");
                    sb.append("-----------------------------------------------------------------------------------");
                }

            }
            Path path = Paths.get(System.getProperty("user.home"), "comp.txt");

            Files.writeString(path, sb.toString());

            gdc.commit();

        } catch (Exception e) {
            gdc.rollback();
            throw new RuntimeException(e);
        }
    }

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    static boolean hasHits(Feed feed) throws IOException, InterruptedException {
        try {
            IFeed ifeed = feed.toIFeed();
            // System.out.println(ifeed.toQuery(client.fetchFields()));
            Result result = client.query(ifeed.toQuery(client.fetchFields()), 0, 1, "asc", null);
            for (Map<String, Object> doc : result.getResponse().getDocs()) {
                System.out.println(doc);
            }
            return (result.getResponse() != null && result.getResponse().getDocs() != null && !result.getResponse().getDocs().isEmpty());
        } catch (Exception e) {
            return false;
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

        if (to.query("select * from field_inf where pk = ?", root.get("pk")).isEmpty()) {
            to.insert("field_inf", root);
        }
        if (to.query("select * from field_inf where pk = ?", branch.get("pk")).isEmpty()) {
            to.insert("field_inf", branch);
        }

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
