package se.vgregion.ifeed.viewer;

/**
 * Created by clalu4 on 2015-04-22.
 */
public class Util {

    private final static ThreadLocal<Object> localValue = new ThreadLocal<Object>();

    public static Object getLocalValue() {
        return localValue.get();
    }

    public static void setLocalValue(Object value) {
        localValue.set(value);
    }

}
