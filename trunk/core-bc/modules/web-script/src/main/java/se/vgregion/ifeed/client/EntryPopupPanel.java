package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.*;

/**
 * A small popup display for entry details.
 */
public class EntryPopupPanel extends PopupPanel {

    private Entry entry;

    /**
     * Constructs an instance. Doing all the layout at once.
     * @param entry the data to render.
     */
    public EntryPopupPanel(Entry entry) {
        super();
        this.entry = entry;

        FlexTable plate = new FlexTable();
        plate.addStyleName("ifeed-popup-inf");

        SimplePanel sp = new SimplePanel();
        sp.getElement().getStyle().setBackgroundColor("#A9A9A9");
        sp.getElement().getStyle().setPadding(1, Style.Unit.PX);
        sp.add(plate);

        int row = 0;
        Label label = new Label("Titel: " + Util.formatValueForDisplay(entry, "dc.title"));
        label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        plate.setWidget(row, 0, label);
        plate.getFlexCellFormatter().getElement(row, 0).getStyle().setBackgroundColor("#A9A9A9");

        plate.getElement().getStyle().setMargin(2, Style.Unit.PX);
        plate.getFlexCellFormatter().setColSpan(row++, 0, 2);

        //plate.setWidget(row, 0, new HTML("<hr/>"));
        //plate.getFlexCellFormatter().setColSpan(row++, 0, 2);

        plate.setText(row, 0, "Beskrivning:");
        plate.getFlexCellFormatter().getElement(row,0).getStyle().setWidth(30, Style.Unit.PC);
        plate.setText(row, 1, Util.formatValueForDisplay(entry, "dc.description"));
        plate.getFlexCellFormatter().getElement(row++,1).getStyle().setWidth(70, Style.Unit.PC);
        plate.setText(row, 0, "Publicerat för enhet:");
        plate.setText(row++, 1, Util.formatValueForDisplay(entry, "dc.publisher.forunit"));
        plate.setText(row, 0, "Godkänt av:");
        plate.setText(row++, 1, Util.formatValueForDisplay(entry, "dc.contributor.acceptedby.role"));
        plate.setText(row, 0, "Typ av dokument:");
        plate.setText(row++, 1, Util.formatValueForDisplay(entry, "dc.type.document.structure"));

        plate.getElement().getStyle().setWidth(500d, Style.Unit.PX);
        plate.getElement().getStyle().setBackgroundColor("white");
        plate.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        plate.getElement().getStyle().setBorderColor("#A9A9A9");
        plate.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        addDomHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                hide();
            }
        }, MouseOutEvent.getType());

        add(sp);
    }

}
