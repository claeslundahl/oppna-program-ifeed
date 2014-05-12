package se.vgregion.ifeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by clalu4 on 2014-05-09.
 */
public class DisplayTable extends Composite {

    FlexTable impl = new FlexTable();

    TableDef tableDef;
    private List<Entry> data;
    String currentSortColumn, currentSortOrder;

    private static Images images = GWT.create(Images.class);

    static Set<String> alignments = new HashSet<String>();

    static {
        Style.TextAlign[] var = Style.TextAlign.values();
        for (Style.TextAlign align : var) {
            alignments.add(align.toString().toLowerCase());
            alignments.add(align.toString().toUpperCase());
        }
    }

    public DisplayTable(final TableDef tableDef, final List<Entry> data) {
        initWidget(impl);
        this.tableDef = tableDef;
        this.data = data;
        this.currentSortColumn = tableDef.getDefaultSortColumn();
        this.currentSortOrder = tableDef.getDefaultSortOrder();
        render();
    }

    public static void showWaitCursor() {
        DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
    }

    public static void showDefaultCursor() {
        DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
    }

    void render() {
        impl.clear();
        if (!tableDef.getFontSize().equals("inherit") && !tableDef.getFontSize().equals("auto")) {
            try {
                double fontSize = Double.parseDouble(tableDef.getFontSize().toLowerCase().replace("px", ""));
                impl.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
            } catch (Exception e) {
                System.out.println("tableDef.getFontSize(): " + tableDef.getFontSize());
            }
        }
        int row = 0;
        List<ColumnDef> columns = tableDef.getColumnDefs();

        if (tableDef.isShowTableHeader()) {
            impl.setText(0, 0, " ");
            int c = 1;
            for (final ColumnDef cd : tableDef.getColumnDefs()) {
                final ToggleButton tb = new ToggleButton(cd.getLabel());
                tb.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                tb.getElement().getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);

                tb.setDown(cd.getName().equals(currentSortColumn));
                tb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                        showWaitCursor();
                        Timer timer = new Timer() {
                            @Override
                            public void run() {
                                currentSortColumn = cd.getName();
                                currentSortOrder = "asc".
                                        equals(currentSortOrder)
                                        ? "desc" : "asc";
                                render();
                                showDefaultCursor();
                            }
                        };
                        timer.schedule(100);
                    }
                });
                HorizontalPanel hp = new HorizontalPanel();
                hp.add(tb);
                if (currentSortColumn.equals(cd.getName())) {
                    hp.add(new Image(currentSortOrder.equals("asc") ? images.sortasc() : images.sortdesc()));
                }
                impl.setWidget(row, c, hp);
                c++;
            }
            row++;
        }

        List<Entry> sortedData = getSortedData();

        for (Entry data : sortedData) {
            int c = 0;
            impl.setWidget(row, c++, mkInfoCell(data));
            ColumnDef first = columns.get(0);

            Anchor anchor = new Anchor(
                    Util.formatValueForDisplay(data, first.getName()),
                    data.get(tableDef.isLinkOriginalDoc() ? "dc.identifier.native" : "url")
                );
            anchor.setTarget("_blank");

            impl.setWidget(row, c++, anchor);

            for (int i = 1; i < columns.size(); i++) {
                ColumnDef cd = columns.get(i);
                String text = Util.formatValueForDisplay(data, cd.getName());
                impl.setText(row, c, text);
                c++;
            }
            row++;
        }

        addColumnWidth();
        addTextAlgnmentToColumns();
    }

    void addColumnWidth() {
        List<ColumnDef> columns = tableDef.getColumnDefs();
        for (int i = 0; i < columns.size(); i++) {
            ColumnDef cd = columns.get(i);
            String width = cd.getWidth();
            if (!isBlanc(width)) {
                width = width.replace("%", "");
                impl.getFlexCellFormatter().getElement(0, i + 1).getStyle().setWidth(
                        Double.parseDouble(width),
                        Style.Unit.PC);
            }
        }
    }

    void addTextAlgnmentToColumns() {
        int c = 1;
        for (ColumnDef cd : tableDef.getColumnDefs()) {
            if (isBlanc(cd.getAlignment())) {
                continue;
            }
            String alignment = cd.getAlignment().toUpperCase();
            if (!isBlanc(alignment) && alignments.contains(alignment)) {
                for (int i = 0, j = impl.getRowCount(); i < j; i++) {
                    impl.getFlexCellFormatter().getElement(i, c).getStyle().setTextAlign(
                            Style.TextAlign.valueOf(alignment)
                    );
                }
            }
            c++;
        }
    }

    boolean isBlanc(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().equals("");
    }

    List<Entry> getSortedData() {
        MapOfLists<Entry> mapOfLists = new MapOfLists<Entry>();
        for (Entry entry : data) {
            mapOfLists.get(entry.get(currentSortColumn)).add(entry);
        }

        List<Entry> result = mapOfLists.allInOrder();
        if (currentSortOrder.equals("asc"))
            return result;

        Collections.reverse(result);
        return result;
    }

    Widget mkInfoCell(final Entry entry) {
        final HorizontalPanel hp = new HorizontalPanel();
        Image icon = new Image(images.information());
        icon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open(urlToMetaData(entry), "_blank", "");
            }
        });
        hp.add(icon);

        hp.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                final EntryPopupPanel info = new EntryPopupPanel(entry);
                info.setPopupPosition(hp.getAbsoluteLeft() + 15, hp.getAbsoluteTop() + 15);
                info.show();
                entry.put("EntryPopupPanel", info);
            }
        }, MouseOverEvent.getType());

        hp.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {
                EntryPopupPanel epp = (EntryPopupPanel) entry.getAsObject("EntryPopupPanel");
                epp.hide();
            }
        }, MouseOutEvent.getType());

        String validToKey = "dc.date.validto";
        if (Util.isTimeStampPassed(entry.get(validToKey))) {
            Image x = new Image(images.exclamation());
            x.setTitle("Dokumentet har gått ut: " + Util.formatValueForDisplay(entry, "dc.date.validto"));
            hp.add(x);
        }

        String validFromKey = "dc.date.validfrom";
        if (!Util.isTimeStampPassed(entry.get(validFromKey))) {
            Image x = new Image(images.exclamation());
            x.setTitle("Dokumentet börjar gälla: " + Util.formatValueForDisplay(entry, validFromKey));
            hp.add(x);
        }

        return hp;
    }

    public List<Entry> getData() {
        return data;
    }

    private String urlToMetaData(Entry entry) {
        String result = "http://ifeed.vgregion.se/iFeed-web/documents/" + entry.get("dc.identifier.documentid") + "/metadata";
        result = result.replace("workspace://SpacesStore/", "");
        return result;
    }

}
