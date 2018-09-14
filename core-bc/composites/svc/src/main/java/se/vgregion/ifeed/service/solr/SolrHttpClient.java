package se.vgregion.ifeed.service.solr;

import com.google.gson.GsonBuilder;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang.text.StrBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            return queryImp(qf, start, rows, sort);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Result queryImp(String fq, Integer start, Integer rows, String sort) throws IOException, ScriptException {
        fq = URLEncoder.encode(fq, "UTF-8");
        if (start != null) {
            fq = fq + "&start=" + start;
        }
        if (rows != null) {
            fq = fq + "&rows=" + rows;
        }
        if (sort != null && !sort.trim().isEmpty()) {
            fq = fq + "&sort=" + sort;
        }
        String url = baseUrl + "&fq=" + fq;
        System.out.println("Url to get " + url);
        String json = read(url);
        return new GsonBuilder().create().fromJson(json, Result.class);
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

    public static SolrHttpClient newInstanceFromConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String baseUrl = properties.getProperty("solr.service") + "select?wt=json&q=*%3A*";
        //String url = "http://i3-dev.vgregion.se:9090/solr/ifeed/select?wt=json&q=" + URLEncoder.encode("*:*", "UTF-8");
        return new SolrHttpClient(baseUrl);
    }

}
