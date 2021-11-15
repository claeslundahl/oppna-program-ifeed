package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultFilter extends Tuple {

    public static List<DefaultFilter> toDefaultFilters(Collection<Map<String, Object>> from) {
        List<DefaultFilter> result = new ArrayList<>();
        for (Map<String, Object> map : from) {
            result.add(new DefaultFilter(map));
        }
        return result;
    }

    public DefaultFilter(Map<String, Object> initial) {
        super(initial);
    }

    public DefaultFilter() {
        super();
    }

    public se.vgregion.ifeed.types.DefaultFilter toJpaVersion() {
        se.vgregion.ifeed.types.DefaultFilter result = new se.vgregion.ifeed.types.DefaultFilter();
        result.setFilterKey((String) get("filterkey"));
        result.setFilterQuery((String) get("filterquery"));
        result.setId((Long) get("id"));
        return result;
    }
}
