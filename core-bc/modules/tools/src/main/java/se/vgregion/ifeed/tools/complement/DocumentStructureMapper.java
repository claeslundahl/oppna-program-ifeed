package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentStructureMapper extends Mapper {

    private Map<String, Filter> mapping;

    private final GoverningDocumentComplementation governingDocumentComplementation;

    public DocumentStructureMapper(GoverningDocumentComplementation governingDocumentComplementation) {
        super("dc.type.document.structure", null);
        this.governingDocumentComplementation = governingDocumentComplementation;
    }

    @Override
    public Filter convert(Filter that) {
        String value = (String) that.get("filterquery");
        Filter template = getMapping().get(value);
        if (template == null) {
            /*System.out.println("Hittade inte mappning för " + value + " all keys are" + getMapping().keySet() +
                    " FLöde " + that.get("ifeed_id"));*/
            return null;
        }
        Filter result = new Filter(template);
        result.getChildren().addAll(template.getChildren());
        if (template.get("field_inf_pk") != null) {
            result.setFieldInf(template.getFieldInf());
        } else {
            /*result.getChildren().addAll(
                    template.getChildren()
                            .stream().map(c -> new Filter(c, c.getFieldInf())).collect(Collectors.toList())
            );*/
        }


        /*result.visit(foo-> {
            if (foo.containsKey("field_inf_pk")) {
                FieldInf fi = foo.getFieldInf();
                List<Filter> children = foo.getChildren();
                System.out.println(fi);
            }
        });*/

        List<Filter> children = result.getChildren();


        return result;
    }

    static Filter newFilter(FieldInf fi, String filterQuery) {
        Filter result = new Filter();
        result.setFieldInf(fi);
        result.put("field_inf_pk", fi.get("pk"));
        result.put("filterkey", fi.get("id"));
        result.put("filterquery", filterQuery);
        return result;
    }

    static Filter and(Filter f1, Filter f2) {
        Filter result = new Filter();
        result.put("operator", "and");
        result.getChildren().add(f1);
        result.getChildren().add(f2);
        return result;
    }

    public Map<String, Filter> getMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
            FieldInf handlingstyp =
                    new FieldInf(governingDocumentComplementation.getOrCreateFromOrInDatabase(new FieldInf("core:ArchivalObject.core:ObjectType", "Handlingstyp", null)));
            FieldInf giltighetsområde =
                    new FieldInf(governingDocumentComplementation.getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:ValidityArea", "Giltighetsområde", null)));
            mapping.put("Blankett", newFilter(handlingstyp, "Blankett, övrig"));
            mapping.put("Informerande dokument", newFilter(handlingstyp, "Rutin"));
            mapping.put("Patientinformation", newFilter(handlingstyp, "Patientinformation, referensexemplar"));
            mapping.put("Riktlinje förvaltning", and(newFilter(handlingstyp, "Riktlinje"), newFilter(giltighetsområde, "Lokalt")));
            mapping.put("Riktlinje koncern", and(newFilter(handlingstyp, "Riktlinje"), newFilter(giltighetsområde, "Övergripande")));
            mapping.put("Rutin förvaltning", and(newFilter(handlingstyp, "Rutin"), newFilter(giltighetsområde, "Lokalt")));
            mapping.put("Rutin koncern", and(newFilter(handlingstyp, "Rutin"), newFilter(giltighetsområde, "Övergripande")));
        }
        return mapping;
    }

}
