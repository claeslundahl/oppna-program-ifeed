package se.vgregion.ifeed.client;

/**
 * Pattern for an object that can get data by name, lika a map.
 */
public interface HasGetter {

    /**
     * Getter to access contained information.
     * @param key name of data.
     * @return the value, as a string, of the searched after data.
     */
    String get(Object key);

}
