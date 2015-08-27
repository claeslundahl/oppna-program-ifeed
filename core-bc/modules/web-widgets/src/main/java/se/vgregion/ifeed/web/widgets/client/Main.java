package se.vgregion.ifeed.web.widgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;

/**
 * Created by clalu4 on 2015-08-25.
 */
public class Main implements EntryPoint {

    @Override
    public void onModuleLoad() {

    }

    public void old() {

        exportStaticMethod();
        Element data = Document.get().getElementById("organizationTreeData");
        JsArray<Org> items = toOrgs(data.getInnerHTML());

        for (int i = 0; i < items.length(); i++) {
            Org item = items.get(i);

        }

        Tree tree = new Tree();
        HTMLPanel place = HTMLPanel.wrap(data);
        place.add(tree);
    }

    //Method to be called from javascript, could be in any other class too
    public static int computeLoanInterest(int amt, float interestRate,
                                          int term) {
        return -1;
    }

    //This method should be called during application startup
    public static native void exportStaticMethod() /*-{
       //the function named here will become available in javascript scope
       $wnd.computeLoanInterest =
          $entry(@se.vgregion.ifeed.web.widgets.client.Main::computeLoanInterest(IFI));
    }-*/;

    public static class Org extends JavaScriptObject {

        protected Org() {
            super();
        }

        public final native String getLabel() /*-{ return this.label; }-*/;

        public final native String getHsaIdentity()  /*-{ return this.hsaIdentity;  }-*/;


    }


    private static JsArray<Org> toOrgs(String json) {
        return JsonUtils.safeEval(json);
    }

}
