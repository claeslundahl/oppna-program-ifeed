package se.vgregion.ifeed.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FieldInf extends Tuple {

    private List<FieldInf> children;

    private List<DefaultFilter> defaultFilters;

    private FieldInf parent;

    private DatabaseApi database;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

/*    private static final FieldInf template = gson.fromJson("{\n" +
            "      \"in_html_view\": false,\n" +
            "      \"level\": 2,\n" +
            "      \"apelon_id\": 113048,\n" +
            "      \"in_tooltip\": false,\n" +
            "      \"type\": \"d:text_fix\",\n" +
            // "      \"leaf_search\": false,\n" +
            "      \"filter\": false,\n" +
            "      \"expanded\": false,\n" +
            "      \"parent_pk\": 437597468,\n" +
            "      \"name\": \"Kodverk (SweMeSH)\",\n" +
            "      \"id\": \"vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path\",\n" +
            "      \"position\": 135,\n" +
            "      \"query_prefix\": \"SweMeSH/\"\n" +
            "    }", FieldInf.class);*/

    public FieldInf() {
        super();
    }

    public FieldInf(String id, String name, String queryPrefix) {
        put("id", id);
        put("name", name);
        put("query_prefix", queryPrefix);
    }

    public FieldInf(FieldInf fieldInf) {
        putAll(fieldInf);
    }

    public static FieldInf toFieldInf(Tuple tuple) {
        FieldInf result = new FieldInf();
        result.putAll(tuple);
        return result;
    }

    @Override
    public Object get(Object key) {
        // if (!template.containsKey(key)) throw new RuntimeException();
        return super.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    public void load(DatabaseApi database) {
        setDatabase(database);
    }

    public List<FieldInf> getChildren() {
        if (children == null && database != null) {
            List<Tuple> items = database.query("select * from field_inf where parent_pk = ?", get("pk"));
            children = new ArrayList<>();
            for (Tuple item : items) {
                FieldInf child = toFieldInf(item);
                children.add(child);
                child.load(database);
            }
        }
        return children;
    }

    public void setChildren(List<FieldInf> children) {
        this.children = children;
    }

    public List<DefaultFilter> getDefaultFilters() {
        if (defaultFilters == null && database != null) {
            List<Tuple> items = database.query("select * from default_filter where field_inf_pk = ?", get("pk"));
            defaultFilters = new ArrayList<>(items.stream().map(i -> new DefaultFilter(i)).collect(Collectors.toList()));
        }
        return defaultFilters;
    }

    public void setDefaultFilters(List<DefaultFilter> defaultFilters) {
        this.defaultFilters = defaultFilters;
    }

    public FieldInf getParent() {
        if (parent == null) {
            List<Tuple> items = database.query("select * from field_inf where pk = ?", get("parent_pk"));
            if (items.size() == 1) {
                parent = toFieldInf(items.get(0));
                parent.load(database);
            }
        }
        return parent;
    }

    public void setParent(FieldInf parent) {
        this.parent = parent;
    }

    public DatabaseApi getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseApi database) {
        this.database = database;
    }

    public se.vgregion.ifeed.types.FieldInf toJpaVersion() {
        se.vgregion.ifeed.types.FieldInf result = new se.vgregion.ifeed.types.FieldInf();
// [in_html_view, level, apelon_id, in_tooltip, type, operator, filter, help, expanded, apelon_key,
        // parent_pk, name, pk, id, position, query_prefix, value]
        if (get("in_html_view") != null)
            result.setInHtmlView((Boolean) get("in_html_view"));
        result.setLevel((Integer) get(("level")));
        result.setApelonKey((String) get("apelon_key"));
        if (get("in_tooltip") != null)
            result.setInTooltip((Boolean) get("in_tooltip"));
        result.setType((String) get("type"));
        result.setOperator((String) get("operator"));
        if (get("filter") != null)
           result.setFilter((Boolean) get("filter"));
        result.setHelp((String) get("help"));
        if (get("expanded") != null)
        result.setExpanded((Boolean) get("expanded"));
        result.setParentPk((Long) get("parent_pk"));
        if (getParent() != null)
            result.setParent(getParent().toJpaVersion());
        result.setName((String) get("name"));
        result.setPk((Long) get("pk"));
        result.setId((String) get("id"));
        result.setPosition((Integer) get("position"));
        result.setQueryPrefix((String) get("query_prefix"));
        result.setValue((String) get("value"));
        result.setDefaultFilters(new HashSet<>());
        for (DefaultFilter df : getDefaultFilters()) {
            result.getDefaultFilters().add(df.toJpaVersion());
        }
        return result;
    }
}
