package se.vgregion.ifeed.formbean;

import se.vgregion.ifeed.types.FilterType.Filter;

public class FilterFormBean {
    private Filter filter;
    private String filterValue;
    private int validFromYear;
    private int validFromMonth;
    private int validFromDay;

    public FilterFormBean() {
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

    public int getValidFromYear() {
        return validFromYear;
    }

    public void setValidFromYear(int validFromYear) {
        this.validFromYear = validFromYear;
    }

    public int getValidFromMonth() {
        return validFromMonth;
    }

    public void setValidFromMonth(int validFromMonth) {
        this.validFromMonth = validFromMonth;
    }

    public int getValidFromDay() {
        return validFromDay;
    }

    public void setValidFromDay(int validFromDay) {
        this.validFromDay = validFromDay;
    }
}
