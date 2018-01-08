package se.vgregion.ifeed.client;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.sun.corba.se.impl.orbutil.ObjectUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Starting point for the web-script. It delas with loading information from the page it is loaded on and then to
 * get data from the server, then rendering that data on the client.
 * <p>
 * The feeds, if several on the page, is rendered one after the other. Each rendering is done after an asyncronous call
 * to the server. If the sice of the feed is larger then the batch-size (se value in the code), then several calls are
 * made for that feed. This division is made so that the client browser not will be that likely to frees for longer
 * moments.
 */
public class Main implements EntryPoint {

    public static native void changesForOldBrowser() /*-{
        if (!Array.prototype.slice) {
          Array.prototype.slice = function(begin, end) {
            var r = [];
            for(var i = begin; i < end; i++)
              r[i] = this[i];
            return r;
          }
        }
        if (!Array.prototype.push) {
          $win.alert('Adding the push!');
          Array.prototype.push = function(first) {
            for (var i = 0; i < arguments.length; i++)
              this[this.length] = arguments[i];
            return arguments.length;
          }
        }
    }-*/;

    @Override
    public void onModuleLoad() {
        changesForOldBrowser();
        Starter.init();
        String s = "<td class=\"ifeed-link-td ifeed-field-title\" style=\"text-align: left; width: 70pc;\">\n" +
            "  <a class=\"gwt-Anchor\" href=\"https://alfresco.vgregion.se/alfresco/service/vgr/storage/node/content/workspace/SpacesStore/1d363e65-3c34-4b38-8340-eb30b73ddb88?a=false&amp;guest=true\"\n" +
            "            target=\"_blank\">Minnesanteckningar DKR Skaraborg 140321\n" +
            "  </a>\n" +
            "</td>";
    }

}
