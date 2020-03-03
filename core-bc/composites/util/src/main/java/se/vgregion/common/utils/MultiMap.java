package se.vgregion.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiMap<K, V> extends HashMap<K, List<V>> {
    @Override
    public List<V> get(Object key) {
        List<V> r = super.get(key);
        if (r == null) put((K) key, r = new ArrayList<>());
        return r;
    }
}
