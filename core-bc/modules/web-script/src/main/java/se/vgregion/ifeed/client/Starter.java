package se.vgregion.ifeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import jsinterop.annotations.JsMethod;

import java.util.*;

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

  @JsMethod
  public static void init() {
    NodeList<Element> elements = querySelectorAll(".ifeedDocList");
    List<IfeedTag> tags = new ArrayList<>();
    for (int i = 0, j = elements.getLength(); i < j; i++) {
      Element element = elements.getItem(i);
      IfeedTag feed = new IfeedTag(element);
      feed.index = i;
      tags.add(feed);
      fetchAndRender(feed);
    }
  }

  public static String getFeedHome() {
    NodeList<Element> ifeeData2 = querySelectorAll("#ifeed-data2");
    String url; // = querySelectorAll("#ifeed-data2").getItem(0).getInnerText().trim();
    if (ifeeData2 != null && ifeeData2.getLength() > 0) {
      url = ifeeData2.getItem(0).getInnerText().trim();
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

  private static void sort(List<Entry> those, String byThatKey, String ascOrDesc) {
    Comparator<? super Entry> sorter = (Comparator<Entry>) (o1, o2) -> {
      String v1 = o1.get(byThatKey);
      String v2 = o2.get(byThatKey);
      if (v1 == null) v1 = "";
      if (v2 == null) v2 = "";
      return v1.toLowerCase().compareTo(v2.toLowerCase());
    };
    if ("asc".equals(ascOrDesc)) {
      those.sort(sorter);
    } else {
      those.sort((o1, o2) -> -sorter.compare(o1, o2));
    }
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
          + "&f=" + Util.join(fieldNames, "&f=");


      final HTMLPanel place = HTMLPanel.wrap(putResultsHere.element);
      place.clear();
      Image wait = new Image(images.ajaxLoader());
      place.add(wait);

      if (!putResultsHere.element.hasAttribute("doing-ajax-call")) {
        putResultsHere.element.setAttribute("data-url", url);
        if (putResultsHere.usepost != null && "yes".equals(putResultsHere.usepost)) {
          url += "&usePost";
        }
        Invocer.fetchFeedByJsonpCall(url, entries -> {
          try {
            if (putResultsHere.element.hasChildNodes()) {
              putResultsHere.element.setInnerHTML("");
            }
            putResultsHere.element.setAttribute("doing-ajax-call", "true");

            sort(entries, putResultsHere.defaultsortcolumn, putResultsHere.defaultsortorder);
            for (Entry extraSortColumn : putResultsHere.extraSortColumns) {
              sort(entries, extraSortColumn.get("name"), extraSortColumn.get("direction"));
            }

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
    NodeList<Element> placeHolders = querySelectorAll(".ifeed-count-" + tag.index);
    if (placeHolders != null) {
      for (int i = 0, j = placeHolders.getLength(); i < j; i++) {
        Element place = placeHolders.getItem(i);
        place.setInnerHTML(tag.fetchedData.size() + "");
      }
    }
  }

  public static void addHeading(FlexTable impl, IfeedTag tableDef) {
    try {
      impl.setText(0, 0, " ");
      int c = 1;
      final HTMLPanel place = HTMLPanel.wrap(tableDef.element);
      for (final IfeedTag.Column cd : tableDef.columns) {
        final Anchor tb = new Anchor(cd.title);
        tb.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        tb.getElement().getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);
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

        FlowPanel hp = new FlowPanel();
        hp.add(tb);
        if (tableDef.defaultsortcolumn.equals(cd.fieldId)) {
          hp.add(new Image(tableDef.defaultsortorder.equals("asc") ? images.sortasc() : images.sortdesc()));
        }
        impl.setWidget(0, c, hp);
        impl.getFlexCellFormatter().addStyleName(0, c, "ifeed-head-td");
        c++;
      }
    } catch (Exception e) {
      Window.alert("addHeading error");
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
    impl.getFlexCellFormatter().addStyleName(row, c, "ifeed-link-td");
    impl.getFlexCellFormatter().addStyleName(row, c, nameToCssClass(first.fieldId));
    c++;

    for (int i = 1; i < columns.size(); i++) {
      IfeedTag.Column cd = columns.get(i);
      String text = Util.formatValueForDisplay(item, cd.fieldId);
      impl.setText(row, c, text);
      impl.getFlexCellFormatter().addStyleName(row, c, "ifeed-td");
      impl.getFlexCellFormatter().addStyleName(row, c, nameToCssClass(cd.fieldId));
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
      return "";
    }
    return "ifeed-field-" + name.replace('.', '-');
  }

  private static boolean isBlanc(String s) {
    if (s == null) {
      return true;
    }
    return s.trim().equals("");
  }

  private static final Widget makeInfoCell(final Entry entry) {
    final HorizontalPanel hp = new HorizontalPanel();
    Image icon = new Image(images.information());
    icon.addClickHandler(clickEvent -> Window.open(urlToMetaData(entry), "_blank", ""));
    hp.add(icon);

    icon.addDomHandler(mouseOverEvent -> {
      final EntryPopupPanel info = new EntryPopupPanel(entry);
      info.setPopupPosition(hp.getAbsoluteLeft() + 15, hp.getAbsoluteTop() + 15);
      info.show();
      entry.put("EntryPopupPanel", info);
    }, MouseOverEvent.getType());

    icon.addDomHandler(mouseOutEvent -> {
      EntryPopupPanel epp = (EntryPopupPanel) entry.getAsObject("EntryPopupPanel");
      if (epp != null)
        epp.hide();
    }, MouseOutEvent.getType());

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
    String result = getFeedHome() + "/iFeed-web/documents/" + entry.get("dc.identifier.documentid") + "/metadata";
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


  public static final native NodeList<Element> querySelectorAll(String selectors) /*-{
        return $doc.querySelectorAll(selectors);
    }-*/;

}
