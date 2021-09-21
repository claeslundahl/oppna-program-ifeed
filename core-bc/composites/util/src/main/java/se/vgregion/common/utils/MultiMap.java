package se.vgregion.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class MultiMap<K, V> extends HashMap<K, List<V>> {

    private Supplier<List<V>> listMaker;

    public MultiMap(Supplier listMaker) {
        this.listMaker = listMaker;
    }

    public MultiMap() {
        listMaker = (Supplier) () -> new ArrayList<>();
    }

    @Override
    public List<V> get(Object key) {
        List<V> r = super.get(key);
        if (r == null) put((K) key, r = listMaker.get());
        return r;
    }

}
