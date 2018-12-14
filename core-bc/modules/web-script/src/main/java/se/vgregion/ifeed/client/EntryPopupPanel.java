package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * A small popup display for entry details.
 */
public class EntryPopupPanel extends PopupPanel {

    private Entry entry;

    // FlexTable plate = new FlexTable();
    // HTMLPanel plate;

    /**
     * Constructs an instance. Doing all the layout at once.
     *
     * @param entry the data to render.
     */
    public EntryPopupPanel(final Entry entry) {
        super();
        this.entry = entry;

        /*SimplePanel sp = new SimplePanel();
        sp.getElement().getStyle().setBackgroundColor("#A9A9A9");
        sp.getElement().getStyle().setPadding(1, Style.Unit.PX);*/

        final LazyPanel lp = new LazyPanel() {
            @Override
            protected Widget createWidget() {
                final SimplePanel result = new SimplePanel();
                Invocer.fetchHtml(Starter.toMetadataUrl((String) entry.get("id")), new Invocer.Callback<String>() {
                    @Override
                    public void event(String s) {
                        result.clear();
                        result.add(new HTMLPanel(
                                "<style type=\"text/css\">\n" +
                                        "            #table-container table.ifeed-metadata-table {\n" +
                                        "                width: 400px;\n" +
                                        "            }\n" +
                                        "\n" +
                                        "            #table-container table.ifeed-metadata-table td {\n" +
                                        "\n" +
                                        "                /* These are technically the same, but use both */\n" +
                                        "                overflow-wrap: break-word;\n" +
                                        "                word-wrap: break-word;\n" +
                                        "\n" +
                                        "                -ms-word-break: break-all;\n" +
                                        "                /* This is the dangerous one in WebKit, as it breaks things wherever */\n" +
                                        "                word-break: break-all;\n" +
                                        "                /* Instead use this non-standard one: */\n" +
                                        "                word-break: break-word;\n" +
                                        "\n" +
                                        "                /* Adds a hyphen where the word breaks, if supported (No Blink) */\n" +
                                        "                -ms-hyphens: auto;\n" +
                                        "                -moz-hyphens: auto;\n" +
                                        "                -webkit-hyphens: auto;\n" +
                                        "                hyphens: auto;\n" +
                                        "\n" +
                                        "            }\n" +
                                        "</style>" +
                                        s));
                    }
                });
                result.setWidth("500px");
                return result;
            }
        };
        add(lp);

        /*sp.add(lp);*/

        addDomHandler(
                new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        try {
                            hide();
                        } catch (Exception e) {
                            Window.alert("" + e);
                        }
                    }
                },
                MouseOutEvent.getType()
        );

        /*
        addDomHandler(event -> {
            try {
                hide();
            } catch (Exception e) {
                Window.alert("" + e);
            }
        }, MouseOutEvent.getType());
        */

      // add(sp);
    }

    /*private void addLabelAndDocumentMeta(String explainingText, String keyToGetWithFromDocument, int row) {
        keyToGetWithFromDocument = keyToGetWithFromDocument.toLowerCase();
        String propertyValue = Util.formatValueForDisplay(entry, keyToGetWithFromDocument) + "";
        if (propertyValue != null && !propertyValue.trim().isEmpty() && !"undefined".equals(propertyValue)) {
            plate.setText(row, 0, explainingText + ": ");
            plate.getFlexCellFormatter().getElement(row, 0).getStyle().setWidth(30, Style.Unit.PC);
            plate.getFlexCellFormatter().getElement(row, 0).getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
            plate.setText(row, 1, propertyValue);
            plate.getFlexCellFormatter().getElement(row, 1).getStyle().setWidth(70, Style.Unit.PC);
        }
    }*/

}
