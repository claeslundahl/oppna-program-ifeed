package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.Element;

import java.util.ArrayList;
import java.util.List;

public class IfeedTag {

    /**
     * <div class="ifeedDocList" columnes="dc.title|Titel|left|100%" fontsize="auto" defaultsortcolumn="dc.title"
     * defaultsortorder="asc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="4565326">
     * </div>
     */

    public String ifeedDocList, columnes, fontsize, defaultsortcolumn, defaultsortorder, showtableheader, linkoriginaldoc, limit,
            hiderightcolumn, feedid, usepost;

    public final List<Column> columns = new ArrayList<>();

    public final Element element;

    public List<Entry> fetchedData;
    public int index;

    public IfeedTag(Element element) {
        this.element = element;
        ifeedDocList = element.getAttribute("ifeedDocList");
        columnes = element.getAttribute("columnes");
        defaultsortcolumn = element.getAttribute("defaultsortcolumn");
        defaultsortorder = element.getAttribute("defaultsortorder");
        showtableheader = element.getAttribute("showtableheader");
        linkoriginaldoc = element.getAttribute("linkoriginaldoc");
        hiderightcolumn = element.getAttribute("hiderightcolumn");
        usepost = element.getAttribute("usepost");
        feedid = element.getAttribute("feedid");
        fontsize = element.getAttribute("fontsize");
        limit = element.getAttribute("limit");

        String[] columnesParts = columnes.split("[',']");
        for (String cp : columnesParts) {
            columns.add(new Column(cp));
        }
    }

    @Override
    public String toString() {
        return "IfeedTag{" +
                "ifeedDocList='" + ifeedDocList + '\'' +
                ", columnes='" + columnes + '\'' +
                ", fontsize='" + fontsize + '\'' +
                ", defaultsortcolumn='" + defaultsortcolumn + '\'' +
                ", defaultsortorder='" + defaultsortorder + '\'' +
                ", showtableheader='" + showtableheader + '\'' +
                ", linkoriginaldoc='" + linkoriginaldoc + '\'' +
                ", limit='" + limit + '\'' +
                ", hiderightcolumn='" + hiderightcolumn + '\'' +
                ", feedid='" + feedid + '\'' +
                '}';
    }

    /**
     * columnes="dc.title|Titel|left|100%"
     */
    public class Column {

        public String fieldId, title, alignment, width;

        public Column(String input) {
            String[] parts = input.split("['|']");
            fieldId = parts[0];
            title = parts[1];
            alignment = parts[2];
            width = parts[3];
        }

    }



}