package se.vgregion.ifeed.tools;

import se.vgregion.common.utils.DistinctArrayList;
import se.vgregion.common.utils.Json;
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

    static Feed toFeed(Map<String, Object> fromThat) {
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

    @Override
    public String toString() {
        Map result = new HashMap(this);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Filter filter : filters) list.add(filter.toMap());
        result.put("filters", list);
        return Json.toJson(result);
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

}
