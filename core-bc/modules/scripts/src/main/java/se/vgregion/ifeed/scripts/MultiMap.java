package se.vgregion.ifeed.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiMap<K, V> extends HashMap<K, List<V>> {

    @Override
    public List<V> get(Object key) {
        if (!containsKey(key)) {
            put((K) key, new ArrayList<>());
        }
        return super.get(key);
    }

}
