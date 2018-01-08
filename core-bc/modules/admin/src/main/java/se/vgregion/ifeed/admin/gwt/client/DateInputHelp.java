package se.vgregion.ifeed.admin.gwt.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.datepicker.client.DatePicker;
import jsinterop.annotations.JsType;

import java.util.*;


/**
 * Created by clalu4 on 2016-11-10.
 */
@JsType(namespace = "se_vgregion_ifeed_client", name = "DateInputHelp")
public class DateInputHelp {

    private static List<Element> getElementsByCssSelector(String selectors) {
        NodeList<Element> result = querySelectorAll(selectors);
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0, j = result.getLength(); i < j; i++) {
            elements.add((Element) result.getItem(i).cast());
        }
        return elements;
    }

    private static final native NodeList<Element> querySelectorAll(String selectors) /*-{
        return $doc.querySelectorAll(selectors);
    }-*/;

    public static void putDatePicker(String ontoElementsByThisCssSelector) {
        final List<Element> elements = getElementsByCssSelector(ontoElementsByThisCssSelector);
        for (Element element : elements) {
            final TextBox box = TextBox.wrap(element);
            box.addKeyPressHandler(new SupressKeyDownHandler("0123456789+-"));

            box.addFocusHandler(new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    popupChooser(box);
                }
            });
            box.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    popupChooser(box);
                }
            });
        }
    }

    private static void popupChooser(final TextBox box) {
        final PopupPanel popupPanel = new PopupPanel(true);
        final DatePicker datePicker = new DatePicker();  // Have to make final to use in inner method
        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event1) {
                Date date = event1.getValue();
                box.setText(DateTimeFormat.getFormat("yyyy-MM-dd").format(date));
                popupPanel.hide();
            }
        });
        popupPanel.setWidget(datePicker);
        int x = box.getElement().getAbsoluteLeft();
        int y = box.getElement().getAbsoluteBottom();
        popupPanel.setPopupPosition(x, y);
        popupPanel.show();
    }


    public static class SupressKeyDownHandler implements KeyPressHandler {

        private final Set<Character> approvedChars = new HashSet<Character>();

        public SupressKeyDownHandler(String pattern) {
            for (byte b : pattern.getBytes()) {
                approvedChars.add((char) b);
            }
        }

        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (!approvedChars.contains(event.getCharCode())) {
                ((ValueBoxBase) event.getSource()).cancelKey();
            }
        }
    }

}
