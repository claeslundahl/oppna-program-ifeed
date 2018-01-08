package se.vgregion.ifeed.admin.backingbeans;

import java.util.Collection;

/**
 * Created by clalu4 on 2014-06-17.
 */
public class CollectionAsList<T> extends MirrorList<T, T> {

    protected CollectionAsList(Collection<T> source) {
        super(source);
    }

    @Override
    T toMirrorImage(T original) {
        return original;
    }

    @Override
    T toRealItem(T original) {
        return original;
    }
}
