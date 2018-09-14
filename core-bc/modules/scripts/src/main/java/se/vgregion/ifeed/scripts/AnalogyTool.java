package se.vgregion.ifeed.scripts;

import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Column;
import se.vgregion.arbetsplatskoder.db.migration.sql.meta.Table;

import java.sql.Timestamp;
import java.util.*;

public class AnalogyTool {

    static Set<String> fromOwnClassificationTags = new HashSet<>(
            Arrays.asList(
                    "dc.publisher.project-assignment",
                    "dc.type.process.name",
                    "dc.type.file.process",
                    "dc.type.file",
                    "dc.identifier.diarie.id",
                    "dc.type.document.serie",
                    "dc.type.document.id"
            )
    );

    static String ownClassificationTag = "vgrsy_DomainExtension_vgrsy_SubjectLocalClassification";

    static Set<String> allTagsToConsider = new HashSet<>();

    static {
        allTagsToConsider.addAll(fromOwnClassificationTags);
        allTagsToConsider.add("dc.publisher.forunit");
        allTagsToConsider.add("dc.source.origin");
    }


    static ConnectionExt main = CreateAnalogNewTags.getStageConnectionExt();

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(main.getUrl());

        if(true) return;

/*        System.out.println(toJavaFactoryMethod(main.getSchemas("public").get(0).getTable("vgr_ifeed_vgr_ifeed")));
        if(true)return;*/

        List<Map<String, Object>> feeds = main.query("select * from vgr_ifeed", 0, 1_000_000);
        for (Map<String, Object> feed : feeds) {
            // System.out.println(feed);
            List<Map<String, Object>> filters = main.query(
                    "select * from vgr_ifeed_filter where ifeed_id = ?",
                    0,
                    1_000_000,
                    feed.get("id")
            );
            MultiMap<String, Map<String, Object>> keyedValues = new MultiMap<>();
            for (Map<String, Object> filter : filters) {
                keyedValues.get(filter.get("filterkey")).add(filter);
            }

            keyedValues.keySet().retainAll(allTagsToConsider);

            insertOne(feed, keyedValues, fromOwnClassificationTags, ownClassificationTag);
            insertOne((feed), keyedValues, new HashSet<>(Arrays.asList("dc.publisher.forunit")), "vgr_VgrExtension_vgr_PublishedForUnit_id");
            insertOne((feed), keyedValues, new HashSet<>(Arrays.asList("dc.source.origin")), "vgr_VgrExtension_vgr_SourceSystem");
        }
        System.out.println(filterSeq);

        main.update(
                "insert into vgr_ifeed_vgr_ifeed(composites_id, partof_id)\n" +
                        "select f1.id, f1.id*-1\n" +
                        "from vgr_ifeed f1 where f1.id < 0");

        main.commit();
    }

    static long filterSeq = -1;


    static void insertOne(Map<String, Object> feed, MultiMap<String, Map<String, Object>> keyedValues, Set<String> fromTag, String toTag) {
        if (keyedValues.isEmpty()) return;
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
        insertOne(feed, and, keyedValues, fromTag, toTag);
    }

    static void insertOne(Map<String, Object> feed, Map<String, Object> rootAnd, MultiMap<String, Map<String, Object>> keyedValues, Set<String> fromTag, String toTag) {
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
                for (Map<String, Object> one : values) {
                    if (fromTag.contains(one.get("filterkey"))) {
                        one.put("id", filterSeq--);
                        one.put("ifeed_id", null);
                        one.put("parent_id", or.get("id"));
                        one.put("filterkey", toTag);
                        main.insert("vgr_ifeed_filter", one);
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
                }
            }
            // main.commit();
        }
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

}
