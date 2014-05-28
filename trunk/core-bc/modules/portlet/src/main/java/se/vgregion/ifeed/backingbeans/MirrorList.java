package se.vgregion.ifeed.backingbeans;

import java.util.*;

/**
 * Created by clalu4 on 2014-05-26.
 */
public abstract class MirrorList<T, R> extends AbstractList<T> {

    private final List<T> images = new ArrayList<T>();

    private final List<R> items = new ArrayList<R>();

    private final Collection<R> source;

    protected MirrorList(Collection<R> source) {
        this.source = source;
        syncWithSource();
    }

    public void syncWithSource() {
        for (R real : source) {
            images.add(toMirrorImage(real));
            items.add(real);
        }
    }

    @Override
    public T get(int index) {
        return images.get(index);
    }

    @Override
    public T set(int index, T element) {
        R real = toRealItem(element);
        source.add(real);
        items.set(index, real);
        return images.set(index, element);
    }

    @Override
    public boolean add(T t) {
        R real = toRealItem(t);
        items.add(real);
        source.add(real);
        return images.add(t);
    }

    @Override
    public boolean remove(Object o) {
        if (images.remove(o)) {
            R real = toRealItem((T) o);
            source.remove(real);
            items.remove(real);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T remove(int index) {
        T t = images.remove(index);
        source.remove(items.remove(index));
        return t;
    }

    @Override
    public int size() {
        return images.size();
    }

    @Override
    public boolean equals(Object o) {
        return images.equals(o);
    }

    @Override
    public void clear() {
        source.clear();
        items.clear();
        images.clear();
    }

    @Override
    public int hashCode() {
        return images.hashCode();
    }

    abstract T toMirrorImage(R original);

    abstract R toRealItem(T original);

}
