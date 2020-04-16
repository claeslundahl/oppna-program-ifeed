package se.vgregion.ifeed.tools;

import se.vgregion.ifeed.types.IFeedFilter;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;

public class Filter extends Tuple {

    private final List<Filter> children = new ArrayList<>();

    public Filter() {
        super();
    }

    public Filter(Filter filter) {
        this();
        putAll(filter);
    }

    public static List<? extends Filter> toFilters(List<Tuple> findings) {
        List<Filter> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toFilter(finding));
        }
        return result;
    }

    static Filter toFilter(Tuple tuple) {
        Filter result = new Filter();
        result.putAll(tuple);
        return result;
    }

    public void fill(DatabaseApi database) {
        if (get("id") != null) {
            List<Tuple> findings = database.query("select * from vgr_ifeed_filter where parent_id = ?", get("id"));
            children.addAll(toFilters(findings));
            for (Filter child : children) {
                child.fill(database);
            }
        }
    }

    public List<Filter> getChildren() {
        return children;
    }

    public void insert(DatabaseApi into, Feed withFeed, Filter orParent) {
        List<Tuple> r = into.query("select max(id) + 1 from vgr_ifeed_filter");
        final long id = (long) r.get(0).values().iterator().next();
        put("id", id);

        if (withFeed != null) {
            put("ifeed_id", withFeed.get("id"));
        } else {
            remove("ifeed_id");
        }

        if (orParent != null) {
            put("parent_id", orParent.get("id"));
        }

        into.insert("vgr_ifeed_filter", this);

        for (Filter child : children) {
            child.put("parent_id", id);
            child.insert(into, null, this);
        }
    }

    public static void main(String[] args) {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        System.out.println(generatedString);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>(this);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Filter child : children) list.add(child.toMap());
        result.put("children", list);
        return result;
    }

    public void delete(DatabaseApi fromHere) {
        for (Filter child : children) {
            child.delete(fromHere);
        }
        fromHere.execute("delete from vgr_ifeed_filter where id = ?", get("id"));
    }

    public void visit(Consumer<Filter> that) {
        that.accept(this);
        for (Filter child : children) {
            child.visit(that);
        }
    }

    public IFeedFilter toIFeedFilter() {
        IFeedFilter result = new IFeedFilter();

        result.setFilterKey((String) get("filterkey"));
        result.setFilterQuery((String) get("filterquery"));

        for (Filter child : getChildren()) {
            result.getChildren().add(child.toIFeedFilter());
        }

        return result;
    }

}
