package se.vgregion.ifeed.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by clalu4 on 2014-03-14.
 */
public final class Entry extends JavaScriptObject implements HasGetter {

    protected Entry() {

    }

    public native String get(Object key) /*-{
      return this[key] + '';
    }-*/;

    public native Object getAsObject(Object key) /*-{
      return this[key];
    }-*/;

    public native void put(Object key, Object value) /*-{
      this[key] = value;
    }-*/;

}
