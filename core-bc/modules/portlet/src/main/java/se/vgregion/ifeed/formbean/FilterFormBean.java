package se.vgregion.ifeed.formbean;

import se.vgregion.ifeed.types.FilterType.Filter;

public class FilterFormBean {
    private Filter filter;
    private String filterValue;

    public FilterFormBean() {
    }

    public FilterFormBean(Filter filter, String filterValue) {
        this.filter = filter;
        this.filterValue = filterValue;
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
}
