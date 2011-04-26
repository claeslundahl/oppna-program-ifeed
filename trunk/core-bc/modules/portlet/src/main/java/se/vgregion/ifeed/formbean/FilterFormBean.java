package se.vgregion.ifeed.formbean;

import se.vgregion.ifeed.types.FilterType.Filter;

public class FilterFormBean {
    private Filter filter;
    private String filterValue;
    private String removeLink;

    public FilterFormBean() {
    }

    public FilterFormBean(Filter filter, String filterValue, String removeLink) {
        this.filter = filter;
        this.filterValue = filterValue;
        this.removeLink = removeLink;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getRemoveLink() {
        return removeLink;
    }

    public void setRemoveLink(String removeLink) {
        this.removeLink = removeLink;
    }

}
