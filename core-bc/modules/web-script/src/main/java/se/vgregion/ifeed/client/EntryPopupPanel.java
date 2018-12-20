package se.vgregion.ifeed.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
        super(true);
        this.entry = entry;
        final VerticalPanel vp = new VerticalPanel();

        Invocer.fetchHtml(Starter.toMetadataUrl((String) entry.get("id")), new Invocer.Callback<String>() {
            @Override
            public void event(String s) {
                Util.log(s);
                vp.clear();
                s = s.replace("(autokomplettering)", "");
                vp.add(new HTMLPanel(
                        "<style type=\"text/css\">\n" +
                                ".popupContent {" +
                                "    width: 450px; " +
                                "    background-color: white;\n" +
                                "}" +
                                "            #table-container {\n" +
                                "                width: 400px; " +
                                "                background-color: white;\n" +
                                "    border: gray thin solid;\n" +
                                "    padding: 10px 10px;" +
                                "            }\n" +
                                "\n" +
                                "            #table-container table.ifeed-metadata-table td {\n" +
                                "                vertical-align: top; \n" +
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


        add(vp);

        addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {
                try {
                    hide();
                } catch (Exception e) {
                    Window.alert("" + e);
                }
            }

        }, MouseOutEvent.getType());

        // add(sp);
    }

}
