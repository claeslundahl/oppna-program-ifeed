package se.vgregion.ifeed.tools;

import se.vgregion.arbetsplatskoder.db.migration.sql.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static se.vgregion.common.utils.Props.fetchProperties;

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

    public List<Tuple> query(String table, Map<String, Object> matching) {
        if (matching == null || matching.isEmpty()) {
            return query("select * from " + table);
        }
        String sql = "select * from " + table + " where ";
        List<Object> values = new ArrayList<>();
        List<String> where = new ArrayList<>();
        for (String key : matching.keySet()) {
            Object v = matching.get(key);
            if (v != null) {
                where.add(key + " = ?");
                values.add(matching.get(key));
            } else {
                where.add(key + " is null");
            }
        }
        sql = sql + String.join(" and ", where);
        return query(sql, values.toArray());
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

    public static DatabaseApi getDatabaseApi() {
        Properties props = fetchProperties();

        DatabaseApi result = new DatabaseApi(
                props.getProperty("datasource.connector.direct.url"),
                props.getProperty("datasource.connector.direct.username"),
                props.getProperty("datasource.connector.direct.password"),
                props.getProperty("datasource.connector.direct.driverClassName")
        );

        return result;
    }

    public static DatabaseApi getRemoteTestDatabaseApi() {
        Properties props = null;
        try {
            props = fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.test.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DatabaseApi result = new DatabaseApi(
                props.getProperty("datasource.connector.direct.url"),
                props.getProperty("datasource.connector.direct.username"),
                props.getProperty("datasource.connector.direct.password"),
                props.getProperty("datasource.connector.direct.driverClassName")
        );

        return result;
    }

    public static DatabaseApi getRemoteStageDatabaseApi() {
        Properties props = null;
        try {
            props = fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.stage.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DatabaseApi result = new DatabaseApi(
                props.getProperty("datasource.connector.direct.url"),
                props.getProperty("datasource.connector.direct.username"),
                props.getProperty("datasource.connector.direct.password"),
                props.getProperty("datasource.connector.direct.driverClassName")
        );

        return result;
    }

    public static DatabaseApi getLocalBackupApi() {
        Properties props = null;
        try {
            props = fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.local.backup.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DatabaseApi result = new DatabaseApi(
                props.getProperty("datasource.connector.direct.url"),
                props.getProperty("datasource.connector.direct.username"),
                props.getProperty("datasource.connector.direct.password"),
                props.getProperty("datasource.connector.direct.driverClassName")
        );
        return result;
    }

    public static DatabaseApi getLocalApi() {
        Properties props = null;
        try {
            props = fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DatabaseApi result = new DatabaseApi(
                props.getProperty("datasource.connector.direct.url"),
                props.getProperty("datasource.connector.direct.username"),
                props.getProperty("datasource.connector.direct.password"),
                props.getProperty("datasource.connector.direct.driverClassName")
        );
        return result;
    }

    public static DatabaseApi getRemoteProdDatabaseApi() {
        Properties props = null;
        try {
            props = fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.prod.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DatabaseApi result = new DatabaseApi(
                props.getProperty("datasource.connector.direct.url"),
                props.getProperty("datasource.connector.direct.username"),
                props.getProperty("datasource.connector.direct.password"),
                props.getProperty("datasource.connector.direct.driverClassName")
        );

        return result;
    }

    public List<Tuple> getPrims(String forTable) {
        try {
            DatabaseMetaData meta = getConnection().getMetaData();
            try (ResultSet rs = meta.getPrimaryKeys(null, null, forTable)) {
                return toTuples(JdbcUtil.toMaps(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
