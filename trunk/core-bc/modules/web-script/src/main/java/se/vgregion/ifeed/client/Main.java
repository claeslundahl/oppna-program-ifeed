package se.vgregion.ifeed.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-03-13.
 */
public class Main implements EntryPoint {

    public final static List<TableDef> tableDefs = new ArrayList<TableDef>();

    private static Images images = GWT.create(Images.class);

    private static List<Element> ifeedDocLists;

    private static int batchSize = 300;

    @Override
    public void onModuleLoad() {
        Element body = RootPanel.getBodyElement();
        List<Element> result = ElementUtil.findByCssClass(body, "ifeedDocList");
        ifeedDocLists = result;
        fetchNext();
        Util.consoleLog("Alla doclists");
        Util.consoleLog(ifeedDocLists);
    }

    void fetchNext() {
        if (ifeedDocLists.isEmpty()) {
            return;
        }
        Element element = ifeedDocLists.remove(0);
        TableDef tableDef = ElementUtil.toTableDef(element);
        tableDefs.add(tableDef);
        fetch(tableDef);
    }


    String getUrl(TableDef tableDef, int startBy, int endBy) {
        // http://ifeed.vgregion.se/
        //String url = "http://127.0.0.1:8080/example-feed.jsonp.jsp";
        /*String url = "http://ifeed.vgregion.se/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder();*/

        /*
        String url = "http://127.0.0.1:8080/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy;
*/

        String url = "http://ifeed.vgregion.se/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy;

        return url;
    }


    private void fetch(final TableDef tableDef) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        panel.add(new Image(images.loading()));
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();

        requestBuilder.requestObject(getUrl(tableDef, 0, 100), new AsyncCallback<JsArray<JavaScriptObject>>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(JsArray<JavaScriptObject> response) {
                List<Entry> entries = new ArrayList<Entry>();
                for (int i = 0; i < response.length(); i++) {
                    Entry entry = response.get(i).cast();
                    entries.add(entry);
                }

                boolean croped = cropDataWhenTooMuch(tableDef, entries);
                panel.clear();
                DisplayTable displayTable = new DisplayTable(tableDef, entries);
                panel.add(displayTable);

                if (!croped && entries.size() == batchSize) {
                    fetch(tableDef, displayTable, batchSize);
                } else {
                    fetchNext();
                }
            }

        });
    }

    private boolean cropDataWhenTooMuch(TableDef tableDef, List<Entry> entries) {
        if (tableDef.getLimit() == 0) {
            return false;
        }
        if (entries.size() >= tableDef.getLimit()) {
            entries.removeAll(entries.subList(tableDef.getLimit(), entries.size()));
            return true;
        }
        return false;
    }

    private void fetch(final TableDef tableDef, final DisplayTable displayTable, final int startAt) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        //panel.add(new Image(images.loading()));
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();

        final int endAt = startAt + batchSize;

        requestBuilder.requestObject(getUrl(tableDef, startAt, endAt), new AsyncCallback<JsArray<JavaScriptObject>>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(JsArray<JavaScriptObject> response) {
                List<Entry> entries = new ArrayList<Entry>();
                for (int i = 0; i < response.length(); i++) {
                    Entry entry = response.get(i).cast();
                    entries.add(entry);
                }

                displayTable.getData().addAll(entries);

                if (!cropDataWhenTooMuch(tableDef, displayTable.getData()) && entries.size() == batchSize) {
                    fetch(tableDef, displayTable, endAt);
                } else {
                    fetchNext();
                }

                displayTable.render();
            }

        });
    }


}
