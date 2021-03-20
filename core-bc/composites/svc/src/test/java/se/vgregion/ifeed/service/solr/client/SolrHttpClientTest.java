package se.vgregion.ifeed.service.solr.client;

import org.apache.commons.lang.StringUtils;
import se.vgregion.common.utils.CommonUtils;
import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.service.ifeed.DocumentPopupConf;
import se.vgregion.ifeed.service.solr.Csvs;
import se.vgregion.ifeed.service.solr.SolrQueryEscaper;
import se.vgregion.ifeed.types.Field;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrHttpClientTest {

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    public static void main(String[] args) throws IOException, URISyntaxException {
        /*IFeedFilter filter = new IFeedFilter("*test*", "title");

        SolrHttpClient oldStage = new SolrHttpClient("http://vgas2192.vgregion.se:9090/solr/ifeed/");
        Result r1 = oldStage.query(filter.toQuery(client.fetchFields()), 0, 100, "asc", null);
        System.out.println(r1.getResponse().getDocs().size());
        SolrHttpClient newStage = new SolrHttpClient("https://solr-stage.vgregion.se/solr/ifeed/");
        Result r2 = newStage.query(filter.toQuery(client.fetchFields()), 0, 100, "asc", null);
        System.out.println(r2.getResponse().getDocs().size());*/

        Map<String, Set<Object>> allValues = client.findAllValues();
        for (String key : allValues.keySet()) {
            Set<Object> values = allValues.get(key);
            for (Object value : values) {
                if (value.toString().contains("Styrande dokument")) {
                    System.out.println("Hittade: " + key);
                    return;
                }
            }
        }
    }

    static String enc() throws MalformedURLException, URISyntaxException {
        String urlStr = "http://localhost:8081/iFeed-web/documents/workspace://SpacesStore/216ba47f-2f19-4339-8567-b712e9838673/metadata";
        URL url = new URL(urlStr);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        url = uri.toURL();

        return url.toExternalForm();
    }


    public static class URLParamEncoder {

        public static String encode(String input) {
            StringBuilder resultStr = new StringBuilder();
            for (char ch : input.toCharArray()) {
                if (isUnsafe(ch)) {
                    resultStr.append('%');
                    resultStr.append(toHex(ch / 16));
                    resultStr.append(toHex(ch % 16));
                } else {
                    resultStr.append(ch);
                }
            }
            return resultStr.toString();
        }

        private static char toHex(int ch) {
            return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
        }

        private static boolean isUnsafe(char ch) {
            if (ch > 128 || ch < 0)
                return true;
            return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
        }

    }

    public static String encode(String urlStr) {
        try {
            // String urlStr = "http://www.example.com/CEREC® Materials & Accessories/IPS Empress® CAD.pdf"
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*static void fetchAllWithSourceSystem() {
        IFeedFilter filter = new IFeedFilter("*", "SourceSystem");
        System.out.println("Antal " + client.query(filter.toQuery(), null, null, null).getResponse().getNumFound());
    }

    static Map<String, Object> fetchDocument(String byThatId) {
        IFeedFilter filter = new IFeedFilter(byThatId, "id");
        Result results = client.query(filter.toQuery(), null, null, "title asc");
        if (results.getResponse() == null || results.getResponse().getNumFound() == 0) {
            return null;
        }
        return results.getResponse().getDocs().get(0);
    }*/

/*    static Map<String, Object> fetchDocumentByName(String value) {
        IFeedFilter filter = new IFeedFilter(value, "title");
        System.out.println(client.getBaseUrl());
        Result results = client.query(filter.toQuery(), null, null, "title asc");
        if (results.getResponse() == null || results.getResponse().getNumFound() == 0) {
            return null;
        }
        return results.getResponse().getDocs().get(0);
    }*/

    static void findById() {
        System.out.println(client.getBaseUrl());
        // "id":"14a94647-9364-491f-ba1c-8dc76963b0b7"
        IFeedFilter filter = new IFeedFilter();
        filter.setFilterKey("id");
        filter.setFilterQuery("14a94647-9364-491f-ba1c-8dc76963b0b7");
        System.out.println(filter.toQuery(client.fetchFields()));
        Result result = client.query(filter.toQuery(client.fetchFields()), 0, 100_000, null, null);
        for (Map<String, Object> doc : result.getResponse().getDocs()) {
            for (String key : doc.keySet()) {
                System.out.println(key + " = " + doc.get(key));
            }
        }
    }

    static String dotJoinIfNotEmpty(String first, String second) {
        String result = "";
        if (!StringUtils.isBlank(first) && !StringUtils.isBlank(second)) {
            return first + "." + second;
        }
        if (!StringUtils.isBlank(first)) {
            return first;
        }
        if (!StringUtils.isBlank(second)) {
            return second;
        }
        return null;
    }

    static void addFoundColumnToAttributeFile() {

        final String foundIndexFieldColumnKey = "Hittat index-fält";
        String[] columnNames = ("Namn attribut;Sökväg;Tagg;Attribut;Beskrivning;Format;Ansvar (MA/SP);Exempel;" +
                "Multivalue?;Tvingande i XML från SP?;Benämning i SharePoint;Mappning SharePoint;;Källa;Kommentar;"
                + foundIndexFieldColumnKey).split(Pattern.quote(";"));

        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        Set<String> names = client.fetchAllFieldNames();
        client.fetchFields().forEach(field -> names.add(field.getName()));

        Path pathToCsvFile = Paths.get("C:\\Users\\clalu4\\Downloads\\Ifeed\\i-ikon-attribut.csv");
        Path pathToCsvOutputFile = Paths.get("C:\\Users\\clalu4\\Downloads\\Ifeed\\i-ikon-attribut-ii.csv");
        List<Map<String, String>> maps = Csvs.toMaps(pathToCsvFile);
        int hits = 0;
        for (Map<String, String> map : maps) {
            String taggAttribute = dotJoinIfNotEmpty(map.get("Tagg"), map.get("Attribut"));
            if (taggAttribute == null) {
                continue;
            }
            for (String name : names) {
                if (name.toLowerCase().endsWith(taggAttribute.toLowerCase())) {
                    map.put(foundIndexFieldColumnKey, name);
                    hits++;
                }
            }
        }
        Csvs.toFile(Arrays.asList(columnNames), maps, pathToCsvOutputFile);
        System.out.println("Antal träffar " + hits);
    }

    static void listAllFields() {
        Set<String> names = client.fetchAllFieldNames();
        for (String name : names) {
            System.out.println(name);
        }
    }

    static void generateCode() {

        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        Set<String> names = client.fetchAllFieldNames();
        client.fetchFields().forEach(field -> names.add(field.getName()));
        List<Map<String, String>> maps = Csvs.toMaps(Paths.get("C:\\Users\\clalu4\\Downloads\\Ifeed\\i-ikon-attribut.csv"));
        Set<String> keys = new TreeSet<>();
        NavigableMap<Integer, Attribute> result = new TreeMap<>();
        int positionInCsv = 0;
        for (Map<String, String> map : maps) {
            positionInCsv++;
            String taggAttribute = dotJoinIfNotEmpty(map.get("Tagg"), map.get("Attribut"));
            if (taggAttribute == null) {
                continue;
            }
            // System.out.println(positionInCsv + " " +taggAttribute);
            for (String name : names) {
                if (name.toLowerCase().endsWith(taggAttribute.toLowerCase())) {
                    result.put(positionInCsv, new Attribute(name, map.get("Namn attribut"), positionInCsv, taggAttribute));
                }
            }
        }

        for (Integer index : result.keySet()) {
            // toTooltipRow(item, "title", "Titel", result);
            Attribute r = result.get(index);
            System.out.println(String.format("toTooltipRow(item, \"%s\", \"%s\", result);", r.getKey(), r.getLabel()));
            // addLabelAndDocumentMeta("Publicerat för enhet", "DC.publisher.forunit", row++);
            // System.out.println(String.format("addLabelAndDocumentMeta(\"%s\", \"%s\", row++);", r.getLabel(), r.getKey()));

            // System.out.println(String.format("result.add(new LabelledValue(\"%s\", \"%s\"));", r.getKey(), r.getLabel()));
        }

    }

    static class Attribute {
        private String key;
        private String label;
        private int position;
        private String matching;

        public Attribute(String key, String label, int position, String matching) {
            this();
            this.key = key;
            this.label = label;
            this.position = position;
            this.matching = matching;
        }

        public Attribute() {

        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return '"' + getKey() + "\", \"" + getLabel() + "\", \"" + matching + '"';
        }

    }

    static void fetchFields() {
        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        Set<String> names = client.fetchAllFieldNames();
        List<DocumentPopupConf> confs = new ArrayList<>();
        for (String name : names) {
            DocumentPopupConf doc = new DocumentPopupConf();
            doc.setName(name);
            doc.setLabel(name);
            confs.add(doc);
        }
        System.out.println(CommonUtils.toBeanText(Arrays.asList("name", "label"), confs));
        System.out.println(names);

        List<Field> fields = client.fetchFields();
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                System.out.println();
                System.out.println(field);
            }
        }
    }

    static void runLatestFeedQuery() throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"))));

        System.out.println("solr.service: " + properties.getProperty("solr.service"));

        String json = new String(Files.readAllBytes(Paths.get(System.getProperty("user.home"), "feed.json")));

        List<IFeed> items = Json.toObjects(IFeed.class, json);

        IFeed feed = items.get(0);

        System.out.println(feed.toQuery(client.fetchFields()));

        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

        // System.out.println(client.post("http://i3-dev.vgregion.se:9090/solr/ifeed/select", "&start=0&rows=10&sort=core_ArchivalObject_core_ObjectType%20asc&wt=json&q=*%3A*"));

        Result result = client.query(feed.toQuery(client.fetchFields()), 0, 1_000_000, null, null);

        int i = 0;
        for (Map<String, Object> doc : result.getResponse().getDocs()) {
            i++;
            System.out.println(i + " = " + doc.get("dc.title"));
        }
    }

    /*private static void checkSolrKeysInConfigPrescenseInIndex() throws IOException {

        ConnectionExt database = CopyDatabaseUtil.getMainConnectionExt();
        FieldInf root = Json.toObject(
                FieldInf.class,
                "{ children: " +
                        database.query(
                                "select * from vgr_ifeed_fields_inf where id = (select max(id) from vgr_ifeed_fields_inf)", 0, 1
                        ).get(0).get("text") +
                        "}"
        );
        checkSolrKey(root);
        System.out.println(client.post("http://i3-dev.vgregion.se:9090/solr/ifeed/select", "&start=0&rows=10&sort=core_ArchivalObject_core_ObjectType%20asc&wt=json&q=*%3A*"));
    }*/

    private static void checkSolrKey(FieldInf forThat) throws IOException {
        if (forThat.getId() != null && !forThat.getId().trim().isEmpty()) {
            Result result = Json.toObject(
                    Result.class,
                    client.post("http://i3-dev.vgregion.se:9090/solr/ifeed/select",
                            "&start=0&rows=10&sort=" + forThat.getId() + "%20asc&wt=json&q=*%3A*"
                    )
            );
            System.out.println(forThat.getId() + " " + result.getResponseHeader().getStatus());
        }
        if (forThat.getChildren() != null) {
            for (FieldInf fieldInf : forThat.getChildren()) {
                checkSolrKey(fieldInf);
            }
        }
    }

    private static Set<String> toTextSet(Set input) {
        Set<String> result = new TreeSet<>();
        for (Object i : input) {
            result.add(String.valueOf(i));
        }
        return result;
    }

    public static void findAllValues() throws IOException {
        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        Map<String, Set<Object>> sofia = client.findAllValues();
        for (String key : sofia.keySet()) {
            System.out.println(key + " " + sofia.get(key));
        }

        SolrHttpClient production = new SolrHttpClient("http://solr.vgregion.se:80/solr/ifeed/");
        Map<String, Set<Object>> bariumAlfresco = production.findAllValues();

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table>");
        for (String baKey : bariumAlfresco.keySet()) {
            sb.append("<tr>");
            for (String sof : sofia.keySet()) {
                Set<String> stuff = toTextSet(sofia.get(sof));
                stuff.retainAll(toTextSet(bariumAlfresco.get(baKey)));
                sb.append("<td>" + stuff.size() + "</td>");
                if (stuff.size() > 20) {
                    System.out.println(baKey + " " + bariumAlfresco.get(baKey).size() + " / " + sof + " " + sofia.get(sof).size() + " " + stuff.size() + " " + stuff);
                }
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        Files.write(Paths.get(System.getProperty("user.home"), "x-table.html"), sb.toString().getBytes());
    }

    static void cacheAllValuesFromAlfrescoBarium() throws IOException {
        Path toTheFile = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "alfresco-barium-values.json");
        SolrHttpClient production = new SolrHttpClient("http://solr.vgregion.se:80/solr/ifeed/");
        final Map<String, Set<Object>> values = production.findAllValues();
        Files.write(toTheFile, Json.toJson(new HashMap<>(values)).getBytes());
        for (String s : values.keySet()) {
            System.out.println(s + " = " + values.get(s));
        }
    }

    static Map<String, List<Object>> getCachedAlfrescoBariumValues() throws IOException {
        Path toTheFile = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "alfresco-barium-values.json");
        byte[] content = Files.readAllBytes(toTheFile);
        return Json.toObject(TreeMap.class, new String(content));
    }

    static NavigableSet<String> getAllFieldsUsedInTags() throws IOException {
        NavigableSet<String> result = new TreeSet<>();
        for (String tag : getAllFeedTags()) {
            result.addAll(getFieldNames(tag));
        }
        return result;
    }

    static NavigableMap<String, Integer> field2count = new TreeMap<String, Integer>() {
        @Override
        public Integer get(Object key) {
            if (!containsKey(key)) {
                put((String) key, 0);
            }
            return super.get(key);
        }
    };

    static Collection<String> getAllFeedTags() throws IOException {
        Collection<String> urls = getPagesWithFeeds();
        System.out.println("Found " + urls.size());
        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        List<String> result = new ArrayList<>();
        for (String url : urls) {
            try {
                String all = client.read(url);
                String[] content = all.split("[<>]");
                for (String s : content) {
                    s = s.replaceAll("\\s{2,}", " ").trim().replaceAll("[\n]{2,}", " ");
                    if (s.startsWith("div class=\"ifeedDocList\"")) {
                        result.add(s);
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed " + url);
            }
        }
        return result;
    }

    static NavigableSet<String> getFieldNames(String fromThatTag) {
        NavigableSet<String> result = new TreeSet<>();
        fromThatTag = fromThatTag.replace("div class=\"ifeedDocList\" columnes=\"", "");
        fromThatTag = fromThatTag.substring(0, fromThatTag.indexOf('"'));
        String[] parts = fromThatTag.split(Pattern.quote(","));
        for (String part : parts) {
            String[] subPart = part.split(Pattern.quote("|"));
            result.add(subPart[0]);
            field2count.put(subPart[0], 1 + field2count.get(subPart[0]));
        }
        return result;
    }

    static NavigableSet<String> getPagesWithFeeds() throws IOException {
        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        String all = client.read("https://ifeed.vgregion.se/iFeed-web/metadata-calls.jsp");
        NavigableSet<String> urls = new TreeSet<>();
        String[] stuff = all.split(Pattern.quote("\""));
        for (String s : stuff) {
            s = s.trim();
            // System.out.println(s);
            if (s.startsWith("http")) {
                urls.add(s);
            }
        }
        return urls;
    }

    public static List<String> patternSample(String inThat, String pattern) {

        //String stringToSearch = "<p>Yada yada yada <code>StringBuffer</code> yada yada ...</p>";
        String stringToSearch = inThat;

        // the pattern we want to search for
        // Pattern p = Pattern.compile("<code>(\\S+)</code>");
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(stringToSearch);

        List<String> result = new ArrayList<>();
        // if we find a match, get the group
        if (m.find()) {

            // get the matching group
            String codeGroup = m.group(1);

            // print the group
            System.out.format("'%s'\n", codeGroup);
            result.add(codeGroup);
        }

        return result;
    }

    // private static final Pattern TAG_REGEX = Pattern.compile("<tag>(.+?)</tag>");

    private static List<String> getTagValuesgetTagValues(final String str, final String tagName) {
        final List<String> tagValues = new ArrayList<>();
        final Pattern TAG_REGEX = Pattern.compile("<" + tagName + "(.+?)</" + tagName + ">");
        final Matcher matcher = TAG_REGEX.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }

}
