package se.vgregion.ifeed.service.solr.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import se.vgregion.ifeed.service.solr.SolrQueryEscaper;
import se.vgregion.ifeed.types.Field;
import se.vgregion.ifeed.types.FieldInf;

import javax.script.ScriptEngine;
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

    /*private final ScriptEngine engine;
    private final ScriptEngineManager sem;*/
    // private final ScriptObjectMirror JSON; // = (ScriptObjectMirror) engine.eval("JSON");

    private final String baseUrl;

    private WeakReference<Map<String, Field>> fields = new WeakReference<>(null);
    private String latestCall;
    private String latestCallAsGet;

    public SolrHttpClient(String baseUrl) {
        super();
        this.baseUrl = baseUrl;

        /*sem = new ScriptEngineManager();
        engine = sem.getEngineByName("javascript");*/
/*        try {
            JSON = (ScriptObjectMirror) engine.eval("JSON");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }*/
    }

    // public Result query(String qf, Integer start, Integer rows, String dir, String ... sort) {

    public Result query(String qf, Integer start, Integer rows, String dir, FieldInf sortField, String... fl) {
        /*if (fl != null)
            System.out.println("Query " + new ArrayList<>(Arrays.asList(fl)));
        else System.out.println("fl is null");*/
        try {
            Result result = queryImp(qf, start, rows, dir, sortField, fl);
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

/*    public String toText(String fq, Integer start, Integer rows, String sort) {
        throw new RuntimeException();
    }*/

    public static String toTextQuery(String fq, Integer start, Integer rows, String sort, String fl)
            throws UnsupportedEncodingException {
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

        if (fl != null) {
            fq += "&fl=" + URLEncoder.encode(fl, "UTF-8");
        }

        return fq;
    }

    public String toText(String fq, Integer start, Integer rows, String sort, String fl) {
        try {
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
            if (sort != null && !sort.trim().isEmpty()) {
                String tempSort = sort;
                if (tempSort.contains(" ")) {
                    tempSort = URLEncoder.encode(tempSort, "UTF-8");
                }
                fq = fq + "&sort=" + tempSort;
            }
            fq = fq + "&wt=json&q=*%3A*";*/

            fq = toTextQuery(fq, start, rows, sort, fl);

            latestCall = baseUrl + "select";

            latestCallAsGet = baseUrl + "select?" + fq;

            /*if (fl != null) {
                fq += "&fl=" + URLEncoder.encode(fl, "UTF-8");
            }*/

            String json = post(latestCall, fq);

            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Result queryImp(String fq, Integer start, Integer rows, String dir, FieldInf sortOn, String... fl) {
        if (start == null) start = 0;
        if (rows == null) rows = 1_000_000;

        String[] sortFields = sortOn == null ? null : sortOn.toSolrKeys();

        if (sortFields == null || sortFields.length == 0) {
            sortFields = new String[]{"title"};
        }

        if (StringUtils.isBlank(dir)) {
            dir = "asc";
        }

        String fields = null;
        if (fl != null && fl.length > 0) {
            for (int i = 0; i < fl.length; i++) {
                fl[i] = SolrQueryEscaper.escape(fl[i]).replace("\\:", "*");
            }
            fields = String.join(", ", fl);
            // System.out.println(fields);
        }

        String json = toText(fq, start, 1_000_000, null, fields);
        Result result = new GsonBuilder().create().fromJson(json, Result.class);

        if (result.getResponse() != null) {
            Collections.sort(result.getResponse().getDocs(), new SwedishComparator(sortFields));
            if ("desc".equalsIgnoreCase(dir)) {
                Collections.reverse(result.getResponse().getDocs());
            }
            result.getResponse().setDocs(
                    result.getResponse().getDocs().subList(0, Math.min(rows, result.getResponse().getDocs().size()))
            );
        }

        return result;
    }

    public String post(String toThatUrl, String thatData) throws IOException {
        URL oracle = new URL(toThatUrl);
        HttpURLConnection con = (HttpURLConnection) oracle.openConnection();
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(thatData);

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
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

    private final static Gson gson = new GsonBuilder().create();

    public String toJson(Object object) {
        return gson.toJson(object);
        // return (String) JSON.callMember("stringify", object);
    }

    public Object toObjectGraph(String json) {
        return gson.fromJson(json, Map.class);
        /*String script = "foo = Java.asJSONCompatible(" + json + ")";
        try {
            Object result = engine.eval(script);
            return result;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }*/
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
                    }
                }
            } else {
                result.add(String.valueOf(value).trim());
            }
        }
        return result;
    }

    public static boolean stellarMatch(String[] stars, String value) {
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
                "asc",
                null,
                null
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

    public NavigableSet<Object> findAllValues(String forField) {
        final NavigableSet<Object> result = new TreeSet<>();

        Result everything = query("", 0, 1_000_000, null, null, forField);

        if (everything.getResponse() != null) {
            for (Map<String, Object> item : everything.getResponse().getDocs()) {
                for (String key : item.keySet()) {
                    Object value = item.get(key);
                    if (value instanceof Collection) {
                        result.addAll((Collection<Object>) item.get(key));
                    } else {
                        result.add(item.get(key));
                    }
                }
            }
        }
        return result;
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

        Result everything = query("", 0, 1_000_000, null, null, null);

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
        return result;
    }

    public String getLatestCall() {
        return latestCall;
    }

    public String getLatestCallAsGet() {
        return latestCallAsGet;
    }

}
