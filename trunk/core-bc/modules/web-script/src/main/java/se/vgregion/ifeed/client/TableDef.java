package se.vgregion.ifeed.client;

import com.google.gwt.user.client.Element;
import se.vgregion.ifeed.shared.DynamicTableDef;

/**
 * Designed to hold information about an ifeed - mirroring the textual description on the element.
 *
 * An sample data could be:
 * <code>
 * <div
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
 * </noscript>
 * </code>
 */
public class TableDef extends DynamicTableDef {

    private Element element;
    private String onStartJsCallback;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void setOnStartJsCallback(String onStartJsCallback) {
        this.onStartJsCallback = onStartJsCallback;
    }

    public String getOnStartJsCallback() {
        return onStartJsCallback;
    }
}
