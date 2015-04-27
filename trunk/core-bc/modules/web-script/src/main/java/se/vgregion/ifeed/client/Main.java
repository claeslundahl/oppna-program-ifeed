package se.vgregion.ifeed.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Starting point for the web-script. It delas with loading information from the page it is loaded on and then to
 * get data from the server, then rendering that data on the client.
 * <p/>
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
        if (body == null) Window.alert("Did not find the body!");
        List<Element> result = ElementUtil.findByCssClass(body, "ifeedDocList");
        for (Element oldElement : result) {
            oldElement.setInnerHTML("");
        }
        ifeedDocLists = result;
        fetchNext();

        com.google.gwt.dom.client.Element rerunIfeedLoadingElement = Document.get().getElementById("rerun-ifeed-loading");
        if (rerunIfeedLoadingElement != null) {
            Button rerunIfeedLoadingButton = Button.wrap(rerunIfeedLoadingElement);
            //Util.log("rerunIfeedLoadingButton"); Util.log(rerunIfeedLoadingButton);
            if (rerunIfeedLoadingButton != null) {
                if (handler != null) {
                    handler.removeHandler();
                }
                handler = rerunIfeedLoadingButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        List<Element> oldElements = ElementUtil.findByCssClass(body, "doc-list");
                        for (Element oldElement : oldElements) {
                            oldElement.removeFromParent();
                        }
                        onModuleLoad();
                    }
                });
            }
        }
    }

    private void fetchNext() {
        if (ifeedDocLists.isEmpty()) {
            return;
        }
        Element element = ifeedDocLists.remove(0);
        TableDef tableDef = ElementUtil.toTableDef(element);
        hideRightEpiServerColumn(tableDef);
        tableDefs.add(tableDef);
        fetch(tableDef);
    }

    private void hideRightEpiServerColumn(TableDef tableDef) {
        /*
            Original handling in js-script.
            if ($(".ifeedDocList").eq(i).attr('hideRightColumn') == "true") {
            $("#rightcol").css('display', 'none');
            $("#centercolinner").css('width', '660px');
            $("#rightcol").css("display", "none");
            $(".doc-list").css('width', '100%');
        }
         */
        if (tableDef.isHideRightColumn()) {
            Element rightcol = DOM.getElementById("rightcol");
            if (rightcol != null) {
                rightcol.getStyle().setDisplay(Style.Display.NONE);
            }
            Element centercolinner = DOM.getElementById("centercolinner");
            /*if (centercolinner != null) {
                centercolinner.getStyle().setWidth(660, Style.Unit.PX);
            }*/

            // From the 'beginning', in the original js-script, this where done:
            //List<Element> docLists = ElementUtil.findByCssClass(RootPanel.getBodyElement(), "doc-list");
            /*for (Element docList : docLists) {
                docList.getStyle().setWidth(100, Style.Unit.PC);
            }*/
        }
    }

    private void fetch(final TableDef tableDef) {
        try {
            fetchImpl(tableDef);
        } catch (Exception e) {
            Util.log(e);
            fetchNext();
        }
    }

    private void fetchImpl(final TableDef tableDef) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        panel.add(new Image(images.loading()));
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();

        //Util.log("First fetch for feed " + tableDef.getFeedId() + ", batchSize " + batchSize);

        requestBuilder.requestObject(Util.getServiceUrl(tableDef, 0, batchSize), new AsyncCallback<JsArray<JavaScriptObject>>() {

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
                //Display displayTable = new Display(tableDef, entries);
                Display displayTable = new Display(tableDef, new EventedList<Entry>());
                panel.add(displayTable);
                if (tableDef.getOnStartJsCallback() != null && !"".equals(tableDef.getOnStartJsCallback())) {
                    eval(tableDef.getOnStartJsCallback());
                }
                displayTable.getData().addAll(entries);

                if (areThereMoore) {
                    fetch(tableDef, displayTable, batchSize);
                } else {
                    fetchNext();
                }
            }

        });
    }

    private native String eval(String javascript)
    /*-{
       return eval(javascript);
    }-*/;


    private boolean areThereMooreToFetch(List<Entry> entries) {
        /*
        if (tableDef.getMaxHitLimit() == 0) {
            return false;
        }
        if (entries.size() >= tableDef.getMaxHitLimit()) {
            entries.removeAll(entries.subList(tableDef.getMaxHitLimit(), entries.size()));
            return true;
        }
        return false;*/
        //Util.log("entries.size(): " + entries.size() + " batchSize: " + batchSize);
        return entries.size() == batchSize;
    }

    private void fetch(final TableDef tableDef, final Display displayTable, final int startAt) {
        final HTMLPanel panel = HTMLPanel.wrap(tableDef.getElement());
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        final int endAt = startAt + batchSize;
        //Util.log("endAt " + endAt + " startAt " + startAt + " batchSize " + batchSize);
        displayTable.displayAjaxLoading();

        requestBuilder.requestObject(Util.getServiceUrl(tableDef, startAt, endAt), new AsyncCallback<JsArray<JavaScriptObject>>() {
            @Override
            public void onFailure(Throwable caught) {
                Util.log(caught);
                displayTable.hideAjaxLoading();
            }

            @Override
            public void onSuccess(JsArray<JavaScriptObject> response) {
                List<Entry> entries = toEntries(response);
                //Util.log("Fetched count " + entries.size());
                displayTable.getData().addAll(entries);

                if (!whenOverFetchLimitThenTruncate(tableDef, displayTable.getData()) && areThereMooreToFetch(entries)) {
                    fetch(tableDef, displayTable, endAt);
                } else {
                    //Util.log("Stops getting for current feed, " + tableDef.getFeedId() + ", entries.size() " + entries.size());
                    //Util.log("displayTable.getElement().getChild(1).getChildCount(): " + displayTable.getElement().getChild(1).getChildCount());
                    fetchNext();
                }
                //displayTable.render();
                displayTable.hideAjaxLoading();
            }

        });
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
