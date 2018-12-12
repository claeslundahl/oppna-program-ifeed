package se.vgregion.ifeed.client;

import com.google.gwt.core.client.EntryPoint;

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
            Array.prototype.slice = function (begin, end) {
                var r = [];
                for (var i = begin; i < end; i++)
                    r[i] = this[i];
                return r;
            }
        }
        if (!Array.prototype.push) {
            $win.alert('Adding the push!');
            Array.prototype.push = function (first) {
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
    }

}
