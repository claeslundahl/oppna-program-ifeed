package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by clalu4 on 2015-05-20.
 */
public class AnchorPostUtil {

    final static Element body = RootPanel.getBodyElement();

    public static void makeLinksPostInsteadOfGetData() {
        List<Element> elements = ElementUtil.findByCssClass(body, "link-as-post-form");

        for (Element element : elements) {
            transform(element);
        }
    }

    private static void transform(Element element) {
        AnchorElement anchor = AnchorElement.as(element);
        Anchor a = Anchor.wrap(anchor);
        String javascriptReturnFalse = "javascript:return false;";
        if (javascriptReturnFalse.equals(anchor.getHref())) {
            return;
        }

        final FormPanel form = new FormPanel();
        VerticalPanel vp = new VerticalPanel();
        form.add(vp);
        form.setMethod("post");
        form.setVisible(false);
        // Assume we have parameters - the ? char is present
        String[] urlAndParams = anchor.getHref().split("\\?");
        form.setAction(urlAndParams[0]);
        form.getElement().setAttribute("target", "_blank");
        try {
            form.setEncoding(anchor.getType());
        } catch (Exception e) {
            // In some ie browsers this seems to fail.
        }
        String[] params = urlAndParams[1].split("\\&");
        for (String nameValue : params) {
            String[] nameValuePair = nameValue.split("\\=");
            final TextBox tb = new TextBox();
            tb.setName(nameValuePair[0]);
            if (nameValuePair[1] != null && !nameValuePair[1].isEmpty()) {
                tb.setValue(URL.decode(nameValuePair[1]));
            }
            vp.add(tb);
        }
        anchor.setHref(javascriptReturnFalse);

        a.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HTMLPanel panel = HTMLPanel.wrap(body);
                panel.add(form);
                form.submit();
                panel.remove(form);
            }
        });
    }

}
