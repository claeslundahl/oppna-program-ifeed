package se.vgregion.ifeed.service.solr;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang.text.StrBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SolrHttpClient {

    private final ScriptEngine engine;
    private final ScriptEngineManager sem;
    private final ScriptObjectMirror JSON; // = (ScriptObjectMirror) engine.eval("JSON");

    private final String baseUrl;

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
        try {
            Result result = queryImp(qf, start, rows, sort);
            addTranslationProperties(result);
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

    private Result queryImp(String fq, Integer start, Integer rows, String sort) throws IOException {
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
            fq = fq + "&sort=" + sort;
        }
        fq = fq + "&wt=json&q=*%3A*";

        String json = post(baseUrl + "select", fq);
        Result result = new GsonBuilder().create().fromJson(json, Result.class);
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
            StringBuffer response = new StringBuffer();

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

    public static NavigableSet<String> toFacetSet(Result fromThat, String fieldName) {
        NavigableSet result = new TreeSet();
        for (Map<String, Object> item : fromThat.getResponse().getDocs()) {
            Object value = item.get(fieldName);
            if (value instanceof Collection) {
                for (Object c : (Collection) value) {
                    result.add(String.valueOf(c).trim());
                }
            } else {
                result.add(String.valueOf(value).trim());
            }
        }
        return result;
    }

    public static class Result {

        private Header responseHeader;
        private Response response;


        public Header getResponseHeader() {
            return responseHeader;
        }

        public void setResponseHeader(Header responseHeader) {
            this.responseHeader = responseHeader;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }
    }

    public static class Header {

        private boolean zkConnected;
        private int status;
        private int QTime;
        private Map<String, String> params;

        public boolean isZkConnected() {
            return zkConnected;
        }

        public void setZkConnected(boolean zkConnected) {
            this.zkConnected = zkConnected;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getQTime() {
            return QTime;
        }

        public void setQTime(int QTime) {
            this.QTime = QTime;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }
    }

    public static class Response {
        private int numFound;

        private int start;
        private List<Map<String, Object>> docs;

        public int getNumFound() {
            return numFound;
        }

        public void setNumFound(int numFound) {
            this.numFound = numFound;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public List<Map<String, Object>> getDocs() {
            return docs;
        }

        public void setDocs(List<Map<String, Object>> docs) {
            this.docs = docs;
        }

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

    public static class Field {

        public Field() {
            super();
        }

        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String type;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private boolean multiValued;

        public boolean getMultiValued() {
            return this.multiValued;
        }

        public void setMultiValued(boolean multiValued) {
            this.multiValued = multiValued;
        }

        private boolean indexed;

        public boolean getIndexed() {
            return this.indexed;
        }

        public void setIndexed(boolean indexed) {
            this.indexed = indexed;
        }

        private boolean required;

        public boolean getRequired() {
            return this.required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        private boolean stored;

        public boolean getStored() {
            return this.stored;
        }

        public void setStored(boolean stored) {
            this.stored = stored;
        }

        private String _default;

        public String getDefault() {
            return this._default;
        }

        public void setDefault(String _default) {
            this._default = _default;
        }

        private Boolean omitNorms;

        public Boolean getOmitNorms() {
            return this.omitNorms;
        }

        public void setOmitNorms(Boolean omitNorms) {
            this.omitNorms = omitNorms;
        }


        @Override
        public String toString() {
            return "Field{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", multiValued=" + multiValued +
                    ", indexed=" + indexed +
                    ", required=" + required +
                    ", stored=" + stored +
                    ", _default='" + _default + '\'' +
                    ", omitNorms=" + omitNorms +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Field)) return false;

            Field field = (Field) o;

            if (multiValued != field.multiValued) return false;
            if (indexed != field.indexed) return false;
            if (required != field.required) return false;
            if (stored != field.stored) return false;
            if (name != null ? !name.equals(field.name) : field.name != null) return false;
            if (type != null ? !type.equals(field.type) : field.type != null) return false;
            if (_default != null ? !_default.equals(field._default) : field._default != null) return false;
            return omitNorms != null ? omitNorms.equals(field.omitNorms) : field.omitNorms == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (multiValued ? 1 : 0);
            result = 31 * result + (indexed ? 1 : 0);
            result = 31 * result + (required ? 1 : 0);
            result = 31 * result + (stored ? 1 : 0);
            result = 31 * result + (_default != null ? _default.hashCode() : 0);
            result = 31 * result + (omitNorms != null ? omitNorms.hashCode() : 0);
            return result;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }


    public Map<String, Set<Object>> findAllValues() {
        Map<String, Set<Object>> result = new TreeMap<String, Set<Object>>() {
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

        return result;
    }

}
