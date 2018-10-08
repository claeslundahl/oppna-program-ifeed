package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.StringUtils;
import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;
import se.vgregion.common.utils.CommonUtils;
import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.scripts.CopyDatabaseUtil;
import se.vgregion.ifeed.service.ifeed.DocumentPopupConf;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class SolrHttpClientTest {

    public static void main(String[] args) throws IOException {
        cacheAllValuesFromAlfrescoBarium();
        // findAllValues();
        // runLatestFeedQuery();
        // addFoundColumnToAttributeFile();
        // compareListWithAllFields();
        // addFoundColumnToAttributeFile();
        // generateCode();
        // fetchFields();
        // testAllIndexFields();
        // checkSolrKeysInConfigPrescenseInIndex();
        // findById();
    }

    static void findById() {
        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        System.out.println(client.getBaseUrl());
        // "id":"14a94647-9364-491f-ba1c-8dc76963b0b7"
        IFeedFilter filter = new IFeedFilter();
        filter.setFilterKey("id");
        filter.setFilterQuery("14a94647-9364-491f-ba1c-8dc76963b0b7");
        System.out.println(filter.toQuery());
        SolrHttpClient.Result result = client.query(filter.toQuery(), 0, 100_000, "title asc");
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

        List<SolrHttpClient.Field> fields = client.fetchFields();
        for (SolrHttpClient.Field field : fields) {
            if (field.getName().equals("id")) {
                System.out.println();
                System.out.println(field);
            }
        }
    }

    static void testAllIndexFields() {
        List<String> fieldNames = Arrays.asList(
                "core:ArchivalObject.core:Producer",
                "core:ArchivalObject.core:Classification.core:Classification.name",
                "core:ArchivalObject.core:ObjectType",
                "vgr:VgrExtension.vgr:PublishedForUnit.id",
                "vgrsy:DomainExtension.vgrsy:SubjectClassification",
                "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification",
                "vgr:VgrExtension.vgr:Tag",
                "vgr:VgrExtension.vgr:SourceSystem",
                "vgr:VgrExtension.vgr:CreatedByUnit.id",
                "vgr:VgrExtension.vgr:CreatedBy",
                "vgr:VgrExtension.vgr:RevisedAvailableFrom",
                "vgr:VgrExtension.vgr:RevisedAvailableTo",
                "vgr:VgrExtension.vgr:SecurityClass",
                "dc.title",
                "dc.title.filename",
                "dc.title.filename.native",
                "dc.title.alternative",
                "dc.description",
                "dc.type.document",
                "dc.type.document.structure",
                "dc.type.document.structure.id",
                "dc.type.record",
                "dc.type.record.id",
                "dc.coverage.hsacode",
                "dc.coverage.hsacode.id",
                "dcterms.audience",
                "dc.audience",
                "dcterms.audience.id",
                "dc.identifier.version",
                "dc.contributor.savedby",
                "dc.contributor.savedby.id",
                "dc.date.saved",
                "vgregion.status.document",
                "vgregion.status.document.id",
                "vgr.status.document",
                "vgr.status.document.id",
                "dc.source.documentid",
                "dc.source",
                "dc.creator",
                "dc.creator.id",
                "dc.creator.freetext",
                "dc.creator.forunit",
                "dc.creator.forunit.id",
                "dc.creator.project-assignment",
                "dc.creator.document",
                "dc.creator.document.id",
                "dc.creator.function",
                "dc.creator.recordscreator",
                "dc.creator.recordscreator.id",
                "dc.date.validfrom",
                "dc.date.validto",
                "dc.date.availablefrom",
                "dc.date.availableto",
                "dc.date.copyrighted",
                "dc.contributor.acceptedby",
                "dc.contributor.acceptedby.id",
                "dc.contributor.acceptedby.freetext",
                "dc.date.accepted",
                "dc.contributor.acceptedby.role",
                "dc.contributor.acceptedby.unit.freetext",
                "dc.contributor.controlledby",
                "dc.contributor.controlledby.id",
                "dc.contributor.controlledby.freetext",
                "dc.date.controlled",
                "dc.contributor.controlledby.role",
                "dc.contributor.controlledby.unit.freetext",
                "dc.publisher.forunit",
                "dc.publisher.forunit.flat",
                "dc.publisher.forunit.id",
                "dc.publisher.project-assignment",
                "dc.rights.accessrights",
                "dc.publisher",
                "dc.publisher.id",
                "dc.date.issued",
                "dc.identifier.documentid",
                "dc.identifier",
                "dc.identifier.native",
                "dc.type.process.name",
                "dc.type.file.process",
                "dc.type.file",
                "dc.identifier.diarie.id",
                "dc.type.document.serie",
                "dc.type.document.id",
                "dc.subject.keywords",
                "dc.subject.authorkeywords",
                "language",
                "dc.language",
                "dc.relation.isversionof",
                "dc.relation.replaces",
                "dc.format.extent",
                "dc.identifier.location",
                "dc.type.templatename",
                "dc.format.extent.mimetype",
                "dc.format.extent.mimetype.native",
                "dc.format.extension",
                "dc.format.extension.native",
                "dc.identifier.checksum",
                "dc.identifier.checksum.native",
                "dc.source.origin"
        );

        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
        for (String fieldName : fieldNames) {
            IFeed feed = new IFeed();
            IFeedFilter filter = new IFeedFilter(
                    "*",
                    fieldName
            );
            feed.addFilter(filter);
            SolrHttpClient.Result result = client.query(feed.toQuery(), 0, 10_000, "title asc");
            System.out.println(fieldName + " " + result.getResponse().getDocs().size());
        }

    }

    static void runLatestFeedQuery() throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"))));

        System.out.println("solr.service: " + properties.getProperty("solr.service"));

        String json = new String(Files.readAllBytes(Paths.get(System.getProperty("user.home"), "feed.json")));
        IFeed feed = Json.toObject(IFeed.class, json);

        System.out.println(feed.toQuery());

        SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

        // System.out.println(client.post("http://i3-dev.vgregion.se:9090/solr/ifeed/select", "&start=0&rows=10&sort=core_ArchivalObject_core_ObjectType%20asc&wt=json&q=*%3A*"));

        SolrHttpClient.Result result = client.query(feed.toQuery(), 0, 1_000_000, "dc.title%20asc");

        for (Map<String, Object> doc : result.getResponse().getDocs()) {
            System.out.println(doc);
        }
    }

    private static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    private static void checkSolrKeysInConfigPrescenseInIndex() throws IOException {

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
    }

    private static void checkSolrKey(FieldInf forThat) throws IOException {
        if (forThat.getId() != null && !forThat.getId().trim().isEmpty()) {
            SolrHttpClient.Result result = Json.toObject(
                    SolrHttpClient.Result.class,
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
        Map<String, Set<Object>> values = production.findAllValues();
        Files.write(toTheFile, Json.toJson(values).getBytes());
    }

    static Map<String, Set<Object>> getCachedAlfrescoBariumValues() throws IOException {
        Path toTheFile = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "alfresco-barium-values.json");
        byte[] content = Files.readAllBytes(toTheFile);
        return Json.toObject(TreeMap.class, new String(content));
    }

}