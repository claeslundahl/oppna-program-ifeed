package se.vgregion.ifeed.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.List;

public class Entry extends JavaScriptObject  {

    protected Entry() {
        super();
    }

    public final native String get(String key) /*-{
      //console.log(this);
     return this[key];
   }-*/;

    public final List<String> getKeys() {
        List<String> result = new ArrayList<>();
        JsArray array = getKeysImpl();

        for (int i = 0, j = array.length(); i < j; i++) {
            result.add(array.get(i).toString());
        }

        return result;
    }

    private final native JsArray getKeysImpl() /*-{
     var r = [];
     for(var k in this)
       r.push(k);
     return r;
   }-*/;

    /**
     * Gets a property from the js-object.
     * @param key name of the property to get.
     * @return value of the property as an object.
     */
    public final native Object getAsObject(Object key) /*-{
      return this[key];
    }-*/;

    /**
     * Puts information inside the mapped js-object.
     * @param key name of the property to set.
     * @param value new value for the property in the js-object.
     */
    public final native void put(Object key, Object value) /*-{
      this[key] = value;
    }-*/;
}
