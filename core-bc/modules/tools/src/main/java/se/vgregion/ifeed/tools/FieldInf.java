package se.vgregion.ifeed.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class FieldInf extends Tuple {

    private List<FieldInf> children;

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
        /*if (!template.containsKey(key))
            throw new RuntimeException(String.format("Key %s is not among those in template-store - %s", key, template.keySet().toString()) );*/
        return super.put(key, value);
    }

    public void load(DatabaseApi database) {
        setDatabase(database);
    }

    public List<FieldInf> getChildren() {
        if (children == null) {
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
}
