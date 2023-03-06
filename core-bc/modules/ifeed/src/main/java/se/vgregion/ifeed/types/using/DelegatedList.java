package se.vgregion.ifeed.types.using;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class DelegatedList<T> implements List<T>, Serializable {

    private List<T> imp;

    public DelegatedList(List<T> imp) {
        super();
        assert imp != null;
        this.imp = imp;
    }

    public DelegatedList() {
        this(new ArrayList<T>());
    }

    public DelegatedList(Collection<? extends T> collection) {
        this(new ArrayList<T>());
        addAll(collection);
    }

    @Override
    public int size() {
        return imp.size();
    }

    @Override
    public boolean isEmpty() {
        return imp.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return imp.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return imp.iterator();
    }

    @Override
    public Object[] toArray() {
        return imp.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) imp.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return imp.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return (boolean) imp.remove((Object) o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return imp.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return imp.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return imp.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return imp.removeAll(c);
    }

    // @Erased
    /*@Override
    public boolean retainAll(Collection<?> c) {
        return imp.retainAll(c);
    }*/

    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Iterator<T> it = iterator();
        while (it.hasNext())
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        return modified;
    }

    @Override
    public void clear() {
        imp.clear();
    }

    @Override
    public T get(int index) {
        return imp.get(index);
    }

    @Override
    public T set(int index, T element) {
        return imp.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        imp.add(index, element);
    }

    @Override
    public T remove(int index) {
        return imp.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return imp.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return imp.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return imp.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return imp.listIterator();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return imp.subList(fromIndex, toIndex);
    }

    public List<T> getImp() {
        return imp;
    }

    public void setImp(List<T> imp) {
        if (this.imp != null) imp.addAll(this.imp);
        this.imp = imp;
    }

    @Override
    public String toString() {
        return imp.toString();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        imp.forEach(action);
    }
}
