package se.vgregion.ifeed.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UtilityTest {

    static List<Map<String, String>> fromSpec = getMetaFromSpec();

    static Map<String, Object> fromEnv = getMetaFromEnv();


    public static void main(String[] args) {

    }



    public static List<Map<String, String>> getMetaFromSpec() {
        String metaTableConf = toText(UtilityTest.class.getResourceAsStream("meta.table.txt"));

        String[] lines = metaTableConf.split(Pattern.quote("\n"));
        String[] keys = lines[0].split(Pattern.quote("\t"));
        List<Map<String, String>> items = new ArrayList<>();
        for (String line : lines) {
            String[] cells = line.split(Pattern.quote("\t"));
            int i = 0;
            Map<String, String> item = new HashMap<>();
            for (String key : keys) {
                if (i >= cells.length) break;
                item.put(key, cells[i]);
                i++;
            }
            items.add(item);
        }
        return items;
    }

    static Map<String, Object> getMetaFromEnv() {
        String envConf = toText(UtilityTest.class.getResourceAsStream("meta.from.env.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(envConf, Map.class);
    }

    static FieldInf toFieldInf() {
        /*
         {
            "id": "dc.type.process.name",
            "name": "Processnamn",
            "help": "Sammanhållen hantering som avgränsas och namnges av verksamheten. (Fritext)",
            "type": "d:text",
            "apelonKey": "",
            "children": [],
            "filter": true,
            "inHtmlView": true,
            "expanded": false
          }
         */
        return null;
    }

    private static String toText(InputStream inputStream) {
        try {
            return toTextImp(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toTextImp(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

}