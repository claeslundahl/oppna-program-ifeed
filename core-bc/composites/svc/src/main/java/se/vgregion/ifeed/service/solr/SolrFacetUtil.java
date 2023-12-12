package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.StringUtils;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by clalu4 on 2014-06-27.
 */
public class SolrFacetUtil {

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    /**
     * Calls the solr server to get facet result of a certain IFeed and field.
     *
     * @param solrBaseUrl the location of the server to use.
     * @param feed        the query to run as condition.
     * @param field       what property to get facets for.
     * @return the top 10 results of the search, sorted descending.
     */
    public static List<String> fetchFacets(String solrBaseUrl, IFeed feed, FieldInf field, String starFilter) {
        try {
            return fetchFacetsImpl(solrBaseUrl, feed, field, starFilter);
        } catch (Exception e) {
            try {
                Files.write(Paths.get(System.getProperty("user.home"), "feed.json"), se.vgregion.common.utils.Json.toJson(feed).getBytes());
            } catch (Exception iff) {
                iff.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static List<String> fetchFacetsImpl(String solrBaseUrl, IFeed feed, FieldInf fi, String starFilter) throws Exception {
        String field = fi.getId();
        if (feed == null) {
            throw new NullPointerException();
        }
        feed.getFilters().addAll(fi.getEntireDefaultCondition());
        final String question = feed.toQuery(client.fetchFields());
        System.out.println("Type ahead: " + question);
        Result result = client.query(
                question,
                0,
                10,
                "asc",
                fi,
                fi.getId()
        );
        if (result.getDocumentList() == null) {
            /*result = client.query(
                    question,
                    0,
                    10_000,
                    null,
                    null
            );*/
            throw new RuntimeException(); // Remove this later...
        }
        List<String> things = new ArrayList<>(SolrHttpClient.toFacetSet(result, field, starFilter));
        if (fi.getQueryPrefix() != null) {
            Set<String> moddedThings = new TreeSet<>();
            for (String thing : things) {
                if (thing.startsWith(fi.getQueryPrefix())) {
                    /*moddedThings.addAll(
                            Arrays.asList(thing.split(Pattern.quote("/"))).stream().filter(s -> {
                                        // System.out.println(s + " == " + starFilter + " -> " + hasLeadingStarPattern(s, starFilter));
                                        return hasLeadingStarPattern(s, starFilter);
                                    }
                            ).collect(Collectors.toSet())
                    );*/
                    ArrayList<String> parts = new ArrayList<>(Arrays.asList(thing.split(Pattern.quote("/"))));
                    moddedThings.add(parts.get(parts.size() - 1));
                }
            }
            moddedThings.remove("");
            moddedThings.remove(fi.getQueryPrefix());
            things = new ArrayList<>(moddedThings);
        }
        things = things.subList(0, Math.min(10, things.size()));
        // System.out.println(things);
        return things;
    }

/*    public static void main(String[] args) {
        String p = "SweMeSH/Sjukdomar";
        String t = "SweMeSH/Sjukdomar/Hjärt-kärlsjukdomar";
        System.out.println(
                removeLeadingStarPatternFromText(t, p)
        );
    }*/

/*    public static String toWildCardRegExp(String wildcardStr) {
        Pattern regex = Pattern.compile("[^*?\\\\]+|(\\*)|(\\?)|(\\\\)");
        Matcher m = regex.matcher(wildcardStr);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            if (m.group(1) != null) m.appendReplacement(sb, ".*");
            else if (m.group(2) != null) m.appendReplacement(sb, ".");
            else if (m.group(3) != null) m.appendReplacement(sb, "\\\\\\\\");
            else m.appendReplacement(sb, "\\\\Q" + m.group(0) + "\\\\E");
        }
        m.appendTail(sb);
        return sb.toString();
    }*/

    public static boolean hasLeadingStarPattern(String input, String pattern) {
        if (pattern == null || pattern.trim().equals("") || pattern.trim().equals("[*]+")) {
            return true;
        }
        List<String> parts = new ArrayList<>(Arrays.asList(pattern.split(Pattern.quote("*"))));
        if (parts.isEmpty()) return true;
        if (!input.startsWith("*") && !input.startsWith(parts.get(0))) {
            return false;
        }

        while (!parts.isEmpty()) {
            String head = parts.remove(0);
            if (head.equals(""))
                continue;
            String[] two = input.split(Pattern.quote(head), 2);
            /*System.out.println("Head: " + head);
            System.out.println("Two: " + Arrays.asList(two));*/
            if (two.length != 2) {
                return false;
            }
            input = two[1];
        }
        return true;
    }

    public static void main(String[] args) {
        String foo = "SweMeSH/Sjukdomar/Bakterie- och svampinfektioner";
        for (String s : foo.split(Pattern.quote("/"))) {
            System.out.println(hasLeadingStarPattern(s, "*Bak*"));
        }
        System.out.println("hej " + "***".matches("[*]+"));
        System.out.println(hasLeadingStarPattern(foo, "****"));

        System.out.println("Ska vara t: " + hasLeadingStarPattern("Hej jag heter foo", "*Hej jag*foo"));
        System.out.println("Ska vara t: " + hasLeadingStarPattern("Hej jag heter foo", "Hej jag*foo"));
        System.out.println("Ska vara t: " + hasLeadingStarPattern("Hej jag heter foo", "***Hej jag**foo*"));
        System.out.println("Ska vara f: " + hasLeadingStarPattern("Hej jag heter foo", "XHej jag*foo"));
        System.out.println("Ska vara f: " + hasLeadingStarPattern("Hej jag heter foo", "*XHej jag*foo"));
    }


    public static String removeLeadingStarPatternFromText(String s, String p) {
        String[] parts = p.split(Pattern.quote("*"));
        return splitAndGetLastBitOnly(s, new ArrayList<>(Arrays.asList(parts)));
    }

    static String splitAndGetLastBitOnly(String input, List<String> partsToRemoveFromStart) {
        if (partsToRemoveFromStart.isEmpty()) {
            return input;
        }
        String first = partsToRemoveFromStart.remove(0);
        String[] parts = input.split(Pattern.quote(first), 2);
        return splitAndGetLastBitOnly(parts[parts.length - 1], partsToRemoveFromStart);
    }

    private static String facetUrl(String solrBaseUrl, IFeed feed, String field) {
        String settings = "select?wt=json&fl=%s&rows=10&facet=true&facet.mincount=1&q=published:true";
        settings = String.format(settings, field);

        IFeedSolrQuery.FeedFilterBag bag = new IFeedSolrQuery.FeedFilterBag();
        StringBuilder sb = new StringBuilder(solrBaseUrl + (solrBaseUrl.endsWith("/") ? "" : "/") + settings);
        for (IFeedFilter f : feed.getFilters()) {
            bag.get(f.getFilterKey() + getFirstNonBlank(f.getOperator(), "matching")).add(f);
        }
        for (List<IFeedFilter> iFeedFilters : bag.values()) {
            sb.append("&fq=");
            String oq = SolrQueryBuilder.createOrQuery(iFeedFilters);
            if ("(())".equals(oq)) {
                throw new RuntimeException("(())");
            }//TODO: Consider removing this before next deploy.
            sb.append(oq);
        }
        return sb.toString().replaceAll(Pattern.quote(" "), "%20");
    }

    static String getFirstNonBlank(String first, String second) {
        if (StringUtils.isBlank(first)) {
            return second;
        } else {
            return first;
        }
    }


}
