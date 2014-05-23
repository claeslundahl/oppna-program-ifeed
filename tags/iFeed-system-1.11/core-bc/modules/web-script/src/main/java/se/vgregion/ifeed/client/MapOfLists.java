package se.vgregion.ifeed.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Utility class that, among other things, ca be used to help sort objects.
 *
 * It is constructed to return lists of objects. If the mapping to the list is not present then a list is inserted and
 * that new instance is returned.
 */
public class MapOfLists<T> extends TreeMap<String, List<T>> {

    @Override
    public List<T> get(Object key) {
        if (key == null) key = "";
        List<T> result = super.get(key);
        if (result == null) {
            result = new ArrayList<T>();
            put(key.toString(), result);
        }
        return result;
    }

    /**
     * Returns all values in the order of the keys that holds them.
     * @return A list of all values.
     */
    public List<T> allInOrder() {
        List<T> allItems = new ArrayList<T>();
        for (String key : keySet()) {
            allItems.addAll(get(key));
        }
        return allItems;
    }

}
