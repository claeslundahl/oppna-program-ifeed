package se.vgregion.ifeed.tools;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class FilterResultCounts extends HashMap<Long, Long> {

    private final String url;

    private final Function<String, Long> counter;

    public FilterResultCounts(String url, Function<String, Long> counter) {
        super();
        this.url = url;
        this.counter = counter;
    }

    private long getAndCountHits(Long feedId) {
        long result = getAndCountHits(String.format(url, feedId + ""));
        if (result > 0) result--;
        put(feedId, result);
        return result;
    }

    private long getAndCountHits(String fromUrl) {
        String htmlList = Http.get(fromUrl);
        if (htmlList != null && !"".equals(htmlList.trim()))
            return counter.apply(htmlList);
        else return 0;
    }

    public static void main(String[] args) {
        // DatabaseApi database = DatabaseApi.getRemoteStageDatabaseApi();
        FilterResultCounts remote = new FilterResultCounts(
                "https://ifeed.vgregion.se/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=id",
                s -> Long.valueOf(s.split(Pattern.quote("\"id\":")).length)
        );

        FilterResultCounts local = new FilterResultCounts(
                "http://localhost:8080/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc&f=id",
                s -> Long.valueOf(s.split(Pattern.quote("\"id\":")).length)
        );
        remote.getAndCountHits(1168L);
        local.getAndCountHits(1168L);

        System.out.println(remote);
        System.out.println(local);

        DatabaseApi database = DatabaseApi.getLocalApi();

        List<Tuple> ids = database.query("select distinct ifeed_id from feed_document_index");

        System.out.println(ids.size());

        int count = 0;
        for (Tuple id : ids) {
            count++;
            remote.getAndCountHits((Long) id.get("ifeed_id"));
            local.getAndCountHits((Long) id.get("ifeed_id"));
            if (count % 10 == 0) System.out.print(" " + String.format("%04d", count));
            if (count % 200 == 0) System.out.println();
        }

        for (Entry<Long, Long> localEntry : local.entrySet()) {
            if (!remote.entrySet().contains(localEntry)) {
                System.out.println();
                System.out.println(localEntry);
                System.out.println(remote.get(localEntry.getKey()));
            }
        }

    }

}
