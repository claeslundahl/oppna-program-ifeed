package se.vgregion.common.utils;

import java.util.ArrayList;
import java.util.List;

public class ComparableArrayList extends ArrayList<String> implements Comparable {
    public ComparableArrayList(List<String> id) {
        super(id);
    }

    public ComparableArrayList() {
        super();
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(String.valueOf(o));
    }
}
