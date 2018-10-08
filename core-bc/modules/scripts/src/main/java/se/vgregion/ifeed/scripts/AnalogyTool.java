package se.vgregion.ifeed.scripts;

import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Column;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;

import java.sql.Timestamp;
import java.util.*;

public class AnalogyTool {

    static Set<String> fromOwnClassificationTags = new HashSet<>(
            Arrays.asList(
                    "dc.publisher.project-assignment", "dc.type.process.name", "dc.type.file.process", "dc.type.file", "dc.type.document.serie", "dc.type.document.id", "dc.type.document", "dc.type.record", "dc.creator.project-assignment", "dc.type.document.structure"
            )
    );

    static String ownClassificationTag = "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification";

    static Set<String> allTagsToConsider = new HashSet<>();

    static {
        allTagsToConsider.addAll(fromOwnClassificationTags);
        allTagsToConsider.add("dc.publisher.forunit.id");
        allTagsToConsider.add("dc.source.origin");
        allTagsToConsider.add("dc.creator.forunit.id");
    }


    static ConnectionExt main = CopyDatabaseUtil.getMainConnectionExt();

    public static void main(String[] args) throws ClassNotFoundException {

        System.out.println(main.getUrl());

        // if (true) return;

        main.update("delete from vgr_ifeed_vgr_ifeed where composites_id < 0 or partof_id < 0");
        main.update("delete from vgr_ifeed_filter where id < 0");
        main.update("delete from vgr_ifeed_ownership where ifeed_id < 0");
        main.update("delete from vgr_ifeed where id < 0");

        List<Map<String, Object>> feeds = main.query("select * from vgr_ifeed", 0, 1_000_000);
        for (Map<String, Object> feed : feeds) {
            // System.out.println(feed);
            List<Map<String, Object>> filters = main.query(
                    "select * from vgr_ifeed_filter where ifeed_id = ?",
                    0,
                    1_000_000,
                    feed.get("id")
            );
            if (filters.isEmpty()) {
                continue;
            }
            MultiMap<String, Map<String, Object>> keyedValues = new MultiMap<>();
            for (Map<String, Object> filter : filters) {
                keyedValues.get(filter.get("filterkey")).add(filter);
            }

            keyedValues.keySet().retainAll(allTagsToConsider);

            int result = 0;
            result += insertOne(feed, keyedValues, fromOwnClassificationTags, ownClassificationTag);
            result += insertOne((feed), keyedValues, new HashSet<>(Arrays.asList("dc.publisher.forunit.id")), "vgr:VgrExtension.vgr:PublishedForUnit.id");
            result += insertOne((feed), keyedValues, new HashSet<>(Arrays.asList("dc.subject.authorkeywords")), "vgr:VgrExtension.vgr:Tag");
            result += insertOne(feed, keyedValues, new HashSet<>(Arrays.asList("dc.creator.forunit.id")), "vgr:VgrExtension.vgr:CreatedByUnit.id");

            if (result == 0) continue;
            Map<String, Object> sofia = vgr_ifeed_filter(
                    ((long) feed.get("id")) * -1,
                    null,
                    "vgr:VgrExtension.vgr:SourceSystem",
                    "SOFIA",
                    "matching",
                    filterSeq--,
                    null
            );
            main.insert("vgr_ifeed_filter", sofia);
        }
        System.out.println(filterSeq);

        main.update(
                "insert into vgr_ifeed_vgr_ifeed(composites_id, partof_id)\n" +
                        "select f1.id, f1.id*-1\n" +
                        "from vgr_ifeed f1 where f1.id < 0");

        main.commit();
    }


    static long filterSeq = -1;


    static int insertOne(Map<String, Object> feed, MultiMap<String, Map<String, Object>> keyedValues, Set<String> fromTag, String toTag) {
        if (keyedValues.isEmpty()) return 0;
        int result = 0;
        feed = new HashMap<>(feed);
        long id = ((long) feed.get("id")) * -1;
        feed.put("id", id);
        Map<String, Object> and;
        if (main.query("select * from vgr_ifeed where id = ?", 0, 1, id).isEmpty()) {
            main.insert("vgr_ifeed", feed);
            and = vgr_ifeed_filter(
                    id,
                    null,
                    null,
                    null,
                    "and",
                    filterSeq--,
                    null
            );
            main.insert("vgr_ifeed_filter", and);
            // main.commit();
            System.out.println("Skapade " + and);
        } else {
            System.out.println("Försöker hämta med params " + id + " and ");
            and = main.query(
                    "select * from vgr_ifeed_filter where ifeed_id = ? and operator = ?",
                    0, 1,
                    id, "and"
            ).get(0);
            System.out.println("Hämtade " + and);
        }
        return insertOne(feed, and, keyedValues, fromTag, toTag);
    }

    static int insertOne(Map<String, Object> feed, Map<String, Object> rootAnd, MultiMap<String, Map<String, Object>> keyedValues, Set<String> fromTag, String toTag) {
        int result = 0;
        for (String key : keyedValues.keySet()) {
            List<Map<String, Object>> values = keyedValues.get(key);
            if (values.size() > 1) {
                Map<String, Object> or = vgr_ifeed_filter(
                        null,
                        null,
                        null,
                        null,
                        "or",
                        filterSeq--,
                        (Long) rootAnd.get("id")
                );
                main.insert("vgr_ifeed_filter", or);
                result++;
                for (Map<String, Object> one : values) {
                    if (fromTag.contains(one.get("filterkey"))) {
                        one.put("id", filterSeq--);
                        one.put("ifeed_id", null);
                        one.put("parent_id", or.get("id"));
                        one.put("filterkey", toTag);
                        main.insert("vgr_ifeed_filter", one);
                        result++;
                    }
                }
            } else {
                Map<String, Object> one = values.iterator().next();
                if (fromTag.contains(one.get("filterkey"))) {
                    one.put("id", filterSeq--);
                    one.put("ifeed_id", null);
                    one.put("parent_id", rootAnd.get("id"));
                    one.put("filterkey", toTag);
                    main.insert("vgr_ifeed_filter", one);
                    result++;
                }
            }
            // main.commit();
        }

        return result;
    }


    static String toJavaFactoryMethod(Table table) throws ClassNotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append("public static Map<String, Object> " + table.getTableName() + "(");
        List<String> args = new ArrayList<>();
        List<String> assignments = new ArrayList<>();
        for (Column column : table.getColumns()) {
            args.add(Class.forName(column.getColumnClassName()).getSimpleName() + " " + column.getColumnName());
            assignments.add(String.format("result.put(\"%s\", %s);", column.getColumnName(), column.getColumnName()));
        }
        sb.append(String.join(", ", args));
        sb.append("){\n");
        sb.append("Map<String, Object> result = new HashMap();\n");
        sb.append(String.join("\n", assignments));
        sb.append("\nreturn result;\n");
        sb.append("}\n");
        return sb.toString();
    }

    public static Map<String, Object> vgr_ifeed(Long id, String description, Boolean linknativedocument, String name, String sortdirection, String sortfield, Timestamp timestamp, String userid, Long version, Long department_id, Long group_id) {
        Map<String, Object> result = new HashMap();
        result.put("id", id);
        result.put("description", description);
        result.put("linknativedocument", linknativedocument);
        result.put("name", name);
        result.put("sortdirection", sortdirection);
        result.put("sortfield", sortfield);
        result.put("timestamp", timestamp);
        result.put("userid", userid);
        result.put("version", version);
        result.put("department_id", department_id);
        result.put("group_id", group_id);
        return result;
    }

    public static Map<String, Object> vgr_ifeed_filter(Long ifeed_id, String filter, String filterkey, String filterquery, String operator, Long id, Long parent_id) {
        Map<String, Object> result = new HashMap();
        result.put("ifeed_id", ifeed_id);
        result.put("filter", filter);
        result.put("filterkey", filterkey);
        result.put("filterquery", filterquery);
        result.put("operator", operator);
        result.put("id", id);
        result.put("parent_id", parent_id);
        return result;
    }

    public static Map<String, Object> vgr_ifeed_vgr_ifeed(Long partof_id, Long composites_id) {
        Map<String, Object> result = new HashMap();
        result.put("partof_id", partof_id);
        result.put("composites_id", composites_id);
        return result;
    }

    public static void core_ArchivalObject_core_Producer() {
        /*
        dc.creator.recordscreator.id (Arkivbildare)
        dc.creator.recordscreator.id (tre värden som översätts till sitt namn)

        "SE2321000131-E000000011326" ;1
        "SE2321000131-E000000000106" ;21
        "SE2321000131-E000000000108" ;168

        core_ArchivalObject_core_Producer
         */
    }

}
