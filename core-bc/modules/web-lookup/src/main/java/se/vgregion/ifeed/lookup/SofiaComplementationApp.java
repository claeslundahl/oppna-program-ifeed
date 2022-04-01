package se.vgregion.ifeed.lookup;

import se.vgregion.ifeed.tools.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class SofiaComplementationApp implements Serializable {

    private String input;

    private final NavigableSet<Number> notFound = new TreeSet<>();

    private final List<Feed> alreadyComplemented = new ArrayList<>();

    private final List<Feed> notComplemented = new ArrayList<>();

    private final List<Feed> justComplemented = new ArrayList<>();

    public List<Feed> run(final String input) {
        Set<Number> ids = split(input);
        this.input = ids.stream().map(id -> id.toString()).collect(Collectors.joining(", "));

        notFound.clear();
        alreadyComplemented.clear();
        notComplemented.clear();
        justComplemented.clear();

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tuple> alreadyComplementedItems = App.getDatabase().query("select * from vgr_ifeed f where " +
                String.format(" f.id in (%s)", ids.stream().map(n -> n.toString())
                        .collect(Collectors.joining(", ")))
                + " and f.id * -1 in (select id from vgr_ifeed)");

        alreadyComplemented.addAll(Feed.toFeeds(alreadyComplementedItems));

        this.input = ids.stream().map(id -> id.toString()).collect(Collectors.joining(", "));
        StartCompletionForExplicitFlows sofia = new StartCompletionForExplicitFlows() {
            @Override
            public void generateFlows() {
                generateFlows(String.format(" and f.id in (%s)", SofiaComplementationApp.this.input));
            }

            @Override
            protected DatabaseApi initDatabase() {
                return App.getDatabase();
            }
        };
        sofia.getLatestGenerated().clear();

        sofia.main();
        SequenceUtil.checkAndOrFixHibernateIndex(App.getDatabase());
        App.getDatabase().commit();
        justComplemented.addAll(sofia.getLatestGenerated());
        if (justComplemented.size() < ids.size()) {
            List<Tuple> databaseItems = App.getDatabase().query("select * from vgr_ifeed f where " +
                    String.format(" f.id in (%s)", SofiaComplementationApp.this.input));
            Set<Number> databaseItemsIds = databaseItems.stream().map(i -> (Number) i.get("id")).collect(Collectors.toSet());
            notFound.addAll(ids);
            notFound.removeAll(databaseItemsIds);

            notComplemented.addAll(Feed.toFeeds(databaseItems));
            notComplemented.removeAll(justComplemented);
            notComplemented.removeAll(alreadyComplemented);
        }
        return sofia.getLatestGenerated();
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
}
