/**
 * 
 */
package se.vgregion.ifeed.service.solr;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * @author anders
 * 
 */
public class DateFormatter {

    public enum DateFormats {
        SOLR_DATE_FORMAT(ISODateTimeFormat.dateTime()), W3CDTF(ISODateTimeFormat.dateTimeNoMillis());
        DateTimeFormatter formatter;

        DateFormats(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        public DateTimeFormatter formatter() {
            return formatter;
        }
    }

    public static String format(int year, int month, int date, DateFormats format) {
        DateTime dt = new DateTime(year, month, date, 0, 0, 0, 0, DateTimeZone.UTC);

        if (format.equals(DateFormats.SOLR_DATE_FORMAT)) {
            return dt.toString(ISODateTimeFormat.dateTime());
        } else {
            return dt.toString(format.formatter);
        }
    }

    public static String format(Date date, DateFormats dateFormat) {
        DateTime dt = new DateTime(date.getTime());
        return dt.toString(dateFormat.formatter);
    }

    public static boolean validate(String solrDateString, DateFormats dateFormat) {
        try {
            dateFormat.formatter.parseDateTime(solrDateString);
        } catch (UnsupportedOperationException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
