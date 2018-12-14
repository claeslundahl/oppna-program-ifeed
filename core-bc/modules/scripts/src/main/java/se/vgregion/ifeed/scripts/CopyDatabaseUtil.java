package se.vgregion.ifeed.scripts;

import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Schema;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CopyDatabaseUtil {

    public static ConnectionExt getMainConnectionExt() {
        Properties prop = getMainJdbcProperties();
        return toConnectionExt(prop);
    }

    public static ConnectionExt getRemoteProdConnectionExt() {
        Properties prop = getProdJdbcProperties();
        return toConnectionExt(prop);
    }

    public static ConnectionExt getRemoteDxpTestConnectionExt() {
        Properties prop = getDxpTestJdbcProperties();
        return toConnectionExt(prop);
    }

    public static ConnectionExt getBackupConnectionExt() {
        Properties prop = getBackupJdbcProperties();
        return toConnectionExt(prop);
    }

    public static ConnectionExt getTestConnectionExt() {
        Properties prop = getTestJdbcProperties();
        return toConnectionExt(prop);
    }

    static ConnectionExt toConnectionExt(Properties prop) {
        ConnectionExt connection = new ConnectionExt(
                prop.getProperty("datasource.connector.direct.url"),
                prop.getProperty("datasource.connector.direct.username"),
                prop.getProperty("datasource.connector.direct.password"),
                prop.getProperty("datasource.connector.direct.driverClassName"));
        return connection;
    }

    public static Properties getMainJdbcProperties() {
        Path path = (Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"));
        return getProperties(path);
    }

    public static Properties getTestJdbcProperties() {
        Path path = (Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config-test.properties"));
        return getProperties(path);
    }

    public static Properties getBackupJdbcProperties() {
        Path path = (Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config-backup.properties"));
        return getProperties(path);
    }

    private static Properties getProdJdbcProperties() {
        Path path = (Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config-prod.properties"));
        return getProperties(path);
    }

    private static Properties getDxpTestJdbcProperties() {
        Path path = (Paths.get(System.getProperty("user.home"), ".hotell", "dxp.remote.test.jdbc.properties"));
        return getProperties(path);
    }

    static Properties getProperties(Path fromHere) {
        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(fromHere)) {
            properties.load(reader);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return properties;
    }

    static ConnectionExt target = getBackupConnectionExt();
    static ConnectionExt prod = getRemoteProdConnectionExt();
    static Schema prodSchema = prod.getSchemas("public").get(0);

    public static void main(String[] args) {

        System.out.println("Target: " + target.getUrl());

        System.out.println("Source: " + prod.getUrl());

        // if (true) return;

        System.out.println("Tables for ifeed:");
        for (Table table : prod.getSchemas("public").get(0).getTables()) {
            if (table.getTableName().contains("vgr_ifeed")) {
                System.out.println(table.getTableName());
                System.out.println(table.getSql());
                System.out.println();
            }
        }


        copy("vgr_ifeed_department");
        copy("vgr_ifeed_group");
        copy("vgr_ifeed");

        copy("vgr_ifeed_dynamic_table");
        copy("vgr_ifeed_dynamic_column_def");
        copy("vgr_ifeed_dynamic_table_sorting");

        copy("vgr_ifeed_filter");
        copy("vgr_ifeed_ownership");
        copy("vgr_ifeed_vgr_ifeed");

        copy("vgr_ifeed_fields_inf");

        target.commit();
    }

    public static void copy(String tableName) {
        if (prodSchema.getTable(tableName) == null) {
            System.out.println("Skipping " + tableName + " does not exist in prod.");
            return;
        }
        List<Map<String, Object>> items = prod.query("select distinct * from " + tableName, 0, 1_000_000);
        System.out.println(
                "Inserts " + items.size() + " items from "
                        + tableName + " where current count is "
                        + target.query("select count(*) from " + tableName, 0, 1)
        );

        for (Map<String, Object> item : items) {
            target.insert(tableName, item);
        }
    }

}
