package se.vgregion.ifeed.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by clalu4 on 2014-06-12.
 */
public class EventedList<T> extends ArrayList<T> {

    @Override
    public boolean add(T har) {
        boolean r = super.add(har);
        if (r) onItemAdd(har, size() - 1);
        return r;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        onItemAdd(element, index);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean r = super.addAll(c);
        if (r)
            for (T har : c)
                onItemAdd(har, indexOf(har));
        return r;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean r = super.addAll(index, c);
        if (r)
            for (T t : c)
                if (contains(c))
                    onItemAdd(t, index++);
        return r;
    }

    @Override
    public T set(int index, T element) {
        T r = super.set(index, element);
        onItemRemoved(r, index);
        onItemAdd(element, index);
        return r;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean r = false;
        for (Object o : c) {
            if (remove(o)) r = true;
        }
        return r;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        boolean r = super.remove(o);
        if (r) onItemRemoved((T) o, index);
        return r;
    }

    @Override
    public T remove(int index) {
        T r = super.remove(index);
        if (r != null) onItemRemoved(r, index);
        return r;
    }

    private final List<Spy<T>> removeSpies = new ArrayList<Spy<T>>();

    private final List<Spy<T>> addSpies = new ArrayList<Spy<T>>();

    public void onItemAdd(T har, int intoIndex) {
        removeRemovable(addSpies);
        for (Spy<T> spy : addSpies) spy.event(har, intoIndex);
    }

    public void onItemRemoved(T har, int fromIndex) {
        removeRemovable(removeSpies);
        for (Spy<T> spy : removeSpies) spy.event(har, fromIndex);
    }

    private void removeRemovable(List<Spy<T>> spies) {
        if (!spies.isEmpty())
            for (int i = spies.size() - 1; i >= 0; i--) {
                Spy<T> spy = spies.get(i);
                if (spy.isRemoveAble())
                    spies.remove(spy);
            }
    }

    public List<Spy<T>> getRemoveSpies() {
        return removeSpies;
    }

    public List<Spy<T>> getAddSpies() {
        return addSpies;
    }

    public static interface Spy<T> extends Serializable {
        void event(T item, int index);
        boolean isRemoveAble();
    }

}
