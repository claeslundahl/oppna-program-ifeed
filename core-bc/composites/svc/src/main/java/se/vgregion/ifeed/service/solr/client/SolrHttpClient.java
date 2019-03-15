package se.vgregion.ifeed.service.solr.client;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang.text.StrBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class SolrHttpClient {

    private final ScriptEngine engine;
    private final ScriptEngineManager sem;
    private final ScriptObjectMirror JSON; // = (ScriptObjectMirror) engine.eval("JSON");

    private final String baseUrl;

    private WeakReference<Map<String, Field>> fields = new WeakReference<>(null);

    public SolrHttpClient(String baseUrl) {
        super();
        this.baseUrl = baseUrl;

        sem = new ScriptEngineManager();
        engine = sem.getEngineByName("javascript");
        try {
            JSON = (ScriptObjectMirror) engine.eval("JSON");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public Result query(String qf, Integer start, Integer rows, String sort) {
        // System.out.println(qf);
        try {
            Result result = queryImp(qf, start, rows, sort);
            // addTranslationProperties(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addTranslationProperties(Result toThat) {
        for (Map<String, Object> doc : toThat.getResponse().getDocs()) {
            addTranslationProperties(doc);
        }
    }

    private void addTranslationProperties(Map<String, Object> doc) {
        ifEmptySetOther(doc, "dc.date.issued", "vgr:VgrExtension.vgr:AvailableFrom");
        ifEmptySetOther(doc, "dc.title", "title");
    }

    private void ifEmptySetOther(Map<String, Object> doc, String target, String source) {
        if (isEmpty(doc, target)) {
            doc.put(target, doc.get(source));
        }
    }

    private boolean isEmpty(Map<String, Object> doc, String key) {
        return doc.get(key) == null || doc.get(key).toString().trim().isEmpty();
    }

    public String toText(String fq, Integer start, Integer rows, String sort) {
        try {
            if (fq == null || fq.trim().isEmpty()) fq = "";
            else fq = URLEncoder.encode(fq, "UTF-8");
            if (!fq.isEmpty()) {
                fq = "fq=" + fq;
            }
            if (start != null) {
                fq = fq + "&start=" + start;
            }
            if (rows != null) {
                fq = fq + "&rows=" + rows;
            }
            if (sort != null && !sort.trim().isEmpty()) {
                String tempSort = sort;
                if (tempSort.contains(" ")) {
                    tempSort = URLEncoder.encode(tempSort, "UTF-8");
                }
                fq = fq + "&sort=" + tempSort;
            }
            fq = fq + "&wt=json&q=*%3A*";

            System.out.println(fq);

            String json = post(baseUrl + "select", fq);

            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Result queryImp(String fq, Integer start, Integer rows, String sort) throws IOException {
        if (fields.get() == null) {
            List<Field> temp = fetchFields();
            Map<String, Field> map = new HashMap<>();
            for (Field field : temp) {
                map.put(field.getName(), field);
            }

            fields = new WeakReference<>(map);
        }

        /*if (fq == null || fq.trim().isEmpty()) fq = "";
        else fq = URLEncoder.encode(fq, "UTF-8");
        if (!fq.isEmpty()) {
            fq = "fq=" + fq;
        }
        if (start != null) {
            fq = fq + "&start=" + start;
        }
        if (rows != null) {
            fq = fq + "&rows=" + rows;
        }
        boolean haveSort = false;
        if (sort != null && !sort.trim().isEmpty()) {
            haveSort = true;
            fq = fq + "&sort=" + sort;
        }
        fq = fq + "&wt=json&q=*%3A*";

        System.out.println(fq);*/

        // String json = post(baseUrl + "select", fq);

        String json = toText(fq, start, rows, sort);

        // System.out.println(json);

        Result result = new GsonBuilder().create().fromJson(json, Result.class);

/*        if (result.getResponse() == null) {
            System.out.println("f");
        }*/

        if (sort != null && !sort.trim().isEmpty()) {
            final String[] parts = sort.split(Pattern.quote(" "));
            if (parts.length == 2) {
                final String sortKey = parts[0];
                final String dir = parts[1];
                Field field = fields.get().get(sortKey);
                if ("text_basic_token".equals(field.getType())) {
                    Collections.sort(result.getResponse().getDocs(), (o1, o2) -> {
                        String s1 = (o1.get(sortKey) + "").toLowerCase(), s2 = (o2.get(sortKey) + "").toLowerCase();
                        return s1.compareTo(s2);
                    });
                    if ("desc".equalsIgnoreCase(dir)) {
                        Collections.reverse(result.getResponse().getDocs());
                    }
                }
            }
        }
        return result;
    }

    public String post(String toThatUrl, String thatData) throws IOException {

        /*System.out.println("Posting to: " + toThatUrl);
        System.out.println("That data: " + thatData);*/

        URL oracle = new URL(toThatUrl);
        HttpURLConnection con = (HttpURLConnection) oracle.openConnection();
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(thatData);

        int responseCode = con.getResponseCode();
        // System.out.println(con.getResponseMessage());

        if (responseCode == 200) {
            //toText(con.getInputStream());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            wr.flush();
            wr.close();
            in.close();
            return response.toString();
        } else {
            return toText(con.getErrorStream());
        }
    }

    private String toText(InputStream inputStream) {
        try {
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
            inputStream.close();
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

    public String read(String fromThatUrl) throws IOException {
        URL oracle = new URL(fromThatUrl);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));
        StrBuilder sb = new StrBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
            sb.append('\n');
        }
        in.close();
        return sb.toString();
    }

    public String toJson(Object object) {
        return (String) JSON.callMember("stringify", object);
    }

    public Object toObjectGraph(String json) {
        String script = "foo = Java.asJSONCompatible(" + json + ")";
        try {
            Object result = engine.eval(script);
            return result;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }



    public List<Field> fetchFields() {
        try {
            String response = read(baseUrl + "schema?wt=json");
            Map<String, Object> root = (Map<String, Object>) toObjectGraph(response);
            Map<String, Object> schema = (Map<String, Object>) root.get("schema");
            List<Map<String, Object>> fields = (List<Map<String, Object>>) schema.get("fields");

            String json = "{\"all\": " + toJson(fields) + "}";

            Type listType = new TypeToken<ArrayList<Field>>() {
            }.getType();
            List<Field> result = new GsonBuilder().create().fromJson(toJson(fields), listType);

            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NavigableSet<String> toFacetSet(Result fromThat, String fieldName, String starPattern) {
        NavigableSet result = new TreeSet();
        for (Map<String, Object> item : fromThat.getResponse().getDocs()) {
            Object value = item.get(fieldName);
            if (value == null) {
                continue;
            }
            if (value instanceof Collection) {
                while (starPattern.contains("**")) {
                    starPattern = starPattern.replace("**", "*");
                }
                String[] stars = starPattern.split(Pattern.quote("*"));

                for (Object c : (Collection) value) {
                    String text = String.valueOf(c).trim();
                    if (stellarMatch(stars, text)) {
                        result.add(text);
                        System.out.println(text);
                    }
                }
            } else {
                result.add(String.valueOf(value).trim());
            }
        }
        return result;
    }

    static boolean stellarMatch(String[] stars, String value) {
        for (String star : stars) {
            int index = value.indexOf(star);
            if (index == -1) {
                return false;
            }
            value = value.substring(index + star.length());
        }
        return true;
    }

    public Set<String> fetchAllFieldNames() {
        Set<String> result = new TreeSet<>();

        List<Map<String, Object>> docs = query(
                "",
                0,
                1_000_000,
                "title asc"
        ).getResponse().getDocs();

        for (Map<String, Object> doc : docs) {
            result.addAll(doc.keySet());
        }

        return result;
    }

    public static SolrHttpClient newInstanceFromConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // String baseUrl = properties.getProperty("solr.service") + "select?wt=json&q=*%3A*";
        String baseUrl = properties.getProperty("solr.service");
        //String url = "http://i3-dev.vgregion.se:9090/solr/ifeed/select?wt=json&q=" + URLEncoder.encode("*:*", "UTF-8");
        return new SolrHttpClient(baseUrl);
    }


    public String getBaseUrl() {
        return baseUrl;
    }


    public Map<String, Set<Object>> findAllValues() {
        final Map<String, Set<Object>> result = new TreeMap<String, Set<Object>>() {
            @Override
            public Set<Object> get(Object key) {
                if (!containsKey(key)) {
                    put((String) key, new TreeSet<>());
                }
                return super.get(key);
            }
        };

        Result everything = query("", 0, 1_000_000, null);


        for (Map<String, Object> item : everything.getResponse().getDocs()) {
            for (String key : item.keySet()) {
                Object value = item.get(key);
                if (value instanceof List) {
                    result.get(key).addAll((Collection<?>) item.get(key));
                } else {
                    result.get(key).add(item.get(key));
                }
            }
        }
        // System.out.println("Antal mappningar " + result.size());
        return result;
    }

}
