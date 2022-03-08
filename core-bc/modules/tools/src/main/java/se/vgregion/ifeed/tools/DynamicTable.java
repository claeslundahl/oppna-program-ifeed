package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.List;

public class DynamicTable extends Tuple {

    private final List<DynamicColumnDef> columns = new ArrayList<>();

    public static List<? extends DynamicTable> toDynamicTables(List<Tuple> findings) {
        List<DynamicTable> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toDynamicTable(finding));
        }
        return result;
    }

    public static DynamicTable toDynamicTable(Tuple tuple) {
        DynamicTable result = new DynamicTable();
        result.putAll(tuple);
        return result;
    }

    public void fill(DatabaseApi database) {
        List<Tuple> findings = database.query("select * from vgr_ifeed_dynamic_column_def where fk_table_def_id = ?", get("id"));
        this.columns.addAll(DynamicColumnDef.toDynamicColumnDefs(findings));
    }

    public void delete(DatabaseApi fromHere) {
        for (DynamicColumnDef column : columns) {
            column.delete(fromHere);
        }
        fromHere.update("delete from vgr_ifeed_dynamic_table where id = ?", get("id"));
    }

    public List<DynamicColumnDef> getColumns() {
        return columns;
    }
}
