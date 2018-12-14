package se.vgregion.ifeed.scripts;

import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class IfeedFilterUtil {

    public static void main(String[] args) throws SQLException {
        // ConnectionExt target = CreateAnalogNewTags.getStageConnectionExt();
        ConnectionExt target = CopyDatabaseUtil.getRemoteDxpTestConnectionExt();

        System.out.println(target.getUrl());

        // if (true) return;

        boolean missingPrim = (target.getSchemas("public").get(0).getTable("vgr_ifeed_filter").getColumn("id") == null);
        if (missingPrim)
            target.update("alter table vgr_ifeed_filter add column id bigint");

        String selectTableSQL = "select vif.*, vif.ctid from vgr_ifeed_filter vif order by vif.ifeed_id";
        List<Map<String, Object>> items = target.query(selectTableSQL, 0, 1_000_000);
        int cursor = 0;
        for (Map<String, Object> item : items) {
            Object ctid = item.get("ctid");
            target.update("update vgr_ifeed_filter set id = ? where ctid = ?", cursor++, ctid);
            if (cursor % 1000 == 0){
                System.out.println("Antal färdiga " + cursor);
                target.commit();
            }
        }

        if (missingPrim)
            target.update("alter table vgr_ifeed_filter add constraint vgr_ifeed_filter_pkey primary key (id)");

        target.commit();
    }

}
