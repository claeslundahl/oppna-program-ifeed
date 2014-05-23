package se.vgregion.ifeed.viewer;

/**
 * Created by clalu4 on 2014-04-24.
 */
public class Util {

    private static ThreadLocal localValue = new ThreadLocal();

    public static void setLocalValue(Object value) {
        localValue.set(value);
    }

    public static Object getLocalValue() {
        return localValue.get();
    }

}
