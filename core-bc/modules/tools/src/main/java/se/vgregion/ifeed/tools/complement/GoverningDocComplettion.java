package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.mutable.MutableInt;
import se.vgregion.common.utils.DistinctArrayList;
import se.vgregion.common.utils.MultiMap;
import se.vgregion.ifeed.tools.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class GoverningDocComplettion {

    private final String stuffWithManyFiltersSql = "select ifeed_id, count(distinct filterkey) from vgr_ifeed_filter where \n" +
            "ifeed_id > 0 and\n" +
            "filterkey in (\n" +
            "'dc.type.document.structure', 'dc.publisher.project-assignment', 'dc.subject.authorkeywords', 'dc.creator.project-assignment', 'dc.type.file',\n" +
            "            'dc.type.document', 'dc.type.file.process', 'dc.type.record', 'dc.type.document.serie', 'dc.type.process.name', 'dc.type.document.id',\n" +
            "            'dc.publisher.forunit.id', 'dc.creator.forunit.id', 'dc.creator.recordscreator.id', 'dc.source.origin'\n" +
            ") group by ifeed_id order by 2 desc";

    private final DatabaseApi database;

    private List<String> oldFilters = Arrays.asList("dc.type.document.structure", "dc.publisher.project-assignment", "dc.subject.authorkeywords", "dc.creator.project-assignment", "dc.type.file",
            "dc.type.document", "dc.type.file.process", "dc.type.record", "dc.type.document.serie", "dc.type.process.name", "dc.type.document.id",
            "dc.publisher.forunit.id", "dc.creator.forunit.id", "dc.creator.recordscreator.id", "dc.source.origin");

    private List<String> mapsToLocalClassification = Arrays.asList("dc.type.document.structure", "dc.publisher.project-assignment",
            "dc.subject.authorkeywords", "dc.creator.project-assignment", "dc.type.file", "dc.type.document",
            "dc.type.file.process", "dc.type.record", "dc.type.document.serie", "dc.type.process.name", "dc.type.document.id");

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private HiddenFieldsUtil hiddenFieldsUtil;

    private Map<String, FieldInf> codeMapping = Map.of("dc.subject.keywords", new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path",
            "Kodverk (Verksamhetskod)", "Verksamhetskod/"),
            "dc.coverage.hsacode", new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path",
                    "Kodverk (Verksamhetskod)", "Verksamhetskod/"));

    public GoverningDocComplettion(DatabaseApi database) {
        this.database = database;
        hiddenFieldsUtil = new HiddenFieldsUtil(database);
    }

    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println(database.getUrl());
        // if(true) return;
        try {
            GoverningDocComplettion gdc = new GoverningDocComplettion(database);
            // print(gdc.one(437595301));
            gdc.main();
            database.rollback();
        } catch (Exception e) {
            database.rollback();
            throw new RuntimeException(e);
        }
    }

    void runWithTopFiltersFeeds(int maxRuns) {
        List<Tuple> items = database.query(stuffWithManyFiltersSql);
        int count = 0;
        for (Tuple item : items) {
            if (count++ > maxRuns) return;
            print(one((Number) item.get("ifeed_id")));
            System.out.println();
        }
    }

    public static void print(Feed feed) {
        /*for (String key : feed.keySet()) {
            System.out.println(key + " = " + feed.get(key));
        }*/
        System.out.println(feed.get("id") + " " + feed.get("name") + " ");
        MutableInt counter = new MutableInt(0);
        for (Filter filter : feed.getFilters())
            filter.visit(f -> counter.setValue(counter.intValue() + 1));
        System.out.println("Filters " + counter.intValue());
        for (Filter filter : feed.getFilters()) {
            print(filter, 1);
        }
    }

    static void print(Filter filter, int level) {
        /*for (String key : filter.keySet()) {
            System.out.println(" ".repeat(level) + key + " = " + filter.get(key));
        }*/
        System.out.println(" ".repeat(level) + filter.get("filterkey") + " " + filter.get("operator") + " " + filter.get("filterquery"));
        if (!filter.getChildren().isEmpty()) {
            // System.out.println(" ".repeat(level) + String.format("%d st. [", filter.getChildren().size()));
            for (Filter child : filter.getChildren()) {
                // System.out.println(" ".repeat(level + 1) + "{");
                print(child, level + 2);
                // System.out.println(" ".repeat(level + 1) + "}");
            }
            // System.out.println(" ".repeat(level) + "]");
        }
    }

    Feed getFeed(Number withId) {
        for (Tuple tuple : database.query("select * from vgr_ifeed where id = ?", withId)) {
            Feed result = Feed.toFeed(tuple);
            result.fill(database);
            return result;
        }
        return null;
    }

    public void main() {
        String oldFiltersList = oldFilters.stream().collect(Collectors.joining("', '"));
        String sql = "select distinct ifeed_id from vgr_ifeed_filter " +
                "where ifeed_id is not null and filterkey in ('%s') and ifeed_id > 0" +
                "order by ifeed_id";
        sql = String.format(sql, oldFiltersList);
        List<Tuple> items = database.query(sql);
        List<Number> feedsWithBariumDocs = new ArrayList<>();
        for (Tuple item : items) {
            System.out.println(item);
            if (hasBariumDocs(item.get("ifeed_id"))) {
                feedsWithBariumDocs.add((Number) item.get("ifeed_id"));
                break;
            }
        }
        System.out.println(items.size());
        System.out.println("Antal barium " + feedsWithBariumDocs.size());

        for (Number feedsWithBariumDoc : feedsWithBariumDocs) {
            one(feedsWithBariumDoc);
        }
    }

    Feed one(Number forThatFeed) {
        Feed feed = getFeed(forThatFeed);
        MultiMap<String, Filter> filters = new MultiMap<>(() -> new DistinctArrayList<Filter>());
        for (Filter filter : new ArrayList<Filter>(feed.getFilters()))
            if (!oldFilters.contains(filter.get("filterkey")))
                feed.getFilters().remove(filter);

        for (Filter filter : feed.getFilters()) {
            filters.get(filter.get("filterquery")).add(filter);
        }
        Long newId = (Long) feed.get("id");
        newId = newId * -1;
        Feed newFeed = new Feed(feed);
        newFeed.put("name", newFeed.get("name") + "(komplettering för styrande dokument)");
        newFeed.put("id", newId);
        newFeed.put("userid", "lifra1");

        filters = putMultiFiltersInOneParent(filters, "or");
        filters = putLocalCategoryInOneAndParent(filters);

        filters = changeKeys(filters,
                Map.of("dc.publisher.forunit.id", "vgr:VgrExtension.vgr:PublishedForUnit.id",
                        "dc.creator.forunit.id", "vgr:VgrExtension.vgr:CreatedByUnit.id",
                        "dc.creator.recordscreator.id", "core:ArchivalObject.core:Producer"/*,
                        "dc.source.origin", "DIDL.Item.Descriptor.Statement.vgrsd:DomainExtension"*/)
        );

        putNewIds(filters.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()), null);

        newFeed.getFilters().addAll(filters.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        for (Filter filter : newFeed.getFilters())
            filter.visit(f -> f.remove("ifeed_id"));

        newFeed.getFilters().add(newGoverningDocsCondition());

        for (Filter filter : newFeed.getFilters())
            filter.put("ifeed_id", newId);

        newFeed.insert(database);

        CompositeLink cl = new CompositeLink(newId * -1, newId);
        cl.insert(database);

        return newFeed;
    }

    private void addBindingWithOriginal(Feed complement) {
        Long id = (Long) complement.get("id");
        id = id * -1;
    }

    Tuple hiddenFieldsBranch;

    List<Tuple> hiddenFields;

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

    Tuple getOrCreateHiddenFieldInf(String forId) {
        Map<String, Object> map = new HashMap<>();
        map.put("parent_pk", getHiddenFieldsBranch().get("pk"));
        map.put("id", forId);
        if (orCreatedStuff.containsKey(map)) {
            return orCreatedStuff.get(map);
        }
        Tuple result = getOrCreate(map, "field_inf");
        return result;
    }

    private final Map<Map, Tuple> orCreatedStuff = new HashMap<>();

    Tuple getOrCreate(Map<String, Object> that, String fromTable) {
        if (orCreatedStuff.containsKey(that))
            return orCreatedStuff.get(that);
        Tuple result = getOrCreateImpl(that, fromTable);
        orCreatedStuff.put(that, result);
        return result;
    }

    Tuple getOrCreateImpl(Map<String, Object> that, String fromTable) {
        List<Tuple> items = database.query(fromTable, that);
        if (items.size() == 1) {
            return items.get(0);
        }
        that = new HashMap<>(that);
        for (Tuple prim : database.getPrims(fromTable)) {
            that.put(prim.get("column_name").toString(), SequenceUtil.getNextHibernateSequeceValue(database));
        }
        database.insert(fromTable, that);
        items = database.query(fromTable, that);
        if (items.size() == 1) {
            return items.get(0);
        } else {
            throw new RuntimeException();
        }
    }

    Filter newGoverningDocsCondition() {
        Filter filter = new Filter();
        filter.put("filterkey", "vgrsd:DomainExtension.domain");
        filter.put("filterquery", "Styrande dokument");
        filter.put("operator", "matching");
        Tuple type = getOrCreate(Map.of("id", "vgrsd:DomainExtension.domain", "parent_pk", getHiddenFieldsBranch().get("pk"), "name", "Dokumenttyp"), "field_inf");
        filter.put("field_inf_pk", type.get("pk"));
        return filter;
    }

    void putNewIds(Collection<Filter> in, Long parentId) {
        for (Filter filter : in) {
            Long ownId = SequenceUtil.getNextHibernateSequeceValue(database);
            filter.put("id", ownId);
            filter.put("parent_id", parentId);
            putNewIds(filter.getChildren(), ownId);
        }
    }

    MultiMap<String, Filter> changeKeys(final MultiMap<String, Filter> here, final Map<String, String> byTheseTranslations) {
        MultiMap<String, Filter> result = new MultiMap<>(() -> new DistinctArrayList<Filter>());
        for (String key : here.keySet()) {
            if (byTheseTranslations.containsKey(key)) {
                List<Filter> values = here.get(key);
                String translation = byTheseTranslations.get(key);
                for (Filter value : values)
                    value.visit(filter -> filter.put("filterkey", translation));
                result.put(translation, values);
            } else {
                result.put(key, here.get(key));
            }
        }
        for (String key : result.keySet()) {
            for (Filter filter : result.get(key)) {
                filter.visit(f -> {
                    if (byTheseTranslations.containsKey(f.get("filterkey"))) {
                        f.put("filterkey", byTheseTranslations.get(f.get("filterkey")));
                    }
                });
            }
        }
        return result;
    }

    MultiMap<String, Filter> putLocalCategoryInOneAndParent(MultiMap<String, Filter> mm) {
        MultiMap<String, Filter> result = new MultiMap<>(() -> new DistinctArrayList<Filter>());
        final String categoryKey = "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification";
        for (String key : mm.keySet()) {
            List<Filter> list = mm.get(key);
            if (list.size() != 1) throw new RuntimeException();
            Filter first = list.get(0);
            if (mapsToLocalClassification.contains(first.get("filterkey"))) {
                result.get(categoryKey).add(first);
            } else {
                result.get(key).add(list.get(0));
            }
        }
        result = putMultiFiltersInOneParent(result, "and");
        for (Filter filter : result.get(categoryKey)) {
            filter.visit(that -> that.put("filterkey", categoryKey));
        }
        return result;
    }

    MultiMap<String, Filter> putMultiFiltersInOneParent(MultiMap<String, Filter> mm, /*Long ifeedId,*/ String operator) {
        MultiMap<String, Filter> result = new MultiMap<>(() -> new DistinctArrayList<Filter>());
        for (String key : mm.keySet()) {
            List<Filter> list = mm.get(key);
            if (list.size() == 1) {
                result.get(key).add(list.get(0));
            } else {
                result.get(key).add(toContainer(list, operator/*, ifeedId*/));
            }
        }
        return result;
    }

    Filter toContainer(List<Filter> moreThanOne, String operator/*, Long feedId*/) {
        Filter result = new Filter();
        Filter first = moreThanOne.get(0);
        result.putAll(first);
        result.put("operator", operator);
        //Long resultId = SequenceUtil.getNextHibernateSequeceValue(database);
        // result.put("id", resultId);
        // result.put("ifeed_id", feedId);
        result.getChildren().addAll(moreThanOne);
        /*for (Filter filter : moreThanOne) {
            filter.put("parent_id", resultId);
        }*/
        return result;
    }

    static boolean hasBariumDocs(Object id) {
        try {
            return hasBariumDocsImpl(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static HttpClient client = HttpClient.newHttpClient();

    static boolean hasBariumDocsImpl(Object id) throws IOException, InterruptedException {

        final String serviceUrl = String.format("http://localhost:7070/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=SourceSystem", id.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        return response.contains("Barium");
    }

}
