package se.vgregion.ifeed.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay (see gwt references) class to wrap information about documents comming in the jsonp service.
 */
public final class Entry extends JavaScriptObject implements HasGetter {

    protected Entry() {

    }

    /**
     * Gets a property from the js-object.
     * @param key name of the property to get.
     * @return value of the property as text.
     */
    public native String get(Object key) /*-{
      return this[key] + '';
    }-*/;

    /**
     * Gets a property from the js-object.
     * @param key name of the property to get.
     * @return value of the property as an object.
     */
    public native Object getAsObject(Object key) /*-{
      return this[key];
    }-*/;

    /**
     * Puts information inside the mapped js-object.
     * @param key name of the property to set.
     * @param value new value for the property in the js-object.
     */
    public native void put(Object key, Object value) /*-{
      this[key] = value;
    }-*/;

}
