package se.vgregion.ifeed.client;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Starting point for the web-script. It delas with loading information from the page it is loaded on and then to
 * get data from the server, then rendering that data on the client.
 * <p>
 * The feeds, if several on the page, is rendered one after the other. Each rendering is done after an asyncronous call
 * to the server. If the sice of the feed is larger then the batch-size (se value in the code), then several calls are
 * made for that feed. This division is made so that the client browser not will be that likely to frees for longer
 * moments.
 */
public class Main implements EntryPoint {

    public final static List<TableDef> tableDefs = new ArrayList<TableDef>();

    private static Images images = GWT.create(Images.class);

    private static List<Element> ifeedDocLists;

    private static int batchSize = 200;

    HandlerRegistration handler;

    /**
     * Here the execution of the code starts.
     */
    @Override
    public void onModuleLoad() {
        final Element body = RootPanel.getBodyElement();
        if (body == null) {
            Util.log("Did not find the body!");
        }
        List<Element> result = ElementUtil.findByCssClass(body, "ifeedDocList");
        for (Element oldElement : result) {
            oldElement.setInnerHTML("");
        }
        ifeedDocLists = result;
        fetchNext();

        com.google.gwt.dom.client.Element rerunIfeedLoadingElement = Document.get().getElementById("rerun-ifeed-loading");
        if (rerunIfeedLoadingElement != null) {
            Button rerunIfeedLoadingButton = Button.wrap(rerunIfeedLoadingElement);
            if (rerunIfeedLoadingButton != null) {
                if (handler != null) {
                    handler.removeHandler();
                }
                handler = rerunIfeedLoadingButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        List<Element> oldElements = ElementUtil.findByCssClass(body, "doc-list");
                        if (oldElements != null) {
                            for (Element oldElement : oldElements) {
                                oldElement.removeFromParent();
                            }
                        }
                        onModuleLoad();
                    }
                });
            }
        }

    }

    private void fetchNext() {
        if (ifeedDocLists == null || ifeedDocLists.isEmpty()) {
            return;
        }
        Element element = ifeedDocLists.remove(0);
        TableDef tableDef = ElementUtil.toTableDef(element);
        if (tableDef == null) {
            return;
        }
        hideRightEpiServerColumn(tableDef);
        tableDefs.add(tableDef);
        fetch(tableDef);
    }

    private void hideRightEpiServerColumn(TableDef tableDef) {
        if (tableDef.isHideRightColumn()) {
            Element rightcol = DOM.getElementById("rightcol");
            if (rightcol != null) {
                rightcol.getStyle().setDisplay(Style.Display.NONE);
            }
            Element centercolinner = DOM.getElementById("centercolinner");
        }
    }

    private void fetch(final TableDef tableDef) {
        try {
            if (tableDef.getFeedId() != null && tableDef.getFeedId().length() < 800) {
                startFetchByGet(tableDef);
            } else {
                // If the data is longer than 200 chars then use the post method.
                startFetchByPost(tableDef);
            }
        } catch (Exception e) {
            Util.log(e);
            fetchNext();
        }
    }

    private void startFetchByGet(final TableDef tableDef) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        panel.add(new Image(images.loading()));
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();

        requestBuilder.requestObject(Util.getGetUrl(tableDef, 0, batchSize), new AsyncCallback<JsArray<JavaScriptObject>>() {

            @Override
            public void onFailure(Throwable caught) {
                Util.log(caught.getMessage());
                fetchNext();
            }

            @Override
            public void onSuccess(JsArray<JavaScriptObject> response) {
                List<Entry> entries = toEntries(response);
                boolean areThereMoore;
                if (whenOverFetchLimitThenTruncate(tableDef, entries)) {
                    areThereMoore = false;
                } else {
                    areThereMoore = areThereMooreToFetch(entries);
                }

                panel.clear();
                EventedList<Entry> model = new EventedList<Entry>();
                final Display displayTable = new Display(tableDef, model);
                panel.add(displayTable);
                if (tableDef.getOnStartJsCallback() != null && !"".equals(tableDef.getOnStartJsCallback())) {
                    eval(tableDef.getOnStartJsCallback());
                }
                displayTable.getData().addAll(entries);

                if (areThereMoore) {
                    fetchByGet(tableDef, displayTable, batchSize);
                } else {
                    fetchNext();
                }
                displayHits(tableDef, displayTable);

            }

        });
    }


    private void startFetchByPost(final TableDef tableDef) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        panel.add(new Image(images.loading()));
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Util.getIfeedHome(tableDef));

        builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        try {
            String data = Util.getRequestData(tableDef, 0, batchSize, false);
            builder.sendRequest(data, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    List<Entry> entries = toEntries((JsArray<JavaScriptObject>) eval(response.getText()));
                    boolean areThereMoore;
                    if (whenOverFetchLimitThenTruncate(tableDef, entries)) {
                        areThereMoore = false;
                    } else {
                        areThereMoore = areThereMooreToFetch(entries);
                    }

                    panel.clear();
                    EventedList<Entry> model = new EventedList<Entry>();
                    Display displayTable = new Display(tableDef, model);
                    panel.add(displayTable);
                    if (tableDef.getOnStartJsCallback() != null && !"".equals(tableDef.getOnStartJsCallback())) {
                        eval(tableDef.getOnStartJsCallback());
                    }
                    displayTable.getData().addAll(entries);

                    if (areThereMoore) {
                        fetchByPost(tableDef, displayTable, batchSize);
                    } else {
                        fetchNext();
                    }
                    displayHits(tableDef, displayTable);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    fetchNext();
                }
            });
        } catch (Exception e) {
            Util.log(e);
        }
    }


    private void displayHits(final TableDef tableDef, final Display display) {
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                //com.google.gwt.dom.client.Element countDisplay = Document.get().getElementById("ifeed-count-" + tableDef.getFeedId().hashCode());
                com.google.gwt.dom.client.Element countDisplay = Document.get().getElementById("ifeed-count-" + tableDef.getFkIfeedId());
                Util.log("tableDef.getFkIfeedId " + tableDef.getFkIfeedId());
                if (countDisplay != null && display != null && display.getData() != null) {
                    countDisplay.setInnerHTML(display.getData().size() + "");
                }
            }
        });
    }

    private native JavaScriptObject eval(String javascript)
    /*-{
       return eval(javascript);
    }-*/;


    private boolean areThereMooreToFetch(List<Entry> entries) {
        return entries.size() == batchSize;
    }

    private void fetchByGet(final TableDef tableDef, final Display displayTable, final int startAt) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        final int endAt = startAt + batchSize;
        displayTable.displayAjaxLoading();

        requestBuilder.requestObject(Util.getGetUrl(tableDef, startAt, endAt), new AsyncCallback<JsArray<JavaScriptObject>>() {
            @Override
            public void onFailure(Throwable caught) {
                Util.log(caught);
                displayTable.hideAjaxLoading();
            }

            @Override
            public void onSuccess(JsArray<JavaScriptObject> response) {
                List<Entry> entries = toEntries(response);
                displayTable.getData().addAll(entries);

                if (!whenOverFetchLimitThenTruncate(tableDef, displayTable.getData()) && areThereMooreToFetch(entries)) {
                    fetchByGet(tableDef, displayTable, endAt);
                } else {
                    fetchNext();
                }
                displayTable.hideAjaxLoading();
                displayHits(tableDef, displayTable);
            }

        });
    }


    private void fetchByPost(final TableDef tableDef, final Display displayTable, final int startAt) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Util.getIfeedHome(tableDef));
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        final int endAt = startAt + batchSize;
        displayTable.displayAjaxLoading();

        try {
            builder.sendRequest(Util.getRequestData(tableDef, startAt, endAt, false), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    //Util.log("Success " + response.getText());
                    List<Entry> entries = toEntries((JsArray) eval(response.getText()));
                    displayTable.getData().addAll(entries);

                    if (!whenOverFetchLimitThenTruncate(tableDef, displayTable.getData()) && areThereMooreToFetch(entries)) {
                        fetchByPost(tableDef, displayTable, endAt);
                    } else {
                        fetchNext();
                    }
                    displayTable.hideAjaxLoading();
                    displayHits(tableDef, displayTable);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    Util.log(exception);
                    displayTable.hideAjaxLoading();
                }
            });
        } catch (Exception e) {
            Util.log(e);
        }

    }


    private List<Entry> toEntries(JsArray<JavaScriptObject> response) {
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < response.length(); i++) {
            Entry entry = response.get(i).cast();
            entries.add(entry);
        }
        return entries;
    }

    private boolean whenOverFetchLimitThenTruncate(TableDef tableDef, List entries) {
        if (tableDef.getMaxHitLimit() > 0 && tableDef.getMaxHitLimit() <= entries.size()) {
            int limit = tableDef.getMaxHitLimit();
            limit = Math.min(limit, entries.size());
            List retainees = entries.subList(0, limit);
            entries.retainAll(retainees);
            return true;
        } else {
            return false;
        }
    }

}
