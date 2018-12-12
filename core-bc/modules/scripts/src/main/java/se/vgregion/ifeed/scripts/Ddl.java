package se.vgregion.ifeed.scripts;

import se.vgregion.arbetsplatskoder.db.migration.AbstractJob;
import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Column;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Primary;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Schema;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ddl {

    public static void main(String[] args) {
        ConnectionExt con = CopyDatabaseUtil.getMainConnectionExt();
        System.out.println(con.getUrl());
        Schema pub = con.getSchemas("public").get(0);
        List<Table> tables = new ArrayList<>();
        for (Table table : pub.getTables()) {
            if (table.getTableName().contains("ifeed")) {
                tables.add(table);
            }
        }
        for (Table table : tables) {
            System.out.println(toDdl(table));
        }
    }


    /*
        CREATE TABLE public.vgr_apelon_metadata
        (
          id bigint NOT NULL,
          key character varying(255),
          name character varying(255),
          CONSTRAINT vgr_apelon_metadata_pkey PRIMARY KEY (id)
        )
     */
    static  String toDdl(Table table) {
        StringBuilder sb = new StringBuilder();
        Map<String, Primary> namedPrims = new HashMap<>();
        for (Primary primary : table.getPrimaries()) {
            namedPrims.put(primary.getColumnName(), primary);
        }
        sb.append("create table " + table.getTableName() + " ( \n");

        List<String> content = new ArrayList<>();
        for (Column column : table.getColumns()) {
            StringBuilder csb = new StringBuilder();
            csb.append(column.getColumnName());
            csb.append(" ");
            csb.append(column.getColumnTypeName());
            content.add(csb.toString());
        }
        if (!namedPrims.isEmpty()) {
            Primary prim = namedPrims.values().iterator().next();
            content.add("constraint " + prim.getPkName() + " primary key (" + prim.getColumnName() + ")");
        }
        sb.append(String.join(",\n", content));

        sb.append("\n)");
        return sb.toString();
    }

}
