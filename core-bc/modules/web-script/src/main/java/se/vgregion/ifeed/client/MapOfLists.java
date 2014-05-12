package se.vgregion.ifeed.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by clalu4 on 2014-05-09.
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

    public List<T> allInOrder() {
        List<T> allItems = new ArrayList<T>();
        for (String key : keySet()) {
            allItems.addAll(get(key));
        }
        return allItems;
    }

}
