package se.vgregion.common.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DistinctArrayList<T> extends DelegatedList<T> implements Serializable {

    public DistinctArrayList() {
        super();
    }

    public DistinctArrayList(Collection<T> items) {
        super();
        addAll(items);
    }

    @Override
    public boolean add(T har) {
        if (!contains(har)) {
            return super.add(har);
        }
        return false;
    }

    @Override
    public void add(int index, T element) {
        if (!contains(element)) super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = false;
        for (T item : c)
            if (add(item))
                result = true;
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        List<? extends T> intersection = new ArrayList<T>(c);
        intersection.removeAll(this);
        return super.addAll(index, intersection);
    }

}
