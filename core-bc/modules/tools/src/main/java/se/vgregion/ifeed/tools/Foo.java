package se.vgregion.ifeed.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Foo {

    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getRemoteProdDatabaseApi();
        List<Tuple> items = database.query(String.format("select * from vgr_ifeed where id in (%s)",
                "437620188, 437620193, 437620197,    437603883, 437620202, 437620207, 437620212, 437620217, 437620222, " +
                        "437620227, 437620232, 437620237, 437620242, 437620247, 437620252, 437620257, 437620262, " +
                        "437616887, 437616881, 73933, 4572616, 437616897, 437616902, 437616916, 74779, 437616921, " +
                        "437616871, 437616876, 59545, 437603878, 437616926, 437616931, 437616932, 74773, 437616937"));
        System.out.println(items.size());
        for (Tuple item : items) {
            Feed feed = Feed.toFeed(item);
            feed.fill(database);
            final Set<FieldInf> roots = new HashSet<>();
            feed.visit(f -> {
                if (f.getFieldInf() != null) {
                    FieldInf fi = f.getFieldInf();
                    roots.add(fi.getParent().getParent());
                }
            });
            Object[] params = new Object[1];
            params[0] = (((Long) item.get("id")) * -1);
            boolean hasComplement = !database.query("select * from vgr_ifeed where id = ?", 0, 1, params).isEmpty();
            System.out.println(new TreeSet<>(roots.stream().map(i -> i.get("name")).collect(Collectors.toSet())) + "\t" + feed.get("name") + "\t" + feed.get("id") + "\t" + (hasComplement ? "Ja" : "Nej"));
        }
    }

}
