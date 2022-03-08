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

    private Group group;
    private Department department;

    public Feed() {
        super();
    }

    private final List<Filter> filters = new ArrayList<>();

    private final List<CompositeLink> composites = new DistinctArrayList<>();

    private final List<CompositeLink> partOf = new DistinctArrayList<>();

    private final List<Ownership> ownerships = new ArrayList<>();

    private final List<DynamicTable> dynamicTables = new ArrayList<>();

    private DatabaseApi database;

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
        this.database = database;
        List<Tuple> findings = database.query("select * from vgr_ifeed_filter where ifeed_id = ?", get("id"));
        filters.addAll(Filter.toFilters(findings));
        for (Filter filter : filters) {
            filter.fill(database);
        }
        findings = database.query("select * from vgr_ifeed_vgr_ifeed where partof_id = ?", get("id"));
        composites.addAll(CompositeLink.toCompositeLinks(findings));

        findings = database.query("select * from vgr_ifeed_vgr_ifeed where composites_id = ?", get("id"));
        partOf.addAll(CompositeLink.toCompositeLinks(findings));

        findings = database.query("select * from vgr_ifeed_ownership where ifeed_id = ?", get("id"));
        ownerships.addAll(Ownership.toOwnerships(findings));

        findings = database.query("select * from vgr_ifeed_dynamic_table where fk_ifeed_id = ?", get("id"));
        dynamicTables.addAll(DynamicTable.toDynamicTables(findings));
        for (DynamicTable dynamicTable : dynamicTables) {
            dynamicTable.fill(database);
        }

        if (containsKey("group_id")) {
            findings = database.query("select * from vgr_ifeed_group where id = ?", get("group_id"));
            for (Group group : (Group.toGroups(findings))) {
                this.group = group;
            }
        }

        if (containsKey("department_id")) {
            findings = database.query("select * from vgr_ifeed_department where id = ?", get("department_id"));
            for (Department department : (Department.toDepartments(findings))) {
                this.department = department;
            }
        }
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
        for (Ownership ownership : ownerships) {
            ownership.delete(fromHere);
        }

        for (DynamicTable dynamicTable : dynamicTables) {
            dynamicTable.delete(fromHere);
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

    public IFeed toJpaVersion() {
        IFeed result = new IFeed();
        result.setId((Long) get("id"));
        result.setName((String) get("name"));
        for (Filter filter : getFilters()) {
            result.addFilter(filter.toIFeedFilter());
        }
        for (CompositeLink composite : getComposites()) {
            List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", composite.get("composites_id"));
            if (items.size() != 1) throw new RuntimeException();
            Feed item = Feed.toFeed(items.get(0));
            item.fill(database);
            IFeed compositeFeed = item.toJpaVersion();
            result.getComposites().add(compositeFeed);
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

    public List<Ownership> getOwnerships() {
        return ownerships;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<DynamicTable> getDynamicTables() {
        return dynamicTables;
    }
}
