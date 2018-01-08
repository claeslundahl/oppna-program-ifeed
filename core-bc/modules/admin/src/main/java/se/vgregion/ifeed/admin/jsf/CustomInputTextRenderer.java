package se.vgregion.ifeed.admin.jsf;

/**
 * Created by patrik on 2014-08-27.
 */

import com.sun.faces.renderkit.html_basic.TextRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;

/**
 * A simple renderer for h:inputText
 * which allows to add some custom attributes.
 */
public class CustomInputTextRenderer extends TextRenderer {

    private static final String[] attributes = new String[]{
            "placeholder", "type", "autofocus"
    };

    @Override
    protected void getEndTextToRender(FacesContext context,
                                      UIComponent component,
                                      String currentValue) throws IOException {
        final ResponseWriter originalResponseWriter = context.getResponseWriter();

        context.setResponseWriter(new ResponseWriterWrapper() {

            @Override
            public ResponseWriter getWrapped() {
                return originalResponseWriter;
            }

            @Override
            public void startElement(String name, UIComponent component) throws IOException {
                super.startElement(name, component);

                if ("input".equalsIgnoreCase(name)) {
                    for (String attr : attributes) {
                        final String value = (String) component.getAttributes().get(attr);
                        if (value != null) {
                            super.writeAttribute(attr, value, attr);
                        }
                    }
                }
            }
        });

        super.getEndTextToRender(context, component, currentValue);
        context.setResponseWriter(originalResponseWriter); // Restore original writer.
    }

}

