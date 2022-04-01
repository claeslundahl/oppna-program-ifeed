package se.vgregion.ifeed.lookup;

import org.apache.commons.lang.mutable.MutableBoolean;
import se.vgregion.ifeed.tools.Feed;
import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.complement.GoverningDocumentComplementation;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class SofiaStyComplementationApp implements Serializable {

    private String input;

    private final NavigableSet<Number> notFound = new TreeSet<>();

    private final List<Feed> alreadyComplemented = new ArrayList<>();

    private final List<Feed> notComplemented = new ArrayList<>();

    private final List<Feed> justComplemented = new ArrayList<>();

    private final List<Feed> withForbiddenFilters = new ArrayList<>();

    private static final Set<String> forbiddenFilters = new HashSet<>(Arrays.asList("dc.type.record", "dc.type.document", "dc.creator.forunit.id"));

    public List<Feed> run(final String input) {
        Set<Number> ids = split(input);
        this.input = ids.stream().map(id -> id.toString()).collect(Collectors.joining(", "));

        notFound.clear();
        alreadyComplemented.clear();
        notComplemented.clear();
        justComplemented.clear();
        withForbiddenFilters.clear();

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(App.getDatabase());
        List<Feed> results = new ArrayList<>();

        for (Number id : ids) {
            List<Feed> feeds = Feed.toFeeds(App.getDatabase().query("select * from vgr_ifeed where id = ?", id));
            if (feeds.isEmpty()) {
                notFound.add(id);
            } else {
                for (Feed feed : feeds) {
                    if (!App.getDatabase().query("select * from vgr_ifeed where id = ?", id.longValue() * -1).isEmpty()) {
                        alreadyComplemented.add(feed);
                    } else {
                        feed.fill(App.getDatabase());
                        MutableBoolean found = new MutableBoolean(false);
                        feed.visit(filter -> {
                            FieldInf fi = filter.getFieldInf();
                            if (fi != null && fi.get("id") != null && forbiddenFilters.contains(fi.get("id"))) {
                                found.setValue(true);
                            }
                        });
                        if (found.booleanValue()) {
                            withForbiddenFilters.add(feed);
                        } else {
                            Feed result = gdc.makeComplement(feed);
                            if (result == null) {
                                notComplemented.add(feed);
                            } else {
                                justComplemented.add(result);
                            }
                        }
                    }
                }
            }
        }
        gdc.commit();
        return results;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    private static Set<Number> split(String s) {
        if (s == null || "".equals(s.trim()))
            return new HashSet<>();
        Set<Number> result = new HashSet<Number>(
                Arrays.asList(s.split("[^0-9]")).stream()
                        .filter(f -> f.matches("[0-9]+"))
                        .map(m -> Long.parseLong(m)).collect(Collectors.toList())
        );
        return result;
    }

    public NavigableSet<Number> getNotFound() {
        return notFound;
    }

    public List<Feed> getAlreadyComplemented() {
        return alreadyComplemented;
    }

    public List<Feed> getNotComplemented() {
        return notComplemented;
    }

    public List<Feed> getJustComplemented() {
        return justComplemented;
    }

    public List<Feed> getWithForbiddenFilters() {
        return withForbiddenFilters;
    }

}
