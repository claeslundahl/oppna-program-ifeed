package se.vgregion.ifeed.client;

import com.google.gwt.user.client.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2014-03-14.
 * <p/>
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
 */
public class TableDef {

    private String fontSize;
    private String defaultSortColumn;
    private String defaultSortOrder;
    private String feedId;

    private int limit;

    private boolean showTableHeader;
    private boolean linkOriginalDoc;
    private boolean hideRightColumn;

    private final List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();

    private Element element;

    public void createColumnDefs(String fromText) {
        String[] fragments = fromText.split("[,]");
        for (String fragment : fragments) {
            ColumnDef columnDef = new ColumnDef();
            columnDef.parseAndSet(fragment);
            columnDefs.add(columnDef);
        }
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getDefaultSortColumn() {
        return defaultSortColumn;
    }

    public void setDefaultSortColumn(String defaultSortColumn) {
        this.defaultSortColumn = defaultSortColumn;
    }

    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(String defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isShowTableHeader() {
        return showTableHeader;
    }

    public void setShowTableHeader(boolean showTableHeader) {
        this.showTableHeader = showTableHeader;
    }

    public boolean isLinkOriginalDoc() {
        return linkOriginalDoc;
    }

    public void setLinkOriginalDoc(boolean linkOriginalDoc) {
        this.linkOriginalDoc = linkOriginalDoc;
    }

    public boolean isHideRightColumn() {
        return hideRightColumn;
    }

    public void setHideRightColumn(boolean hideRightColumn) {
        this.hideRightColumn = hideRightColumn;
    }

    public List<ColumnDef> getColumnDefs() {
        return columnDefs;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
