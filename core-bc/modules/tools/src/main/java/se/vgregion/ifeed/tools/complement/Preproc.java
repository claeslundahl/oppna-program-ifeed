package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.common.utils.MultiMap;
import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Tuple;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Preproc {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        getOrCreateAllHiddenFieldInfs();
    }

    static public List<Tuple> getOrCreateAllHiddenFieldInfs() {
        List<Tuple> result = new ArrayList<>();
        // From: https://vgregion.sharepoint.com/:x:/r/sites/sy-rs-systemstod-for-styrande-dokument/_layouts/15/Doc.aspx?sourcedoc=%7B25E3A6DE-11AC-4E7E-B142-5BBA4375C5F9%7D&file=Barium-s%C3%B6krapport%20iFeed.xlsx&action=default&mobileredirect=true
        String[] stuff = ("vgrsy:DomainExtension.vgrsy:SubjectLocalClassification\tEgen ämnesindelning\n" +
                "vgr:VgrExtension.vgr:Tag\tFöretagsnyckelord\n" +
                "vgr:VgrExtension.vgr:PublishedForUnit.id\tUpprättat för enhet\n" +
                "vgr:VgrExtension.vgr:CreatedByUnit.id\tUpprättat av enhet\n" +
                "core:ArchivalObject.core:Producer\tMyndighet\n" +
                "DIDL.Item.Descriptor.Statement.vgrsd:DomainExtension\t\"Domän Styrande dokument\"\n" +
                "vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path\tKodverk (SweMeSH)\n" +
                "vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path\tVerksamhetskod\n" +
                "vgr:VgrExtension.vgr:Source.id\tDokumentid källa\n" +
                "\tKodverk (Dokumentstruktur VGR)\n" +
                "vgr:VgrExtension.vgr:Title\tTitel\n" +
                "\tKodverk (Legitimerade yrken, Specialistutbildningar, HosPersKat)\n" +
                "vgrsd:DomainExtension.vgrsd:ValidTo\tGiltighetsdatum tom").split(Pattern.quote("\n"));

        //Set<String> isInDbNow = getHiddenFields().stream().map(f -> f.get("id").toString()).collect(Collectors.toSet());

        Set<String> ids = Arrays.stream(stuff).map(r -> r.split(Pattern.quote("\t"))[0])
                .filter(s -> !"".equals(s.trim())).collect(Collectors.toSet());

        DatabaseApi database = DatabaseApi.getLocalApi();
        GoverningDocComplettion gdc = new GoverningDocComplettion(database);

        MultiMap r = new MultiMap();
        System.out.println("Antal idn: " + ids.size());
        for (String id : ids) {
            System.out.println(id);
            List<Tuple> items = database.query("select * from field_inf where id = ?", id);
            if (items.isEmpty()) {
                r.get(id);
            } else
                for (Tuple item : items) {
                    if (gdc.getHiddenFields().contains(item))
                        continue;
                    item.remove("pk");
                    item.put("in_html_view", false);
                    item.put("filter", false);
                    item.put("expanded", false);
                    r.get(id).add(item);
                    break;
                }
        }
        System.out.println(gson.toJson(r));
        System.out.println("Antal: " + r.size());

        /*System.out.println(new File(".").getAbsolutePath());
        System.out.println(Preproc.class.getResource(Preproc.class.getSimpleName() + ".class"));*/

        for (Tuple hiddenField : gdc.getHiddenFields()) {
            System.out.println(hiddenField.get("id") + " " + hiddenField.get("name"));
        }

        return result;
    }
}
