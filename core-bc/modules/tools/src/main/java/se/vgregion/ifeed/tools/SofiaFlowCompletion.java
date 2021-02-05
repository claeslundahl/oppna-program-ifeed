package se.vgregion.ifeed.tools;

import se.vgregion.common.utils.MultiMap;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.types.IFeed;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.String.format;
import static se.vgregion.common.utils.Props.fetchProperties;
import static se.vgregion.ifeed.tools.DatabaseApi.getDatabaseApi;

/**
 * <p> There are two sets of metadata in the document handling systems - SOFIA (S) and Alfresco/Barium (AB).
 * SOFIA is the new standard that holds a lot less different fields for the users to mark their documents with. </p>
 * <p> The two types of sets coexists in the index. Some of the metadata is, on a loose conceptual level, the same between
 * the two specifications. For example: The field "vgr:VgrExtension.vgr:PublishedForUnit.id" in S is equivalent of the
 * field "dc.publisher.forunit.id" in AB. </p>
 * <p> This class contains static functionality that generates equivalent feeds from old ones. Feeds from AB that
 * contains fields that have corresponding fields in S can be taken as input and then produce an output that makes
 * another feed who is supposed to hit documents from S. </p>
 * <p> The new feed(s) then is included into the old one (there exists a construct in ifeed that gives the ability to
 * nest feeds into each other - appending their search results together). This makes old feeds produce search results
 * that includes documents from the new SOFIA document source also. </p>
 */
public class SofiaFlowCompletion {

    DatabaseApi database;

    static Map<String, String> old2newFilterNames = new HashMap<>();

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    static {
        for (CorrespondingKeys correspondingKey : MetadataSwap.CORRESPONDING_KEYS) {
            for (String fromThat : correspondingKey.fromThese) {
                old2newFilterNames.put(fromThat, correspondingKey.newKey);
            }
        }
    }

    /*public static void main(String[] args) {
        new SofiaFlowCompletion().start();
    }*/

    public void start() {
        database = getDatabaseApi();
        System.out.println("Flödeskopmplettering för databasen " + database.getUrl());
        if (true) return;
        long startTime = System.currentTimeMillis();
        main();
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("Tid för utförande var " + duration + "ms. Eller " + ((duration / 1000) + "s."));
    }

    public void main() {
        deleteAllGeneratedFlows();
        generateFlows();
        checkAndOrFixHibernateIndex();
        database.commit();
    }


    public List<Feed> findMoreComplexFeeds() {
        List<Feed> result = fetchAllApplicable();
        for (Feed feed : new ArrayList<>(result)) {
            if (feed.getFilters().size() > 2) {

            } else {
                result.remove(feed);
            }
        }
        return result;
    }

    public void generateFlows() {
        generateFlows(" and id = 437584535hejknekt ");
    }

    public static boolean hasBariumStuff(Feed feed) {
        IFeed iFeed = feed.toIFeed();
        Result result = client.query(iFeed.toQuery(client.fetchFields()), 0, 1_000_000, null, null);
        if (result.getResponse() != null) {
            for (Map<String, Object> doc : result.getResponse().getDocs()) {
                if (doc.containsKey("SourceSystem") && "Barium".equals(doc.get("SourceSystem"))) {
                    return true;
                }
            }
        }
        // System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(result));
        return false;
    }

    public void generateFlows(String extraSqlCondition) {
        List<Feed> feeds = fetchAllApplicable(extraSqlCondition);
        System.out.println("Bearbetar " + feeds.size() + " flöden.");
        int c = 1;
        List<Long> feedIds = new ArrayList<>();
        for (Feed feed : feeds) {
            if (hasBariumStuff(feed)) {
                System.out.println("Feed had Barium stuff: " + feed);
                System.out.println("Skips it!");
                continue;
            }
            Feed sofia = toSofiaEquivalent(feed);
            sofia.insert(database);
            long id = (long) feed.get("id");
            feedIds.add(id);
            new CompositeLink(id, id * -1).insert(database);
            c++;
        }
        System.out.println("Skapade flödeskomplettering för dessa: " + feedIds);
    }

    void deleteAllGeneratedFlows() {
        List<Feed> feeds = Feed.toFeeds(database.query("select * from vgr_ifeed where id < 0"));
        System.out.println("Hittade " + feeds.size() + " att ta bort.");
        for (Feed feed : feeds) {
            feed.fill(database);
            feed.delete(database);
        }
    }

    static Feed toSofiaEquivalent(Feed feed) {
        Feed compensation = new Feed(feed);
        long id;
        compensation.put("id", id = ((long) compensation.get("id")) * -1);
        // System.out.println("Id som skapas är " + id);
        compensation.put("name", compensation.get("name") + " (SOFIA-komplement)");
        compensation.put("userid", "lifra1");
        compensation.remove("group_id");
        compensation.remove("department_id");

        MultiMap<String, Filter> oldName2field = new MultiMap<>();
        for (Filter filter : feed.getFilters()) {
            oldName2field.get(filter.get("filterkey")).add(filter);
        }
        oldName2field.keySet().retainAll(allOldFields());
        Filter and = new Filter();
        and.put("operator", "and");
        for (String key : oldName2field.keySet()) {
            List<Filter> filters = oldName2field.get(key);
            if (filters.size() == 1) {
                Filter filter = new Filter(filters.get(0));
                filter.put("filterkey", old2newFilterNames.get(filter.get("filterkey")));
                filter.put("id", -1 * (long) filter.get("id"));
                and.getChildren().add(filter);
            } else {
                Filter or = new Filter();
                or.put("operator", "or");
                for (Filter filter : filters) {
                    filter = new Filter(filter);
                    filter.put("filterkey", old2newFilterNames.get(filter.get("filterkey")));
                    or.getChildren().add(filter);
                    filter.put("id", -1 * (long) filter.get("id"));
                }
                and.getChildren().add(or);
            }
        }

        if (and.getChildren().size() > 1) {
            compensation.getFilters().add(and);
        } else {
            Filter one = and.getChildren().get(0);
            compensation.getFilters().add(one);
        }

        compensation.visit(filter -> filter.remove("ifeed_id"));

        for (Filter filter : compensation.getFilters()) {
            filter.put("ifeed_id", id);
        }

        return compensation;
    }

    List<Feed> fetchAllApplicable() {
        return fetchAllApplicable("");
    }

    /**
     * Selects all of the feeds from the db that does not already have an included/nested feed with minus id.
     *
     * @param withExtraCondition Extra condition where the caller can add conditions to the call. Example of this
     *                           could be "f.id = 123" where the feed with id 123 is being fetched.
     * @return A lis of objects as a result of the db query.
     */
    List<Feed> fetchAllApplicable(String withExtraCondition) {
        String sql =
                "select f.* from vgr_ifeed f \n" +
                        "where f.id in (\n" +
                        "  select distinct ifeed_id from vgr_ifeed_filter where filterkey in (\n" +
                        "  \t'%s'\n" +
                        "  )\n" +
                        ") and f.id not in (select (id*-1) from vgr_ifeed where id < 0) \n" + withExtraCondition +
                        "\nexcept select * from vgr_ifeed where id < 0";
        sql = String.format(sql, String.join("', '", allOldFields()));
        List<Feed> result = Feed.toFeeds(database.query(sql));
        for (Feed feed : result) {
            feed.fill(database);
        }
        return result;
    }

    static List<String> allOldFields() {
        List<String> result = new ArrayList<>();
        for (CorrespondingKeys correspondingKey : MetadataSwap.CORRESPONDING_KEYS) {
            result.addAll(correspondingKey.fromThese);
        }
        return result;
    }

    /**
     * Turn up the hibernate index if the primary keys in vgr_ifeed_filter exceeds the current value.
     */
    void checkAndOrFixHibernateIndex() {
        long lastSequenceValue = (long) database.oneFieldSingleValueQuery("SELECT last_value FROM hibernate_sequence");
        long maxFilterId = (long) database.oneFieldSingleValueQuery("SELECT max(id) FROM vgr_ifeed_filter");
        if (maxFilterId >= lastSequenceValue) {
            database.update(format("alter sequence hibernate_sequence restart with %d", lastSequenceValue + 1l));
        }
    }

}
