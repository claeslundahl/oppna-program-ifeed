package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.List;

public class DynamicColumnDef extends Tuple {

    public static List<? extends DynamicColumnDef> toDynamicColumnDefs(List<Tuple> findings) {
        List<DynamicColumnDef> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toDynamicColumnDef(finding));
        }
        return result;
    }

    public static DynamicColumnDef toDynamicColumnDef(Tuple tuple) {
        DynamicColumnDef result = new DynamicColumnDef();
        result.putAll(tuple);
        return result;
    }

    public void delete(DatabaseApi fromHere) {
        fromHere.update("delete from vgr_ifeed_dynamic_column_def where id = ?", get("id"));
    }

}
