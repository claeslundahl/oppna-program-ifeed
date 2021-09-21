package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldInfUtil {

    protected final DatabaseApi database;

    private Tuple hiddenFieldsBranch;

    private List<Tuple> hiddenFields;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Tuple hiddenTemplate = gson.fromJson("{\n" +
            "      \"in_html_view\": false,\n" +
            "      \"level\": 2,\n" +
            "      \"apelon_id\": 113048,\n" +
            "      \"in_tooltip\": false,\n" +
            "      \"type\": \"d:text_fix\",\n" +
            "      \"leaf_search\": false,\n" +
            "      \"filter\": false,\n" +
            "      \"expanded\": false,\n" +
            "      \"parent_pk\": 437597468,\n" +
            "      \"name\": \"Kodverk (SweMeSH)\",\n" +
            "      \"id\": \"vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path\",\n" +
            "      \"position\": 135,\n" +
            "      \"query_prefix\": \"SweMeSH/\"\n" +
            "    }", Tuple.class);

    public FieldInfUtil(DatabaseApi database) {
        this.database = database;
    }

    public List<Tuple> getHiddenFields() {
        if (hiddenFields == null) {
            String sql = "select * from field_inf where parent_pk = ?";
            hiddenFields = database.query(sql, getHiddenFieldsBranch().get("pk"));
        }
        return hiddenFields;
    }

    public Tuple getHiddenFieldsBranch() {
        if (hiddenFieldsBranch == null) {
            String sql = "select * from field_inf root join field_inf branch on branch.parent_pk = root.pk where root.name = ? and branch.name = ?";
            List<Tuple> items = database.query(sql, "Gömda", "Fält");
            if (items.size() != 1) throw new RuntimeException();
            for (Tuple item : items)
                hiddenFieldsBranch = item;
        }
        return hiddenFieldsBranch;
    }

    public void insert(long pk, String id, String name, String queryPrefix) {
        Map<String, Object> tuple = new HashMap<>(hiddenTemplate);
        tuple.put("pk", pk);
        tuple.put("id", id);
        tuple.put("name", name);
        tuple.put("query_prefix", queryPrefix);
        database.insert("field_inf", tuple);
    }

    /*public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        FieldInfUtil fieldInfUtil = new FieldInfUtil(database);
    }*/

}
