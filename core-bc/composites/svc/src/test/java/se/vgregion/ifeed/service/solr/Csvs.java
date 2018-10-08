package se.vgregion.ifeed.service.solr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Csvs {

    public static void main(String[] args) {
        Path p = Paths.get("C:\\Users\\clalu4\\Downloads\\Dialys\\Aktiva patienter DIalys i Väst_samlad lista.csv");
        List<Map<String, String>> maps = toMaps(p);
        Set<String> pnrs = new HashSet<>();
        for (Map<String, String> map : maps) {
            String pnr = map.get("Personnummer");
            if (pnr.length() == 11)
                pnr = "19".concat(pnr);
            pnrs.add(String.format("'%s'", pnr));
        }
        System.out.println(pnrs);
    }

    public static List<Map<String, String>> toMaps(Path path) {
        try {
            List<Map<String, String>> result = new ArrayList<>();
            List<String> rows = new ArrayList<>(Files.readAllLines(path));
            String[] keys = toKeys(rows.remove(0));

            for (String row : rows) {
                String[] parts = row.split(Pattern.quote(";"));
                Map<String, String> map = new HashMap<>();
                int c = 0;
                for (String key : keys) {
                    if (parts.length <= c) break;
                    map.put(key, parts[c++]);
                }
                result.add(map);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String[] toKeys(String line) {
        String[] result = line.split(Pattern.quote(";"));
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].replaceAll("\\(.*\\)", "").trim();
        }
        return result;
    }

    public static String toText(Collection keys, Collection<? extends Map> items) {
        StringBuilder sb = new StringBuilder();
        final List<String> heading = new ArrayList<>();
        for (Object key : keys) {
            heading.add(String.valueOf(key));
        }
        sb.append(String.join(";", heading));
        sb.append("\n");
        for (Map<?, ?> item : items) {
            final List<String> row = new ArrayList<>();
            for (Object key : keys) {
                Object value = item.get(key);
                if (value != null && !value.toString().trim().isEmpty()) {
                    row.add(String.valueOf(value));
                } else {
                    row.add("");
                }
            }
            sb.append(String.join(";", row));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void toFile(Collection keys, Collection<? extends Map> items, Path putResultsHere) {
        try {
            Files.write(putResultsHere, Arrays.asList(toText(keys, items).split(Pattern.quote("\n"))), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
