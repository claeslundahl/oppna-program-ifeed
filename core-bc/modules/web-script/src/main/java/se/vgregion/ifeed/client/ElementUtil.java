package se.vgregion.ifeed.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import java.util.ArrayList;
import java.util.List;

public class ElementUtil {

    /**
     * Returns a List of Element objects that have the specified CSS class name.
     *
     * @param element   Element to start search from
     * @param className name of class to find
     * @return elements that matches the className.
     */
    public static List<Element> findByCssClass(Element element, String className) {
        List result = new ArrayList();
        try {
            //Util.log("Före findByCssClass");
            findByCssClassImpl(result, element, className);
            //Util.log("Efter findByCssClass");
        } catch (Exception e) {
            Util.log(e);
        }
        return result;
    }

    private static void findByCssClassImpl(List<Element> res, Element element, String className) {
        if (element == null) {
            return;
        }

        String c = element.getClassName();

        if (c != null) {
            //Util.log("c = '" + c + "'");
            String[] p = c.split("[' ']");
            for (int x = 0; x < p.length; x++) {
                if (p[x].equals(className)) {
                    res.add(element);
                }
            }
        }

        for (int i = 0; i < DOM.getChildCount(element); i++) {
            Element child = DOM.getChild(element, i);
            findByCssClassImpl(res, child, className);
        }
    }

    /**
     * Converts an element from the markup to a TableDef instance. The element looks something like this:
     * <code> <div
     * class="ifeedDocList"
     * columnes="dc.title|Titel|left|70,dcterms.audience|Titel|left|70"
     * fontSize="auto"
     * defaultsortcolumn="dc.title"
     * defaultsortorder="asc"
     * showTableHeader="yes"
     * linkOriginalDoc="no"
     * limit="0"
     * hiderightcolumn="yes"
     * feedid="3630394">
     * </div><noscript><iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/3630394/?by=dc.title&dir=asc' id='iframenoscript' name='iframenoscript' style='width: 100%; height: 400px' frameborder='0'>
     * </iframe>
     * </noscript> </code>
     *
     * @param from element that contains ifeed information to be packaged as a TableDef
     * @return the TableDef that packs information about the ifeed.
     */

    public static TableDef toTableDef(Element from) {
        try {
            TableDef tableDef = new TableDef();
            tableDef.setElement(from);
            tableDef.setFeedHome(Util.getIfeedHome(tableDef));
            tableDef.setDefaultSortColumn(from.getAttribute("defaultsortcolumn"));
            tableDef.setDefaultSortOrder(from.getAttribute("defaultsortorder"));
            tableDef.setFeedId(from.getAttribute("feedid"));
            tableDef.setFkIfeedId(toLongOrHashCode(tableDef.getFeedId()));

            tableDef.setFontSize(from.getAttribute("fontSize"));
            tableDef.setHideRightColumn("yes".equals(from.getAttribute("hiderightcolumn")));
            tableDef.setLinkOriginalDoc("yes".equals(from.getAttribute("linkoriginaldoc")));
            String limitText = from.getAttribute("limit");
            tableDef.setMaxHitLimit(limitText == null || limitText.isEmpty() ? 0 : Integer.parseInt(limitText));
            tableDef.createColumnDefs(from.getAttribute("columnes"));

            tableDef.setShowTableHeader(getBooleanValue(from, "showTableHeader"));
            tableDef.setHideRightColumn(getBooleanValue(from, "hiderightcolumn"));
            tableDef.setOnStartJsCallback(from.getAttribute("onStartJsCallback"));

            return tableDef;
        } catch (Exception e) {
            return null;
        }
    }

    private static long toLongOrHashCode(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            return value.hashCode();
        }
    }

    private static boolean getBooleanValue(Element from, String key) {
        String value = from.getAttribute(key);
        if (value == null && "".equals(value.trim())) {
            return false;
        }
        return "yes".equals(value);
    }

}