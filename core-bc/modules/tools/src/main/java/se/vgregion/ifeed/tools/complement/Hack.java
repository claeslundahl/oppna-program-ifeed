package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static se.vgregion.ifeed.tools.complement.GoverningDocComplettion.print;

public class Hack {

    static DatabaseApi database = DatabaseApi.getRemoteProdDatabaseApi();

    public static void main(String[] args) {
        // fixSofiaFilters();
        // delete();
    }

    public static void delete() {
        // DatabaseApi database = DatabaseApi.getLocalApi();
        String delete = "delete from vgr_ifeed_filter where ifeed_id in (\n" +
                "select \"id\" from vgr_ifeed where id < 0 and name like '%(sty. dok. komplement)'\n" +
                "\t) \n" +
                "\tand filterkey = 'vgrsd:DomainExtension.domain' and filterquery = 'Styrande dokument'\n";
        System.out.println(database.update(delete));
        database.commit();
    }

    public static void fixSofiaFilters() {
        // DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println(database.getUrl());
        List<Tuple> items = database.query("select * from vgr_ifeed where id < 0 and name like ?", "%(SOFIA-komplement)");
        List<Tuple> sofiaFields = getFieldTree(database, "SOFIA samarbetsyta");
        final HashSet<Number> sofiaFieldsPks = new HashSet<Number>(sofiaFields.stream().map(sf -> (Number) sf.get("pk")).collect(Collectors.toList()));
        final Set<Long> errorFeedIds = new HashSet<>();

        for (final Tuple item : items) {
            final Feed feed = Feed.toFeed(item);
            feed.fill(database);
            feed.visit(filter -> {
                Number fieldPk = (Number) filter.get("field_inf_pk");
                if (fieldPk != null) {
                    if (!sofiaFieldsPks.contains(fieldPk)) {
                        // System.out.println("Har inte! " + filter);
                        errorFeedIds.add((Long) feed.get("id"));
                        Tuple replacement = sofiaFields.stream().filter(tuple -> tuple.get("id").equals(filter.get("filterkey"))).findAny().get();
                        int r = database.update("update vgr_ifeed_filter set field_inf_pk = ? where id = ?", replacement.get("pk"), filter.get("id"));
                        if (r != 1) {
                            database.rollback();
                            throw new RuntimeException();
                        }
                    }
                }
            });
        }
        database.commit();
        System.out.println(items.size());
        System.out.println();
        System.out.println(errorFeedIds);
        System.out.println(errorFeedIds.size());
    }


    static List<Tuple> getFieldTree(DatabaseApi database, String rootName) {
        String sql = "select distinct root.name, branch.name, leaf.name, leaf.id, leaf.pk\n" +
                "from field_inf leaf\n" +
                "join field_inf branch on branch.pk = leaf.parent_pk\n" +
                "join field_inf root on root.pk = branch.parent_pk\n" +
                "where root.name = ?\n" +
                "order by 1, 2, 3";
        List<Tuple> items = database.query(sql, rootName);

        return items;
    }


    public static void older(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", -127596);
        for (Tuple item : items) {
            Feed feed = Feed.toFeed(item);
            feed.fill(database);
            print(feed);

            /*List pks = new ArrayList();

            feed.visit(filter -> {
                FieldInf field = filter.getFieldInf();
                if (field==null) return;
                pks.add(field.get("pk"));
                System.out.println("Filter: " + filter);
                field.load(database);
                System.out.println(" " + field);
            });
            System.out.println(pks);*/
        }
    }


    public static void old(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println(database.getUrl());
        try {
            GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(database);
            List<Tuple> items = database.query("select * from vgr_ifeed vi where vi.id < 0 and vi.name like ?", new Object[]{"%(sty. dok. komplement)"});
            System.out.println("Hittade " + items.size());
            for (Tuple item : items) {
                Feed feed = Feed.toFeed(item);
                feed.fill(database);
                List<Filter> nfs = gdc.addDefaultFilters(feed);
                for (Filter nf : nfs) {
                    nf.put("id", SequenceUtil.getNextHibernateSequeceValue(database));
                    nf.insert(database, feed, null);
                }
                System.out.println(item);
                // if (true) throw new RuntimeException();
            }
        } catch (Exception e) {
            database.rollback();
            e.printStackTrace();
        }
        database.commit();
    }

}
