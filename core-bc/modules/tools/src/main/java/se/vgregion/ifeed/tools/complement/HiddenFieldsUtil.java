package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.tools.*;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenFieldsUtil extends FieldInfUtil {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Tuple hiddenTemplate = gson.fromJson("{\n" +
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
            "    }", Tuple.class);

    SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
    Set<String> allKeys;

    public Set<String> getAllKeys() {
        if (allKeys == null) {
            allKeys = client.fetchAllFieldNames();
        }
        return allKeys;
    }

    public void setAllKeys(Set<String> allKeys) {
        this.allKeys = allKeys;
    }

    public HiddenFieldsUtil(DatabaseApi database) {
        super(database);
    }

    public void copyNewHiddenFieldsIntoDatabase() {
        try {
            System.out.println("Start!");
            System.out.println("Using database at " + database.getUrl());
            runCopyNewHiddenFieldsIntoDatabase();
            System.out.println("End!");
        } catch (Exception e) {
            database.rollback();
            throw new RuntimeException(e);
        }
    }

    private void runCopyNewHiddenFieldsIntoDatabase() {

        addIfNotThereWithFeedback("vgrsy:DomainExtension.vgrsy:SubjectLocalClassification",
                null,
                "Egen ämnesindelning");

        addIfNotThereWithFeedback("vgr:VgrExtension.vgr:Tag",
                null,
                "Företagsnyckelord");

        addIfNotThereWithFeedback("vgr:VgrExtension.vgr:PublishedForUnit.id",
                null,
                "Upprättat för enhet");

        addIfNotThereWithFeedback("vgr:VgrExtension.vgr:CreatedByUnit.id",
                null,
                "Upprättat av enhet");

        addIfNotThereWithFeedback("core:ArchivalObject.core:Producer",
                null,
                "Myndighet");

        addIfNotThereWithFeedback("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path",
                "SweMeSH/",
                "Kodverk (SweMeSH)");

        addIfNotThereWithFeedback("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path",
                "Verksamhetskod/",
                "Kodverk (Verksamhetskod)");


        /*addIfNotThereWithFeedback("DIDL.Item.Descriptor.Statement.vgrsd:DomainExtension",
                null,
                "Domän Styrande dokument");
                Det här ersätts av nästa anrop istället - vet inte varför DIDL... står i dokumentet för rätt / använt
                innan är ju vgrsd:DomainExtension.domain.
                */

        addIfNotThereWithFeedback("vgrsd:DomainExtension.domain",
                null,
                "Domän Styrande dokument");
    }

    public void addIfNotThereWithFeedback(String id, String queryPrefix, String name) {
        if (addIfNotThere(id, queryPrefix, name)) {
            System.out.println("Added " + Arrays.asList(id, queryPrefix, name));
        } else {
            System.out.println("Ignored " + Arrays.asList(id, queryPrefix, name));
        }
    }

    public boolean addIfNotThere(String id, String queryPrefix, String name) {
        if (!getAllKeys().contains(id)) {
            if (getAllKeys().stream().map(ak -> ak.toLowerCase(Locale.ROOT)).collect(Collectors.toSet()).contains(id.toLowerCase(Locale.ROOT))) {
                System.out.println("Nyckel fanns som lover case!");
            }
            /*Map<String, Set<Object>> allValues = client.findAllValues();
            for (String key : allValues.keySet()) {
                System.out.println(key + " = " + allValues.get(key));
            }*/
            System.out.println(String.join("\n", getAllKeys()));
            throw new RuntimeException(String.format("Key '%s' is not in index.", id));
        }
        Map<String, Object> match = new HashMap<>();
        match.put("id", id);
        match.put("query_prefix", queryPrefix);
        Long parentPk = (Long) getHiddenFieldsBranch().get("pk");
        match.put("parent_pk", parentPk);
        List<Tuple> items = database.query("field_inf", match);
        if (items.isEmpty()) {
            Map<String, Object> item = new HashMap<>(hiddenTemplate);
            item.put("name", name);
            item.put("pk", SequenceUtil.getNextHibernateSequeceValue(database) * -1);
            item.put("parent_pk", parentPk);
            item.put("id", id);
            item.put("query_prefix", queryPrefix);
            database.insert("field_inf", item);
            return true;
        }
        return false;
    }

    void fixHiddenFieldsConnection() {
        String sql = "select vif1.* from vgr_ifeed_filter vif1\n" +
                "join vgr_ifeed_filter vif2 on vif2.parent_id = vif1.id\n" +
                "join vgr_ifeed_filter vif3 on vif3.parent_id = vif2.id\n" +
                "left outer join field_inf fi on fi.pk = vif3.field_inf_pk\n" +
                "where vif1.ifeed_id < 0 \n" +
                "and vif3.field_inf_pk is null or vif3.filterkey != fi.id\n" +
                "union all\n" +
                "select vif1.* from vgr_ifeed_filter vif1\n" +
                "join vgr_ifeed_filter vif2 on vif2.parent_id = vif1.id\n" +
                "left outer join field_inf fi on fi.pk = vif2.field_inf_pk\n" +
                "where vif1.ifeed_id < 0 \n" +
                "and vif2.field_inf_pk is null or vif2.filterkey != fi.id\n" +
                "union all\n" +
                "select vif1.* from vgr_ifeed_filter vif1\n" +
                "left outer join field_inf fi on fi.pk = vif1.field_inf_pk\n" +
                "where vif1.ifeed_id < 0 \n" +
                "and vif1.field_inf_pk is null or vif1.filterkey != fi.id\n" +
                "and vif1.operator not in ('or', 'and')";
        for (Tuple tuple : database.query(sql)) {
            fixHiddenFieldsConnection(Filter.toFilter(tuple));
        }
    }

    public void fixHiddenFieldsConnection(Feed feed) {
        if (feed.getFilters() == null) {
            feed.fill(database);
        }
        for (Filter filter : feed.getFilters()) {
            filter.visit(f -> {
                String key = (String) f.get("filterkey");
                if (key != null && !"".equals(key.trim())) {
                    fixHiddenFieldsConnection(f);
                }
            });
        }
    }

    void fixHiddenFieldsConnection(Filter item) {
        System.out.println(item);
        Map<String, Tuple> map = new HashMap<>();
        getHiddenFields().stream().forEach(f -> map.put((String) f.get("id"), f));
        Object newFieldInfPk = map.get(item.get("filterkey")).get("pk");
        int result = database.update(
                "update vgr_ifeed_filter set field_inf_pk = ? where id = ?",
                newFieldInfPk, item.get("id")
        );
        System.out.println(result);
        if (result != 1) {
            throw new RuntimeException();
        }
    }

    private List<Tuple> hiddenFields;

    public List<Tuple> getHiddenFields() {
        if (hiddenFields == null) {
            String hiddenSql = "select leaf.*\n" +
                    "from field_inf trunk\n" +
                    "join field_inf branch on trunk.pk = branch.parent_pk \n" +
                    "join field_inf leaf on branch.pk = leaf.parent_pk\n" +
                    "where trunk.name = 'Gömda'";
            this.hiddenFields = database.query(hiddenSql);
        }
        return hiddenFields;
    }

}
