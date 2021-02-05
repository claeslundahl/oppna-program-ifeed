package se.vgregion.ifeed.tools;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetadataSwap {

    /*public static final String nested_filter_key = "exp_fk_ifeed_filter_parent";

    public static final String vgr_ifeed_filter_key = "exp_fk7c4b00ce23437911";*/

    public static final CorrespondingKeys subjectLocalClassification = new CorrespondingKeys(
            "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification",
            "dc.publisher.project-assignment", "dc.type.process.name", "dc.type.file.process", "dc.type.file", "dc.type.document.serie", "dc.type.document.id", "dc.type.document", "dc.type.record", "dc.creator.project-assignment", "dc.type.document.structure" //, "dc.subject.authorkeywords"
    );

    public static final CorrespondingKeys authorCompanyKeyword = new CorrespondingKeys("vgr:VgrExtension.vgr:Tag", "dc.subject.authorkeywords");

    public static final CorrespondingKeys publishedForUnit = new CorrespondingKeys(
            "vgr:VgrExtension.vgr:PublishedForUnit.id", "dc.publisher.forunit.id"
    );

    /*public static final CorrespondingKeys tag = new CorrespondingKeys(
            "vgr:VgrExtension.vgr:CreatedByUnit.id", "dc.subject.authorkeywords"
    );*/

    public static final CorrespondingKeys forunitId = new CorrespondingKeys(
            // "vgr:VgrExtension.vgr:CreatedByUnit.id", "dc.creator.recordscreator.id"
            "vgr:VgrExtension.vgr:CreatedByUnit.id", "dc.creator.forunit.id"
    );

    public static final CorrespondingKeys recordscreatorId = new CorrespondingKeys(
            "core:ArchivalObject.core:Producer", "dc.creator.recordscreator.id"
    );

    public static final List<CorrespondingKeys> CORRESPONDING_KEYS = new ArrayList<>(
            Arrays.asList(subjectLocalClassification, publishedForUnit, /*tag,*/ forunitId, recordscreatorId, authorCompanyKeyword)
    );

    public static void main(String[] args) {
        // System.out.println(String.join("', '", subjectLocalClassification.fromThese));
        /*for (CorrespondingKeys correspondingKey : CORRESPONDING_KEYS) {
            System.out.println(correspondingKey.newKey + "\t"
                    + correspondingKey.fromThese.toString().replaceAll("['\\[''\\]']",""));
        }*/

        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(CORRESPONDING_KEYS));
    }

}
