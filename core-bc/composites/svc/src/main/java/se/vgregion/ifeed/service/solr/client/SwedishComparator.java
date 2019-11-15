package se.vgregion.ifeed.service.solr.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

public class SwedishComparator implements Comparator<Map<String, Object>> {

    private final String[] sortKeys;

    public SwedishComparator(String... sortKeys) {
        this.sortKeys = sortKeys;
    }

    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        String s1 = getFirstNonBlankValue(o1),
                s2 = getFirstNonBlankValue(o2);
        s1 = swapÅÄ(s1).toLowerCase();
        s2 = swapÅÄ(s2).toLowerCase();
        return s1.compareTo(s2);
    }

    static String swapÅÄ(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == 'å') {
                c = 'ä';
            } else if (c == 'ä') {
                c = 'å';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    String getFirstNonBlankValue(Map<String, Object> fromThat) {
        for (String key : sortKeys) {
            if (fromThat.containsKey(key)) {
                return toNonNullString(fromThat.get(key));
            }
        }
        return "";
    }

    static String toNonNullString(Object fromThat) {
        if (fromThat == null) return "";
        if (fromThat instanceof Collection) {
            TreeSet ts = new TreeSet((Collection) fromThat);
            return ts.descendingSet().toString();
        }
        return fromThat.toString();
    }

}
