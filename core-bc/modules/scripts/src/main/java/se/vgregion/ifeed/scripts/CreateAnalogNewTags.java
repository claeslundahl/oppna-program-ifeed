package se.vgregion.ifeed.scripts;

import org.postgresql.Driver;
import se.vgregion.arbetsplatskoder.db.migration.sql.ConnectionExt;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static se.vgregion.ifeed.scripts.CopyDatabaseUtil.getMainConnectionExt;
import static se.vgregion.ifeed.scripts.CopyDatabaseUtil.getProperties;
import static se.vgregion.ifeed.scripts.CopyDatabaseUtil.toConnectionExt;

public class CreateAnalogNewTags {

    static ConnectionExt here = getStageConnectionExt();

    public static void main(String[] args) {
        int created = (createFeeds());
        System.out.println("Number of created shadow-feeds: " + created);
        insertTranslations();
        here.commit();
    }

    public static ConnectionExt getStageConnectionExt() {
        Properties prop = getStageConnectionProps();
        return toConnectionExt(prop);
    }

    private static Properties getStageConnectionProps() {
        Path path = (Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config-stage.properties"));
        return getProperties(path);
    }

    static void insertTranslations() {
        Set<String> set = new HashSet<>(
                Arrays.asList("dc.publisher.project-assignment", "dc.type.process.name",
                        "dc.type.file.process", "dc.type.file", "dc.identifier.diarie.id", "dc.type.document.serie", "dc.type.document.id"
                )
        );
        String newTagName = "vgrsy_DomainExtension_vgrsy_SubjectLocalClassification";
        insertComplementaryFilters(set, newTagName);

        insertComplementaryFilters(Arrays.asList("dc.publisher.forunit"), "vgr_VgrExtension_vgr_PublishedForUnit_id");
        insertComplementaryFilters(Arrays.asList("dc.source.origin"), "vgr_VgrExtension_vgr_SourceSystem");
    }

    static void insertComplementaryFilters(Collection<String> forThoseOldOnes, String newTagName) {
        int total = 0;
        System.out.println("Translates to " + newTagName);
        for (String s : forThoseOldOnes) {
            long now = System.currentTimeMillis();
            List<Map<String, Object>> results = here.query("select * from vgr_ifeed_filter where filterkey = ?", 0, 1_000_000, s);
            for (Map<String, Object> result : results) {
                long id = (long) result.get("ifeed_id");
                result.put("ifeed_id", id * -1);
                result.put("filterkey", newTagName);
                here.insert("vgr_ifeed_filter", result);
            }
            now = System.currentTimeMillis() - now;
            System.out.println(" " + s + " " + results.size() + "st. Took " + now + " ms.");
            total = +results.size();
        }
        System.out.println("Total: " + total + " items.");
    }


    static String fieldsPartSql = "(\n" +
            "\t\t'dc.publisher.project-assignment',\n" +
            "\t\t'dc.type.process.name',\n" +
            "\t\t'dc.type.file.process',\n" +
            "\t\t'dc.type.file',\n" +
            "\t\t'dc.identifier.diarie.id',\n" +
            "\t\t'dc.type.document.serie',\n" +
            "\t\t'dc.type.document.id', \n" +
            "\t\t'dc.publisher.forunit',\n" +
            "\t\t'dc.source.origin'\n" +
            "\t)\n";

    static int createFeeds() {
        String select = "select distinct id*-1 as id, description, linknativedocument, name, sortdirection, sortfield, timestamp, 'lifra1', version, department_id, group_id from vgr_ifeed where id in (\n" +
                "\tselect distinct ifeed_id from vgr_ifeed_filter where filterkey in " +
                fieldsPartSql +
                ")";

        System.out.print("Inserts new feeds (vgr_ifeed).");
        long now = System.currentTimeMillis();
        int r = here.update(
                "insert into vgr_ifeed(id, description, linknativedocument, name, sortdirection, sortfield, timestamp, userid, version, department_id, group_id)\n"
                        + select
        );
        now = System.currentTimeMillis() - now;
        System.out.println(" Took " + now + " ms.");

        now = System.currentTimeMillis();
        System.out.print("Inserts connections, binding new feeds to old (vgr_ifeed_vgr_ifeed). ");
        here.update(
                "insert into vgr_ifeed_vgr_ifeed(partof_id, composites_id)\n" +
                        "select i.id*-1, i.id from (\n"
                        + select
                        + ") i"
        );
        now = System.currentTimeMillis() - now;
        System.out.println(" Took " + now + " ms.");

        return r;
    }

}
