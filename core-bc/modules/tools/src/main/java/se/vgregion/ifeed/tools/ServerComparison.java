package se.vgregion.ifeed.tools;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ServerComparison {

    private final String hostOneUrl;

    private final String hostTwoUrl;

    private final DatabaseApi database;

    private final String callPartTemplate = "/iFeed-web/documentlists/%s/?by=&dir=asc";

/*
    private final NavigableSet<Number> diffingFeeds = new TreeSet<>();

    private final NavigableSet<Number> errorFeeds = new TreeSet<>();
*/

    public static void main(String[] args) {
        ServerComparison serverComparer = new ServerComparison(
                "http://localhost:8080", "https://ifeed.vgregion.se", null);

        // These diffed.
        System.out.println("Rerun of those that diffed.");
        Result result = serverComparer.run(
                Arrays.asList(-437593385, -437593051, -437588458, -437588444, -4581132, -4381862, -4091246,
                -3755468, -3727232, -3727230, -118096, -116459, 114018, 116459, 4049043, 4373893, 4451335, 4478631,
                4581132, 437588342, 437588444, 437588458, 437589353, 437590504, 437590508, 437594058, 437598201,
                437618804, 437629189, 437629195, 437629216, 437629321)
        );
        print(result);
        System.out.println();
        // These failed - ann error occurred.
        System.out.println("Rerun of those that failed with error.");
        result = serverComparer.run(
                Arrays.asList(-3744695, -122053, -122048, 887, 122794, 129199, 129715, 130252, 437597786)
        );
        print(result);
    }

    public static void mainFromDatabase() {
        DatabaseApi database = DatabaseApi.getLocalApi();
        ServerComparison serverComparer = new ServerComparison("http://localhost:8080", "https://ifeed.vgregion.se", database);

    }

    private static void print(Result result) {
        System.out.println();
        System.out.println("With errors: " + result.errorFeeds);
        System.out.println("With diffs: " + result.diffingFeeds);
    }

    public ServerComparison(String hostOneUrl, String hostTwoUrl, DatabaseApi database) {
        this.hostOneUrl = hostOneUrl;
        this.hostTwoUrl = hostTwoUrl;
        this.database = database;
    }

    public Result run() {
        String sql = "select distinct ifeed_id from vgr_ifeed_filter where ifeed_id is not null order by 1";
        List<Tuple> items = database.query(sql);
        System.out.println("Found " + items.size() + " items.");
        return run(items);
    }

    public Result run(Collection<Number> items) {
        Set<Tuple> tuples = items.stream().map(item -> new Tuple(Map.of("ifeed_id", item)))
                .collect(Collectors.toSet());

        return run(tuples);
    }

    public Result run(Iterable<Tuple> items) {
        String firstUrl = hostOneUrl + callPartTemplate;
        String secondUrl = hostTwoUrl + callPartTemplate;
        Result result = new Result();
        int i = 0;
        for (Tuple item : items) {
            Object id = (item.get("ifeed_id"));
            String url1 = String.format(firstUrl, id.toString());
            String url2 = String.format(secondUrl, id.toString());
            try {
                if (getAndCountHits(url1) != getAndCountHits(url2)) {
                    result.diffingFeeds.add((Number) id);
                }
            } catch (Exception e) {
                result.errorFeeds.add((Number) id);
            }
            i++;
            if (i % 10 == 0) System.out.print(String.format("%05d ", i));
            if (i % 100 == 0) System.out.println();
        }
        return result;
    }


    private static int getAndCountHits(String fromUrl) {
        String htmlList = Http.get(fromUrl);
        return (htmlList.split(Pattern.quote("class=\"clearfix\"")).length);
    }

    public static class Result {
        public final NavigableSet<Number> diffingFeeds = new TreeSet<>();
        public final NavigableSet<Number> errorFeeds = new TreeSet<>();

        @Override
        public int hashCode() {
            return diffingFeeds.hashCode() + errorFeeds.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Result) {
                Result result = (Result) obj;
                return result.diffingFeeds.equals(diffingFeeds) && result.errorFeeds.equals(errorFeeds);
            }
            return false;
        }

    }

}
