package se.vgregion.ifeed.lookup;

import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.FeedDocumentIndexSupport;
import se.vgregion.ifeed.tools.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App implements Serializable {

    static long idIndex = 0;

    private long id = idIndex++;

    private List<Tuple> result;

    public List<Tuple> fetch(String forThat) {
        if (forThat == null || "".equals(forThat.trim())) {
            result = null;
            return new ArrayList<>();
        }
        DatabaseApi database = DatabaseApi.getLocalApi();
        List<Tuple> result = database.query("select * from feed_document_index where document_id = ?", forThat);
        setResult(result);
        return result;
    }

    public List<Tuple> getResult() {
        return result;
    }

    public void setResult(List<Tuple> result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String reindex() {
        if (!FeedDocumentIndexSupport.isRunning()) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule((Runnable) () -> FeedDocumentIndexSupport.main(), 20l, TimeUnit.SECONDS);
            return "Startar körning.";
        } else {
            return "Indexering körs på "
                    + FeedDocumentIndexSupport.totalCount
                    + " flöden. Är på flöde #"
                    + FeedDocumentIndexSupport.runCounter + ".";
        }
    }

}
