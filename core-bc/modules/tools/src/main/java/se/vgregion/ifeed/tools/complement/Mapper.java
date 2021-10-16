package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Mapper {

    private final Set<String> fromKeys;

    private FieldInf fieldInf;

    public Mapper(Set<String> fromKeys, FieldInf fieldInf) {
        this.fromKeys = fromKeys;
        this.fieldInf = fieldInf;
    }

    public Mapper(String fromKey, FieldInf fieldInf) {
        this.fromKeys = new HashSet<>(Collections.singleton(fromKey));
        this.fieldInf = fieldInf;
    }

    public Filter convert(Filter that) {
        Filter result = new Filter(that);
        result.put("field_inf_pk", fieldInf.get("pk"));
        result.put("filterkey", fieldInf.get("id"));
        return result;
    }

    public FieldInf getFieldInf() {
        return fieldInf;
    }

    public void setFieldInf(FieldInf fieldInf) {
        this.fieldInf = fieldInf;
    }

    public Set<String> getFromKeys() {
        return fromKeys;
    }

}
