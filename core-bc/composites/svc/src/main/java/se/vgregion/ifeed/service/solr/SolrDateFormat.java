/**
 * 
 */
package se.vgregion.ifeed.service.solr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * @author anders
 * 
 */
public class SolrDateFormat {


    private static final DateFormat SOLR_DATE_FORMAT = getSolrDateFormat();
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'";
    private static final String ZULU_TIMEZONE = "Zulu";

    private static DateFormat getSolrDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(ZULU_TIMEZONE));

        return dateFormat;
    }

    public static String format(String year, String month, String date) {
        // SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        // f.setTimeZone(TimeZone.getTimeZone(ZULU_TIMEZONE));
        // Date d = null;
        // try {
        // d = f.parse(year + month + date);
        // } catch (ParseException e) {
        // throw new RuntimeException("Unable to parse date", e);
        // }
        // return format(d);
        return year + "-" + StringUtils.right("0"+month,2) + "-" + StringUtils.right("0" + date, 2) + "T00:00:00.000Z";
    }

    public static String format(Date date) {
        return SOLR_DATE_FORMAT.format(date);
    }

    public static boolean validate(String solrDateString) {
        try {
            SOLR_DATE_FORMAT.parse(solrDateString);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
