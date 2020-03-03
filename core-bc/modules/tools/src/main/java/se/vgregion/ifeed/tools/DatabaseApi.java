package se.vgregion.ifeed.tools;

import se.vgregion.arbetsplatskoder.db.migration.sql.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseApi extends ConnectionExt {

    public DatabaseApi(String url, String user, String password, String driver) {
        super(url, user, password, driver);
    }

    public DatabaseApi(Connection connection) {
        super(connection);
    }

    public static Junctor toJunctor(Map<String, Object> fromThat) {
        Junctor result = Junctor.and();
        for (String key : fromThat.keySet()) {
            result.add(new Match(new Atom(key), " = ", new ValueRef(fromThat.get(key))));
        }
        return result;
    }

    public List<Tuple> query(String question, Object... withParameters) {
        return toTuples(super.query(question, 0, 1_000_000, withParameters));
    }

    static Tuple toTuple(Map<String, Object> item) {
        Tuple result = new Tuple();
        result.putAll(item);
        return result;
    }

    static List<Tuple> toTuples(List<Map<String, Object>> items) {
        List<Tuple> result = new ArrayList<>();
        for (Map<String, Object> item : items) {
            result.add(toTuple(item));
        }
        return result;
    }

    public Object oneFieldSingleValueQuery(String sql) {
        return query(sql).get(0).values().iterator().next();
    }

}
