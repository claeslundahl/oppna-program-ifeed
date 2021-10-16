package se.vgregion.ifeed.tools;

import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;

import java.util.HashMap;
import java.util.Map;

public class Tuple extends HashMap<String, Object> implements Comparable {

    private Table table;

    public Tuple(Map<String, Object> from) {
        super(from);
    }

    public Tuple(Table table) {
        super();
        this.table = table;
    }

    public Tuple() {
        super();
    }

    @Override
    public int compareTo(Object o) {
        return hashCode() - o.hashCode();
    }
}
