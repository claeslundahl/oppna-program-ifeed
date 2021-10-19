package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.tools.FieldInf;
import se.vgregion.ifeed.tools.Filter;
import se.vgregion.ifeed.types.IFeedFilter;

import java.util.Map;
import java.util.Set;

public class DocumentIdMapper extends Mapper {

    private static final SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    public DocumentIdMapper(Set<String> fromKeys, FieldInf fieldInf) {
        super(fromKeys, fieldInf);
    }

    public DocumentIdMapper(String fromKey, FieldInf fieldInf) {
        super(fromKey, fieldInf);
    }

    @Override
    public Filter convert(Filter that) {
        IFeedFilter iff = new IFeedFilter();
        iff.setFilterKey((String) that.get("filterkey"));
        iff.setFilterQuery((String) that.get("filterquery"));
        Filter result = super.convert(that);
        Result items = client.query(iff.toQuery(client.fetchFields()), 0, 1, "asc", null, "title");
        if (items == null || items.getResponse() == null || items.getResponse().getDocs() == null || items.getResponse().getDocs().isEmpty()) {
            return null;
        }
        Map<String, Object> first = items.getResponse().getDocs().get(0);
        result.put("filterquery", first.get("title"));
        return result;
    }

}
