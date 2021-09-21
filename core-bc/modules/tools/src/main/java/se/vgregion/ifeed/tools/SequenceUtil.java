package se.vgregion.ifeed.tools;

import java.util.List;
import java.util.Map;

public class SequenceUtil {

    /*public static void restartWithHighestPrimaryKeyPlusOne(DatabaseApi database) {
        MutableInt largestPk = new MutableInt(0);

        Pgres pgres = new Pgres(database);

        for (Table table : pgres.pub.getTables()) {
            List<Prim> prims = table.getPrims();

            if (prims.isEmpty()) {
                System.out.println("Warning: Table " + table.getTableName().value() + " had no prims!");
            } else if (prims.size() != 1) {
                System.out.println("Warning: Table " + table.getTableName().value() + " had not just one prim! Namely "
                        + prims.stream().map(p -> p.getColumnName().value()).collect(Collectors.joining(", "))
                );
            } else {
                Prim prim = prims.get(0);
                Object largest =
                        database.query(String.format("select max(%s) from %s", prim.getColumnName().value(), prim.getTableName().value()), 0, 1)
                                .get(0).values().iterator().next();
                if (largest instanceof Number) {
                    Number numberId = (Number) largest;
                    if (largestPk.value().longValue() < numberId.longValue()) {
                        largestPk.set(numberId.intValue());
                    }
                } else {
                    System.out.println("Info: Not a number -> " + prim.getTableName().value() + "." + prim.getColumnName().value() + " = " + largest);
                }
            }
        }
        largestPk.inc(1);
        database.update("alter sequence hibernate_sequence restart with " + largestPk.value());
    }*/

    public static Long getNextValueFrom(DatabaseApi database, String sequence) {
        String sql = String.format("select nextval('%s')", sequence);
        List<Tuple> hsLastValue = database.query(sql);
        return (Long) hsLastValue.get(0).values().iterator().next();
    }

    public static Long getNextHibernateSequeceValue(DatabaseApi from) {
        return getNextValueFrom(from, "hibernate_sequence");
    }

    public static Long getLatestValueFrom(DatabaseApi database, String sequence) {
        String sql = String.format("select last_value from %s", sequence);
        List<Tuple> hsLastValue = database.query(sql);
        return (Long) hsLastValue.get(0).values().iterator().next();
    }

    public static boolean create(DatabaseApi in, String that, Long withIncrement, Long andStart) {
        String sql = String.format("CREATE SEQUENCE IF NOT EXISTS %s INCREMENT %s START %s;", that, withIncrement.toString(), andStart.toString());
        return in.execute(sql);
    }

}
