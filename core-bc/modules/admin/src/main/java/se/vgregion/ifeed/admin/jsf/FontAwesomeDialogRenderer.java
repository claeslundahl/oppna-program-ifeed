package se.vgregion.ifeed.admin.jsf;

import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.dialog.DialogRenderer;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

/**
 * @author Patrik Björk
 */
public class FontAwesomeDialogRenderer extends DialogRenderer {
    @Override
    protected void encodeIcon(FacesContext context, String anchorClass, String iconClass) throws IOException {

        if (Dialog.TITLE_BAR_CLOSE_CLASS.equals(anchorClass)) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("a", null);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("class", anchorClass, null);

            writer.startElement("i", null);
            writer.writeAttribute("class", "fa fa-remove", null);
            writer.endElement("i");

            writer.endElement("a");
        } else {
            super.encodeIcon(context, anchorClass, iconClass);
        }
    }

}