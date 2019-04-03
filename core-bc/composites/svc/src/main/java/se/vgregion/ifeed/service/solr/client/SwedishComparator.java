package se.vgregion.ifeed.service.solr.client;

import java.util.*;

public class SwedishComparator implements Comparator<Map<String, Object>> {

    private final String sortKey;

    public SwedishComparator(String sortKey) {
        this.sortKey = sortKey;
    }

    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        String s1 = (o1.get(sortKey) + "").toLowerCase(), s2 = (o2.get(sortKey) + "").toLowerCase();
        s1 = swapÅÄ(s1);
        s2 = swapÅÄ(s2);
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

}
