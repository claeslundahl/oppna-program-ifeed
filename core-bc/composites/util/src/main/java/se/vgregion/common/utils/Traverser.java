package se.vgregion.common.utils;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

public class Traverser {

    public static void go(Object into, Consumer<Object> visitor) {
        goImp(into, visitor, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private static void goImp(Object into, Consumer<Object> visitor, Set<Object> blacklist) {
        if (blacklist.contains(into) || into == null || into instanceof Class) {
            return;
        }
        blacklist.add(into);
        visitor.accept(into);
        JavaAttributeMap attributeMap = new JavaAttributeMap(into);
        for (String key : attributeMap.keySet()) {
            Object value = attributeMap.get(key);
            if (value == null) {
                continue;
            }
            // Is it an array?
            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i ++) {
                    Object arrayElement = Array.get(value, i);
                    goImp(arrayElement, visitor, blacklist);
                }
            } else{
                goImp(value, visitor, blacklist);
            }
        }
    }

}
