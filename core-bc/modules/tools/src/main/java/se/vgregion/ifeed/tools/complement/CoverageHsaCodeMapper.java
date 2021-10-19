package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CoverageHsaCodeMapper extends Mapper {

    private final Map<String, String> map = new HashMap<>();

    public CoverageHsaCodeMapper(Set<String> fromKeys, FieldInf fieldInf) {
        super(fromKeys, fieldInf);
        init();
    }

    public CoverageHsaCodeMapper(String fromKey, FieldInf fieldInf) {
        super(fromKey, fieldInf);
        init();
    }

    private void init() {
        map.put("Akutverksamhet vid sjukhus, specialiserad vård", "Akutverksamhet vid sjukhus, allmän");
        map.put("Ambulanstransportverksamhet", "Ambulanssjukvård");
        map.put("Andningsfysiologi", "Lungfysiologi");
        map.put("Astma, stödverksamhet", "Astma- och KOL-mottagning, primärvård");
        map.put("Bild- och funktionsmedicin", "Radiologi");
        map.put("Diabetes, stödverksamhet", "Diabetesmottagning, primärvård");
        map.put("Fotvård", "Medicinsk fotvård");
        map.put("Förebyggande hälso- och sjukvård, stödverksamhet", "Livsstillsmottagning, primärvård");
        map.put("Hälso- och sjukvård i särskilt boende", "Läkarverksamhet, särskilt boende");
        map.put("Laboratorieverksamhet, öppen vård", "Provtagningsverksamhet, öppen vård");
        map.put("Medicinsk njursjukvård", "Njurmedicin");
        map.put("Medicinsk njursjukvård, barn och ungdom", "Njurmedicin, barn och ungdom");
        map.put("Rättspsykiatriska undersökningar, verksamhet", "Rättspsykiatriska undersökningar, RMV");
        map.put("Sjukgymnastik", "Sjukgymnastik/Fysioterapi");
    }

    @Override
    public Filter convert(Filter that) {
        Filter result = super.convert(that);
        String replacement = map.get(that.get("filterquery"));
        if (replacement != null) {
            result.put("filterquery", replacement);
        }
        return result;
    }

   /* static Filter newFilter(FieldInf fi, String filterQuery) {
        Filter result = new Filter();
        result.setFieldInf(fi);
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
    }*/


}
