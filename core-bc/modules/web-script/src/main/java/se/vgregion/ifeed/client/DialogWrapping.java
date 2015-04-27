package se.vgregion.ifeed.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class DialogWrapping extends DialogBox {

    public DialogWrapping(Element element, String caption) {
        HTML html = HTML.wrap(element);
        // Set the dialog box's caption.
        setText(caption);

        setAnimationEnabled(false);
        // Enable glass background.
        setGlassEnabled(true);
        add(html);
        center();

        // DialogBox is a SimplePanel, so you have to set its widget property to
        // whatever you want its contents to be.
        /*Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DialogWrapping.this.hide();
            }
        });
        setWidget(ok);*/
    }
  }