package se.vgregion.ifeed.tools;

import org.apache.commons.lang.mutable.MutableInt;
import se.vgregion.common.utils.DistinctArrayList;
import se.vgregion.ifeed.types.IFeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Feed extends Tuple {

    public Feed() {
        super();
    }

    private final List<Filter> filters = new ArrayList<>();

    private final List<CompositeLink> composites = new DistinctArrayList<>();

    private final List<CompositeLink> partOf = new DistinctArrayList<>();

    public Feed(Feed feed) {
        super();
        putAll(feed);
    }

    public static List<Feed> toFeeds(List<Tuple> query) {
        List<Feed> result = new ArrayList<>();
        for (Tuple tuple : query) {
            result.add(toFeed(tuple));
        }
        return result;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public static Feed toFeed(Map<String, Object> fromThat) {
        Feed result = new Feed();
        result.putAll(fromThat);
        return result;
    }

    public void fill(DatabaseApi database) {
        List<Tuple> findings = database.query("select * from vgr_ifeed_filter where ifeed_id = ?", get("id"));
        filters.addAll(Filter.toFilters(findings));
        for (Filter filter : filters) {
            filter.fill(database);
        }
        findings = database.query("select * from vgr_ifeed_vgr_ifeed where partof_id = ?", get("id"));
        composites.addAll(CompositeLink.toCompositeLinks(findings));

        findings = database.query("select * from vgr_ifeed_vgr_ifeed where composites_id = ?", get("id"));
        partOf.addAll(CompositeLink.toCompositeLinks(findings));
    }

    public void insert(DatabaseApi into) {
        into.insert("vgr_ifeed", this);
        for (Filter filter : filters) {
            filter.insert(into, this, null);
        }
        for (CompositeLink composite : composites) {
            composite.insert(into);
        }
    }

    public void delete(DatabaseApi fromHere) {
        for (Filter filter : filters) {
            filter.delete(fromHere);
        }
        for (CompositeLink composite : composites) {
            composite.delete(fromHere);
        }
        for (CompositeLink part : partOf) {
            part.delete(fromHere);
        }
        fromHere.update("delete from vgr_ifeed where id = ?", get("id"));
    }

    private boolean toStringRuns = false;

    @Override
    public String toString() {
        try {
            if (toStringRuns) return "[rec-ref]";
            toStringRuns = true;
            Map result = new HashMap(this);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Filter filter : filters) list.add(filter.toMap());
            result.put("filters", list);
            return result.toString();
        } finally {
            toStringRuns = false;
        }
        //return Json.toJson(result);
    }

    public List<CompositeLink> getComposites() {
        return composites;
    }

    public List<CompositeLink> getPartOf() {
        return partOf;
    }

    public void visit(Consumer<Filter> withThat) {
        for (Filter filter : filters) {
            filter.visit(withThat);
        }
    }

    public IFeed toIFeed() {
        IFeed result = new IFeed();
        result.setName((String) get("name"));
        for (Filter filter : getFilters()) {
            result.addFilter(filter.toIFeedFilter());
        }
        return result;
    }

    public String toText() {
        return print(this);
    }

    private static String print(Feed feed) {
        StringBuilder sb = new StringBuilder();
        sb.append(feed.get("id") + " " + feed.get("name") + " ");
        MutableInt counter = new MutableInt(0);
        for (Filter filter : feed.getFilters())
            filter.visit(f -> counter.setValue(counter.intValue() + 1));
        sb.append("Filters " + counter.intValue());
        for (Filter filter : feed.getFilters()) {
            sb.append(print(filter, 1));
        }
        return sb.toString();
    }

    static String print(Filter filter, int level) {
        StringBuilder sb = new StringBuilder();
        String fieldName = "";
        String queryPrefix = "";

        FieldInf fi = filter.getFieldInf();
        if (fi != null) {
            fieldName = fi.get("name") + " ";
            if (fi.get("query_prefix") != null) {
                queryPrefix = fi.get("query_prefix") + " ";
            }
        }

        sb.append("\n" + " ".repeat(level) + fieldName + filter.get("filterkey") +
                " " + queryPrefix + filter.get("operator") + " " + filter.get("filterquery"));
        if (!filter.getChildren().isEmpty()) {
            for (Filter child : filter.getChildren()) {
                sb.append(print(child, level + 2));
            }
        }
        return sb.toString();
    }

}
