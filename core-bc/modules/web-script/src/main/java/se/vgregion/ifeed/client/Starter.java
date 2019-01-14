package se.vgregion.ifeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import static se.vgregion.ifeed.client.Util.isUtcDate;

//import jsinterop.annotations.JsMethod;

/**
 * Created by clalu4 on 2017-03-01.
 */
public class Starter {

    static Set<String> alignments = new HashSet<String>();

    static {
        Style.TextAlign[] var = Style.TextAlign.values();
        for (Style.TextAlign align : var) {
            alignments.add(align.toString().toLowerCase());
            alignments.add(align.toString().toUpperCase());
        }
    }

    private static Images images = GWT.create(Images.class);

    private static final native void publicizeInit()/*-{
        $wnd.se = {};
        $wnd.se.vgregion = {};
        $wnd.se.vgregion.ifeed = {};
        $wnd.se.vgregion.ifeed.client = {};
        $wnd.se.vgregion.ifeed.client.Starter = {};
        $wnd.se.vgregion.ifeed.client.Starter.init = function () {
            @se.vgregion.ifeed.client.Starter::init()();
        }
    }-*/;

    //@JsMethod
    public static void init() {
        //NodeList<Element> elements = querySelectorAll(".ifeedDocList");
        JsArray<Element> elements = findElements("div", "className", "ifeedDocList");
        List<IfeedTag> tags = new ArrayList<>();
        for (int i = 0, j = elements.length(); i < j; i++) {
            Element element = elements.get(i);
            IfeedTag feed = new IfeedTag(element);
            feed.index = i;
            tags.add(feed);
            fetchAndRender(feed);
        }
        publicizeInit();
    }

    public static String getFeedHome() {
        String pathParameter = Window.Location.getParameter("ifeed-data2");
        if (pathParameter != null && !pathParameter.isEmpty()) {
            return pathParameter;
        }
        JsArray<Element> ifeeData2 = findElements("div", "id", "ifeed-data2");
        String url; // = querySelectorAll("#ifeed-data2").getItem(0).getInnerText().trim();
        if (ifeeData2 != null && ifeeData2.length() > 0) {
            url = ifeeData2.get(0).getInnerText().trim();
        } else {
            url = "https://ifeed.vgregion.se";
        }
        return url;
    }

    private static void sort(IfeedTag fetchedDataInsideThat) {
        List<Entry> those = fetchedDataInsideThat.fetchedData;
        String byThatKey = fetchedDataInsideThat.defaultsortcolumn;
        String ascOrDesc = fetchedDataInsideThat.defaultsortorder;
        sort(those, byThatKey, ascOrDesc);
    }

    private static String noNpe(String s) {
        return s + "";
    }

    private static void sort(final List<Entry> those, final String byThatKey, final String ascOrDesc) {
        final Comparator<? super Entry> sorter = new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                String v1 = o1.get(byThatKey);
                String v2 = o2.get(byThatKey);
                if (v1 == null) v1 = "";
                if (v2 == null) v2 = "";
                //return v1.toLowerCase().compareTo(v2.toLowerCase());
                return noNpe(v1).toLowerCase().compareTo(noNpe(v2).toLowerCase());
                //return noNpe(v1).compareTo(noNpe(v2));
            }
        };

        if ("asc".equals(ascOrDesc)) {
            Collections.sort(those, sorter);
            //those.sort(sorter);
        } else {
            Collections.sort(those, new Comparator<Entry>() {
                @Override
                public int compare(Entry o1, Entry o2) {
                    return -sorter.compare(o1, o2);
                }
            });
            // those.sort((o1, o2) -> -sorter.compare(o1, o2));
        }
    }

    public static String toMetadataUrl(String id, boolean popup) {
        id = id.replace("workspace://SpacesStore/", "");
        String url = getFeedHome();
        // https://ifeed.vgregion.se/iFeed-web/documents/b5e59d21-f1d6-40a6-8451-f98bc7efb29c/metadata
        url += (!url.endsWith("/") ? "/" : "")
                + "iFeed-web/documents/" + id + "/metadata";
        if (popup) url += "?type=tooltip";
        return url;
    }

    public static void fetchAndRender(final IfeedTag putResultsHere) {
        try {
            String url = getFeedHome();
            List<String> fieldNames = new ArrayList<>();
            for (IfeedTag.Column column : putResultsHere.columns) {
                fieldNames.add(column.fieldId);
            }
            fieldNames.add(putResultsHere.defaultsortcolumn);
            url += (!url.endsWith("/") ? "/" : "")
                    + "iFeed-web/meta.json?instance=" + putResultsHere.feedid
                    + "&f=" + Util.join(fieldNames, "&f=") + "&type=tooltip";


            final HTMLPanel place = HTMLPanel.wrap(putResultsHere.element);
            place.clear();
            Image wait = new Image(images.ajaxLoader());
            place.add(wait);

            if (!putResultsHere.element.hasAttribute("doing-ajax-call")) {
                putResultsHere.element.setAttribute("data-url", url);
                if (putResultsHere.usepost != null && "yes".equals(putResultsHere.usepost)) {
                    url += "&usePost";
                }
                Invocer.fetchFeedByJsonpCall(url, new Invocer.Callback<List<Entry>>() {
                    @Override
                    public void event(List<Entry> entries) {
                        {
                            try {
                                if (putResultsHere.element.hasChildNodes()) {
                                    putResultsHere.element.setInnerHTML("");
                                }
                                putResultsHere.element.setAttribute("doing-ajax-call", "true");

                                List<Entry> order = new ArrayList<Entry>(putResultsHere.extraSortColumns);
                                Collections.reverse(order);
                                for (Entry extraSortColumn : order) {
                                    sort(entries, extraSortColumn.get("name"), extraSortColumn.get("direction"));
                                }

                                sort(entries, putResultsHere.defaultsortcolumn, putResultsHere.defaultsortorder);

                                if (putResultsHere.limit != null && putResultsHere.limit.matches("^(-?)\\d+$")) {
                                    int limit = Integer.parseInt(putResultsHere.limit);
                                    if (limit > 0) {
                                        entries = entries.subList(0, Math.min(entries.size(), limit));
                                    }
                                }

                                putResultsHere.fetchedData = entries;

                                place.add(createTable(putResultsHere));
                                findAnySizePlaceholdersAndFillThem(putResultsHere);
                                putResultsHere.element.removeAttribute("doing-ajax-call");
                            } catch (Exception e) {
                                Util.log(e);
                            }
                        }
                    }
                });
            } else {
                Util.log("Not doing an ajax call for " + putResultsHere.feedid);
            }
        } catch (Exception e) {
            Util.log(e);
        }
    }

    public static FlexTable createTable(final IfeedTag putResultsHere) {
        FlexTable ft = new FlexTable();
        ft.addStyleName("doc-list");
        int c = 1, r = 0;
        if ("yes".equals(putResultsHere.showtableheader)) {
            addHeading(ft, putResultsHere);
            r++;
        }
        for (Entry entry : putResultsHere.fetchedData) {
            c = 0;
            for (IfeedTag.Column column : putResultsHere.columns) {
                each(entry, r, ft, putResultsHere);
                c++;
            }
            r++;
        }
        return ft;
    }

    public static void findAnySizePlaceholdersAndFillThem(IfeedTag tag) {
        // NodeList<Element> placeHolders = querySelectorAll(".ifeed-count-" + tag.index);
        JsArray<Element> placeHolders = findElements("span", "className", "ifeed-count-" + tag.index);
        if (placeHolders != null) {
            for (int i = 0, j = placeHolders.length(); i < j; i++) {
                Element place = placeHolders.get(i);
                place.setInnerHTML(tag.fetchedData.size() + "");
            }
        }
    }

    public static void addHeading(final FlexTable impl, final IfeedTag tableDef) {
        try {
            impl.setText(0, 0, " ");
            int c = 1;
            final HTMLPanel place = HTMLPanel.wrap(tableDef.element);
            for (final IfeedTag.Column cd : tableDef.columns) {
                final Anchor tb = new Anchor(cd.title);
                tb.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                tb.getElement().getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);

                tb.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        com.google.gwt.user.client.Timer timer = new Timer() {
                            @Override
                            public void run() {
                                tableDef.defaultsortorder = "asc".
                                        equals(tableDef.defaultsortorder) && tableDef.defaultsortcolumn.equals(cd.fieldId)
                                        ? "desc" : "asc";
                                tableDef.defaultsortcolumn = cd.fieldId;
                                place.clear();
                                place.getElement().setInnerHTML("");
                                sort(tableDef);
                                place.add(createTable(tableDef));
                            }
                        };
                        timer.schedule(100);
                    }
                });

        /*
        tb.addClickHandler(clickEvent -> {
          com.google.gwt.user.client.Timer timer = new com.google.gwt.user.client.Timer() {
            @Override
            public void run() {
              tableDef.defaultsortorder = "asc".
                  equals(tableDef.defaultsortorder) && tableDef.defaultsortcolumn.equals(cd.fieldId)
                  ? "desc" : "asc";
              tableDef.defaultsortcolumn = cd.fieldId;
              place.clear();
              place.getElement().setInnerHTML("");
              sort(tableDef);
              place.add(createTable(tableDef));
            }
          };
          timer.schedule(100);
        });
        */

                FlowPanel hp = new FlowPanel();
                hp.add(tb);
                if (tableDef.defaultsortcolumn.equals(cd.fieldId)) {
                    hp.add(new Image(tableDef.defaultsortorder.equals("asc") ? images.sortasc() : images.sortdesc()));
                }
                impl.setWidget(0, c, hp);
                //impl.getFlexCellFormatter().addStyleNameHere(0, c, "ifeed-head-td");
                addStyleNameHere("ifeed-head-td", impl.getFlexCellFormatter(), 0, c, 10);
                c++;
            }
        } catch (Exception e) {
            Window.alert("addHeading error");
        }
    }

    static void addStyleNameHere(String withName, FlexTable.FlexCellFormatter intoThat, int atRow, int andCellIndex, Object refNr) {
        try {
            intoThat.addStyleName(atRow, andCellIndex, withName);
        } catch (Exception e) {
            Window.alert("Error at addStyleNameHere with refNr " + refNr);
        }
    }

    public static void each(Entry item, final int row, FlexTable impl, IfeedTag ifeed) {
        List<IfeedTag.Column> columns = ifeed.columns;
        int c = 0;
        impl.setWidget(row, c, makeInfoCell(item));
        impl.getFlexCellFormatter().addStyleName(row, c++, "ifeed-info-td");
        IfeedTag.Column first = columns.get(0);

        Anchor anchor = new Anchor(
                Util.formatValueForDisplay(item, first.fieldId),
                item.get("yes".equals(ifeed.linkoriginaldoc) ? "dc.identifier.native" : "url")
        );

        anchor.setTarget("_blank");
        impl.setWidget(row, c, anchor);
        // impl.getFlexCellFormatter().addStyleNameHere(row, c, "ifeed-link-td");
        addStyleNameHere("ifeed-link-td", impl.getFlexCellFormatter(), row, c, 321);
        // impl.getFlexCellFormatter().addStyleNameHere(row, c, nameToCssClass(first.fieldId));
        addStyleNameHere(nameToCssClass(first.fieldId), impl.getFlexCellFormatter(), row, c, 123);
        c++;

        for (int i = 1; i < columns.size(); i++) {
            IfeedTag.Column cd = columns.get(i);
            String text = Util.formatValueForDisplay(item, cd.fieldId);
            impl.setText(row, c, text);
            impl.getFlexCellFormatter().addStyleName(row, c, "ifeed-td");
            // impl.getFlexCellFormatter().addStyleNameHere(row, c, nameToCssClass(cd.fieldId));
            String cssStyleName = nameToCssClass(cd.fieldId);
            addStyleNameHere(cssStyleName, impl.getFlexCellFormatter(), row, c, 10000 + i + " " + cssStyleName + " " + cd.fieldId);
            if (isUtcDate(item.get(cd.fieldId))) {
                impl.getFlexCellFormatter().getElement(row, c).getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
            }
            c++;
        }
        addTextAlignmentToColumn(impl, row, columns);
    }

    private static void addTextAlignmentToColumn(FlexTable ft, int row, List<IfeedTag.Column> columns) {
        int c = 1;
        for (IfeedTag.Column cd : columns) {
            if (isBlanc(cd.alignment)) {
                continue;
            }
            String alignment = cd.alignment.toUpperCase();
            if (!isBlanc(alignment) && alignments.contains(alignment)) {
                ft.getFlexCellFormatter().getElement(row, c).getStyle().setTextAlign(
                        Style.TextAlign.valueOf(alignment)
                );
            }
            c++;
        }
    }

    private static String nameToCssClass(String name) {
        if (isBlanc(name)) {
            return "ifeed-field-blank";
        }
        return "ifeed-field-" + name.replace('.', '-');
    }

    private static boolean isBlanc(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().equals("");
    }

    private static EntryPopupPanel currentInfo;

    private static final Widget makeInfoCell(final Entry entry) {
        final HorizontalPanel hp = new HorizontalPanel();
        Image icon = new Image(images.information());
        icon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(urlToMetaData(entry), "_blank", "");
            }
        });
        hp.add(icon);

        // Util.log("Before adding dom handler.");

        icon.addMouseOverHandler(new MouseOverHandler() {
                                     @Override
                                     public void onMouseOver(MouseOverEvent mouseOverEvent) {
                                         if (currentInfo != null) {
                                             currentInfo.hide();
                                         }
                                         final EntryPopupPanel info = currentInfo = new EntryPopupPanel(entry);
                                         // info.setPopupPosition(hp.getAbsoluteLeft() + 15, hp.getAbsoluteTop() + 15);
                                         // info.setSize("200px", "200px");
                                         // info.center();
                                         info.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                                             public void setPosition(int offsetWidth, int offsetHeight) {
                                                 /*int left = (Window.getClientWidth() - offsetWidth) / 3;
                                                 int top = (Window.getClientHeight() - offsetHeight) / 3;
                                                 info.setPopupPosition(left, top);
                                                 info.setPopupPosition(10, 10);*/
                                                 info.setPopupPosition(hp.getAbsoluteLeft() + 15, hp.getAbsoluteTop() + 15);
                                             }
                                         });
                                         entry.put("EntryPopupPanel", info);
                                         info.show();
                                         // Util.log("onMouseOver");
                                     }
                                 }
        );

        String validToKey = "dc.date.validto";
        String textDate = entry.get(validToKey);
        if (!isBlanc(textDate) && Util.isTimeStampPassed(textDate)) {
            Image x = new Image(images.exclamation());
            x.setTitle("Dokumentet har gått ut: " + Util.formatValueForDisplay(entry, "dc.date.validto"));
            hp.add(x);
        }

        String validFromKey = "dc.date.validfrom";
        textDate = entry.get(validFromKey);
        if (!isBlanc(textDate) && !Util.isTimeStampPassed(textDate)) {
            Image x = new Image(images.exclamation());
            x.setTitle("Dokumentet börjar gälla: " + Util.formatValueForDisplay(entry, validFromKey));
            hp.add(x);
        }

        return hp;
    }

    private static String urlToMetaData(Entry entry) {
        // String result = getFeedHome() + "/iFeed-web/documents/" + entry.get("dc.identifier.documentid") + "/metadata";
        String result = getFeedHome() + "/iFeed-web/documents/" + entry.get("id") + "/metadata";
        result = result.replace("meta.json/iFeed-web/", "");
        result = result.replace("workspace://SpacesStore/", "");
        return result;
    }

    static DateTimeFormat sdf = new DateTimeFormat("yyyy-MM-dd", new DefaultDateTimeFormatInfo()) {
    };

    private static Set<String> timeStampFieldNames = new HashSet<String>(
            Arrays.asList("dc.date.issued", "dc.date.validfrom", "dc.date.validto")
    );

    static String currentTextDate;

    static {
        currentTextDate = sdf.format(new Date());
    }

    /**
     * Finds out if the time of a time-stamp is passed or not. It compares that against that of the current time (or
     * rather the time when the script first initialized).
     *
     * @param timeStampAsText the time-stamp to be tested as text.
     * @return true if the time of the parameter is less than that of the current time. False is also returned if the
     * content is nothing.
     */
    public static boolean isTimeStampPassed(String timeStampAsText) {
        if (timeStampAsText == null || "".equals(timeStampAsText.trim())) {
            return false;
        }
        timeStampAsText = timeStampAsText.substring(0, Math.max(Math.min(timeStampAsText.length(), 10), 0));
        return timeStampAsText.compareTo(currentTextDate) <= 0;
    }

    // w00t! Generics work just fine with overlay types
    public static class JsArray<E extends JavaScriptObject> extends JavaScriptObject {
        protected JsArray() {
        }

        public final native int length() /*-{
            return this.length;
        }-*/;

        public final native E get(int i) /*-{
            return this[i];
        }-*/;
    }

    public static final native JsArray<Element> findElements(String tag, String key, String value) /*-{

        if (!Array.prototype.indexOf) {
            Array.prototype.indexOf = function (obj, start) {
                for (var i = (start || 0), j = this.length; i < j; i++) {
                    if (this[i] === obj) {
                        return i;
                    }
                }
                return -1;
            }
        }

        try {
            var all = $doc.getElementsByTagName(tag);
            var r = [];
            for (var i = 0; i < all.length; i++) {
                var element = all[i];
                if (element[key] != null && element[key] == value || element[key].split(" ").indexOf(value) > -1) {
                    r[r.length] = element;
                }
            }
            return r;
        } catch (e) {
            $wnd.alert("Problem i findElements " + e.message);
        }
    }-*/;

    // var all = document.getElementsByTagName("*");


}
