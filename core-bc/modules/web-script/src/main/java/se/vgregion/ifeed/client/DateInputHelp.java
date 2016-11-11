package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.datepicker.client.DatePicker;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

import java.util.*;

/**
 * Created by clalu4 on 2016-11-10.
 */
@JsType(namespace = "se_vgregion_ifeed_client", name = "DateInputHelp")
public class DateInputHelp {

    private static List<Element> getElementsByCssSelector(String selectors) {
        NodeList<Element> result = querySelectorAll(selectors);
        List<Element> elements = new ArrayList<>();
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

            box.addFocusHandler(event -> popupShooser(box));
            box.addClickHandler(event -> popupShooser(box));
        }
    }

    private static void popupShooser(final TextBox box) {
        final PopupPanel popupPanel = new PopupPanel(true);
        final DatePicker datePicker = new DatePicker();  // Have to make final to use in inner method
        datePicker.addValueChangeHandler(event1 -> {
            Date date = event1.getValue();
            box.setText(DateTimeFormat.getFormat("yyyy-MM-dd").format(date));
            popupPanel.hide();
        });
        popupPanel.setWidget(datePicker);
        int x = box.getElement().getAbsoluteLeft();
        int y = box.getElement().getAbsoluteBottom();
        popupPanel.setPopupPosition(x, y);
        popupPanel.show();
    }


    public static class SupressKeyDownHandler implements KeyPressHandler {

        private final Set<Character> approvedChars = new HashSet<>();

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
