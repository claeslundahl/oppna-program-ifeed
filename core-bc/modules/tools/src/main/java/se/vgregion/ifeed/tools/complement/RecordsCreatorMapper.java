package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Filter;

import java.util.Set;

public class RecordsCreatorMapper extends Mapper {

    public RecordsCreatorMapper(Set<String> fromKeys, FieldInf fieldInf) {
        super(fromKeys, fieldInf);
    }

    @Override
    public Filter convert(Filter that) {
        Filter result = super.convert(that);
        String currentFilterQuery = (String) result.get("filterquery");
        if (currentFilterQuery.equals("SE2321000131-E000000000108") || currentFilterQuery.equals("Södra Älvsborgs Sjukhus")) {
            result.put("filterquery", "SE2321000131-E000000013465");
            return result;
        } else {
            return null;
        }
    }

}
