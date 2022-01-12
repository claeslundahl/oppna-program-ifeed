package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.commons.lang.mutable.MutableInt;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.tools.*;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenFieldsUtil extends FieldInfUtil {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public final Tuple hiddenTemplate = gson.fromJson("{\n" +
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
                "Verksamhetskod");

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
        String sql = "select * from vgr_ifeed where id < 0";
        System.out.println(sql);
        List<Tuple> items = database.query(sql);

        final List<Feed> toFix = new ArrayList<>();
        for (Tuple item : items) {
            Feed feed = Feed.toFeed(item);
            feed.fill(database);
            MutableBoolean needsFix = new MutableBoolean(false);
            for (Filter filter : feed.getFilters()) {
                filter.visit(f -> {
                    String fk = (String) f.get("filterkey");
                    if (fk != null && !"".equals(fk.trim())) {
                        FieldInf fi = f.getFieldInf();
                        fi.load(database);
                        if (fi == null || !fi.get("id").equals(f.get("filterkey"))) {
                            needsFix.setValue(true);
                        }
                    }
                });
                if (needsFix.booleanValue()) {
                    toFix.add(feed);
                }
            }
        }

        System.out.println("Hittade " + toFix.size() + " saker att fixa.");
        int i = 0;
        for (Feed feed : toFix) {
            i++;
            //System.out.println(i + " " + feed);
            System.out.println(i + " Ändrade " + fixHiddenFieldsConnection(feed) + " antal i " + feed);
        }


        /*int i = 0;
        for (Tuple tuple : items) {
            // System.out.println(tuple.get("ifeed_id"));
            Filter filter = Filter.toFilter(tuple);
            filter.fill(database);
            filter.visit(f -> {
                String filterKey = (String) f.get("filterkey");
                if (filterKey != null && !"".equals(filterKey.trim()))
                    fixHiddenFieldsConnection(f);
            });
            i++;
            if (i % 5 == 0) System.out.print(String.format("%1$4s", i) + " ");
            if (i % 20 == 0) System.out.println();
        }*/
    }

    public int fixHiddenFieldsConnection(Feed feed) {
        MutableInt result = new MutableInt(0);
        if (feed.getFilters() == null) {
            feed.fill(database);
        }
        for (Filter filter : feed.getFilters()) {
            filter.visit(f -> {
                String key = (String) f.get("filterkey");
                if (key != null && !"".equals(key.trim())) {
                    result.add(fixHiddenFieldsConnection(f));
                }
            });
        }
        return result.intValue();
    }

    int fixHiddenFieldsConnection(Filter item) {
        Map<String, Tuple> map = new HashMap<>();
        getSofiaFields().stream().forEach(f -> map.put((String) f.get("id"), f));
        if (map.get(item.get("filterkey")) == null) {
            throw new RuntimeException("Does not have substitute for key " + item.get("filterkey"));
        }
        Object newFieldInfPk = map.get(item.get("filterkey")).get("pk");
        int result = database.update(
                "update vgr_ifeed_filter set field_inf_pk = ? where id = ?",
                newFieldInfPk, item.get("id")
        );
        if (result != 1) {
            throw new RuntimeException();
        }
        return result;
    }

    private List<Tuple> hiddenFields;

    private List<Tuple> sofiaFields;

    public List<Tuple> getSofiaFields() {
        if (sofiaFields == null) {
            String hiddenSql = "select leaf.*\n" +
                    "from field_inf trunk\n" +
                    "join field_inf branch on trunk.pk = branch.parent_pk \n" +
                    "join field_inf leaf on branch.pk = leaf.parent_pk\n" +
                    "where trunk.name = 'SOFIA samarbetsyta'";
            this.sofiaFields = database.query(hiddenSql);
        }
        return sofiaFields;
    }

    @Deprecated
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
