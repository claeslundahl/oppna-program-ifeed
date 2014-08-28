package se.vgregion.ifeed.client;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    public static String getServiceUrl(TableDef tableDef, int startBy, int endBy) {
        // http://ifeed.vgregion.se/
        //String url = "http://127.0.0.1:8080/example-feed.jsonp.jsp";
        /*String url = "http://ifeed.vgregion.se/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder();*/


        /*String url = "http://127.0.0.1:8080/iFeed-web2/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy;
*/

        /*String url = "http://ifeed.vgregion.se/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy;*/

        /*
        String url = "/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy + "&fromPage="
                + UriUtils.encode(Window.Location.getProtocol() + "//"
                + Window.Location.getHostName() + ":"
                + Window.Location.getPort()
                + Window.Location.getPath());*/

        String url = "/iFeed-web/meta.json?instance="
                + tableDef.getFeedId() + "&by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy + "&fromPage="
                + UriUtils.encode(Window.Location.getProtocol() + "//"
                + Window.Location.getHostName() + ":"
                + Window.Location.getPort()
                + Window.Location.getPath());

        /*
        String url = "http://portalen-test.vgregion.se/iFeed-web/documentlists/"
                + tableDef.getFeedId() + "/metadata.json?by="
                + tableDef.getDefaultSortColumn()
                + "&dir=" + tableDef.getDefaultSortOrder()
                + "&startBy=" + startBy + "&endBy=" + endBy + "&fromPage="
                + UriUtils.encode(Window.Location.getProtocol() + "//"
                + Window.Location.getHostName() + ":"
                + Window.Location.getPort()
                + Window.Location.getPath());
        */
        /*
        Element element = tableDef.getElement();
        com.google.gwt.dom.client.Element sibling = element.getNextSiblingElement();
        if (sibling != null && sibling.getTagName().equalsIgnoreCase("noscript")) {
            com.google.gwt.dom.client.Element iframe = sibling.getFirstChildElement();
            if (iframe != null) {
                String host = iframe.getAttribute("src");
                host = host.substring(0, host.indexOf("/iFeed-web/"));
                url = host + url;
            } else {
                String host = sibling.getInnerText();
                host = host.substring(host.indexOf("src='") + 5, host.indexOf("/iFeed-web/"));
                url = host + url;
            }
        }*/
        url = getIfeedHome(tableDef) + url;
        return url;
    }

    public static String getIfeedHome(TableDef tableDef) {
        String url = "";
        Element element = tableDef.getElement();
        com.google.gwt.dom.client.Element sibling = element.getNextSiblingElement();

        /*if (sibling != null && sibling.getTagName().equalsIgnoreCase("noscript")) {
            com.google.gwt.dom.client.Element iframe = sibling.getFirstChildElement();
            if (iframe != null) {
                String host = iframe.getAttribute("src");
                host = host.substring(0, host.indexOf("/iFeed-web/"));
                url = host + url;
            } else {
                String host = sibling.getInnerText();
                if (host == null || "".equals(host.trim())) {
                    host = sibling.getInnerHTML();
                }
                host = host.substring(host.indexOf("src=") + 5, host.indexOf("/iFeed-web/"));
                url = host + url;
            }
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
        }*/

        Element dataUrl = DOM.getElementById("ifeed-data");
        if (dataUrl != null) {
            url = dataUrl.getAttribute("location");
        } else {
            Element dataUrl2 = DOM.getElementById("ifeed-data2");
            if (dataUrl2 != null) {
                url = dataUrl2.getInnerText().trim();
            }
        }

        if (url == null || "".equals(url)) {
            url = "http://ifeed.vgregion.se";
            //url = "http://vgas0565.vgregion.se:8081";
            //url = "http://loocalhost:8081";
        }
        //log("using url " + url);
        return url;
    }

}
