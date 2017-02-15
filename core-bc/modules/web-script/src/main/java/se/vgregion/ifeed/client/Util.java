package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

import java.util.*;

/**
 * A collection of utility functions to be used inside the gui. Handy to have this separately so that it can be run in
 * unit-tests.
 */
public class Util {

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
     * Formats a time stamp to something more friendly to the swedish-reading user.
     *
     * @param asText the time as a textual time-stamp.
     * @return A formatted version of the input or empty string if it turned out to be null.
     */
    public static String timeStampTodate(String asText) {
        if (asText != null && !"".equals(asText.trim()) && !"undefined".equals(asText)) {
            if (asText.contains("T")) {
                return asText.substring(0, asText.indexOf("T"));
            } else {
                return asText;
            }
        } else {
            return "";
        }
    }

    /**
     * General formatting of values to display in the gui.
     *
     * @param entry the entry in wich the value resides.
     * @param key   name of the value inside the entry.
     * @return value of the entry formatted to be viewed by a person accustomed reading swedish.
     */
    public static String formatValueForDisplay(HasGetter entry, String key) {
        if (timeStampFieldNames.contains(key)) {
            return timeStampTodate(entry.get(key));
        }
        return entry.get(key);
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

    /**
     * Logs with console.log (if that is present in the browsers window-object.
     *
     * @param message
     * @return true if the logging could be performed (if there where a console.log).
     */
    native public static boolean log(Object message) /*-{
      if (window['console']) {
        console.log(message);
        return true;
      } else {
        return false;
      }
    }-*/;

    public static String getGetUrl(TableDef tableDef, int startBy, int endBy) {
        String url = getRequestData(tableDef, startBy, endBy);
        url = getIfeedHome(tableDef) + "?" + url;

        List<Element> skipCache = ElementUtil.findByCssClass((Element) Document.get().getDocumentElement(), "skip-cache");
        if (skipCache != null && skipCache.size() == 1) {
            Element item = skipCache.get(0);
            if (item.getInnerHTML().trim().equals("true")) {
                url = url + "&skip-cache=true";
            }
        }
        return url;
    }

    public static String getRequestData(TableDef tableDef, int startBy, int endBy) {
        return getRequestData(tableDef, startBy, endBy, true);
    }

    public static String getRequestData(TableDef tableDef, int startBy, int endBy, boolean encodeInstance) {
        String instance = tableDef.getFeedId();
        if (encodeInstance) {
            instance = URL.encodeQueryString(instance);
        }

        String url = "instance="
                + instance + "&by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy + "&fromPage="
                + UriUtils.encode(Window.Location.getProtocol() + "//"
                + Window.Location.getHostName() + ":"
                + Window.Location.getPort()
                + Window.Location.getPath());
        return url;
    }

    /**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param list    the different strings to be concatenated
     * @return as string product of the parameters.
     */
    public static String join(List<?> list, String junctor) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0) + "";
        }

        for (int i = 0, j = list.size() - 1; i < j; i++) {
            sb.append(list.get(i));
            sb.append(junctor);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }

    public static String getIfeedHome(TableDef tableDef) {
        String url = "";
        Element element = tableDef.getElement();
        com.google.gwt.dom.client.Element sibling = element.getNextSiblingElement();

        Element dataUrl = DOM.getElementById("ifeed-data");
        if (dataUrl != null) {
            url = dataUrl.getAttribute("location");
        } else {
            Element dataUrl2 = DOM.getElementById("ifeed-data2");
            if (dataUrl2 != null) {
                url = dataUrl2.getInnerText().trim();
            } else {
            }
        }

        if (url == null || "".equals(url)) {
            url = "http://ifeed.vgregion.se";
        }
        return url + "/iFeed-web/meta.json";
    }

    public static native int localeCompare(String a, String b)  /*-{
      return a.toLowerCase().localeCompare(b.toLowerCase());
    }-*/;

}
