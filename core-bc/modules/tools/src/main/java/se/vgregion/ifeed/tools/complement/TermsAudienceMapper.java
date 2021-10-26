package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Filter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TermsAudienceMapper extends Mapper {

    private final Map<String, Filter> templates = new HashMap<>();

    private final FieldInf hosPersKat, legitimateProfession, specialistTraining;

    public TermsAudienceMapper(String fromKey, FieldInf hosPersKat, FieldInf legitimateProfession, FieldInf specialistTraining) {
        super(fromKey, null);
        this.hosPersKat = hosPersKat;
        this.legitimateProfession = legitimateProfession;
        this.specialistTraining = specialistTraining;
        init();
    }

    private void init() {
        templates.put("Annan vårdgivare", new Filter(Map.of("filterkey", hosPersKat.get("id"), "filterquery", "Annan vårdgivare"), hosPersKat));
        templates.put("Apotekare", new Filter(Map.of("filterkey", legitimateProfession.get("id"), "filterquery", "Apotekare"), legitimateProfession));
        templates.put("Bild- och funktionsmedicin", new Filter(Map.of("filterkey", specialistTraining.get("id"), "filterquery", "Bild- och funktionsmedicin"), specialistTraining));

        templates.put("Läkare",
                or(
                        new Filter(Map.of("filterkey", hosPersKat.get("id"), "filterquery", "Läkare"), hosPersKat),
                        new Filter(Map.of("filterkey", legitimateProfession.get("id"), "filterquery", "Läkare"), legitimateProfession)
                )
        );

        templates.put("Receptarie", new Filter(Map.of("filterkey", legitimateProfession.get("id"), "filterquery", "Receptarie"), legitimateProfession));
        templates.put("Sjuksköterska", new Filter(Map.of("filterkey", legitimateProfession.get("id"), "filterquery", "Sjuksköterska"), legitimateProfession));
        templates.put("Specialistsjuksköterska: Akutsjukvård med inriktning mot anestesisjukvård",
                new Filter(Map.of("filterkey", specialistTraining.get("id"), "filterquery",
                        "Specialistsjuksköterska: Akutsjukvård med inriktning mot anestesisjukvård"), specialistTraining));
    }

    static Filter or(Filter f1, Filter f2) {
        Filter result = new Filter();
        result.put("operator", "or");
        result.getChildren().add(f1);
        result.getChildren().add(f2);
        return result;
    }

    @Override
    public Filter convert(Filter that) {
        Filter template = templates.get(that.get("filterquery"));
        // template.put("field_inf_pk", template.getFieldInf().get("pk"));
        if (template == null) {
            return null;
        }
        return template;
        /*if (template.getChildren().isEmpty()) {
            Filter result = new Filter(template, template.getFieldInf());
            return result;
        } else {
            Filter result = new Filter(template);
            result.getChildren()
                    .addAll(template.getChildren().stream()
                            .map(c -> new Filter(c, c.getFieldInf())).collect(Collectors.toList()));
            return result;
        }*/
    }

}
