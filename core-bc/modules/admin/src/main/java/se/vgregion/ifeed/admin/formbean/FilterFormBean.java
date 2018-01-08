package se.vgregion.ifeed.admin.formbean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.DateFormatter.DateFormat;

/**
 * @author bjornryding
 * 
 */
public class FilterFormBean implements Serializable {
    private static final long serialVersionUID = 1L;
    // private Filter filter;
    private String filterValue;
    private int validFromYear;
    private int validFromMonth;
    private int validFromDay;
    private String direction;
    private String sortField;
    private String filterTypeId;

    public FilterFormBean() {
        setFilterValue(new Date());
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    // public Filter getFilter() {
    // return filter;
    // }

    // public void setFilter(Filter filter) {
    // this.filter = filter;
    // }

    /**
     * @return
     */
    public String getFilterValue() {
        // if (filter != null && filter.getMetadataType() == MetadataType.DATE) {
        if (filterValue == null && validFromYear > 0) {
            filterValue = DateFormatter.format(validFromYear, validFromMonth + 1, validFromDay,
                    DateFormat.SOLR_DATE_FORMAT);
        }
        return filterValue;
    }

    /**
     * @param filterValue
     */
    public final void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * @param d
     */
    public final void setFilterValue(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        validFromYear = c.get(Calendar.YEAR);
        validFromMonth = c.get(Calendar.MONTH);
        validFromDay = c.get(Calendar.DATE);
    }

    /**
     * @param validFromYear
     */
    public final void setValidFromYear(int validFromYear) {
        this.validFromYear = validFromYear;
    }

    /**
     * @param validFromMonth
     */
    public final void setValidFromMonth(int validFromMonth) {
        // In Java Calendar January = 0, December = 11
        this.validFromMonth = validFromMonth;
    }

    /**
     * @param validFromDay
     */
    public final void setValidFromDay(int validFromDay) {
        this.validFromDay = validFromDay;
    }

    /**
     * @return
     */
    public final int getValidFromDay() {
        return validFromDay;
    }

    /**
     * @return
     */
    public final int getValidFromMonth() {
        return validFromMonth;
    }

    /**
     * @return
     */
    public final int getValidFromYear() {
        return validFromYear;
    }

    public String getFilterTypeId() {
        return filterTypeId;
    }

    public void setFilterTypeId(String filterTypeId) {
        this.filterTypeId = filterTypeId;
    }
}
