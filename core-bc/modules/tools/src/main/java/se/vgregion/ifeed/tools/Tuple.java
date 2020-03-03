package se.vgregion.ifeed.tools;

import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;
import se.vgregion.common.utils.Json;

import java.util.HashMap;

public class Tuple extends HashMap<String, Object> {

    private Table table;

    public Tuple(Table table) {
        super();
        this.table = table;
    }

    public Tuple() {
        super();
    }

}
