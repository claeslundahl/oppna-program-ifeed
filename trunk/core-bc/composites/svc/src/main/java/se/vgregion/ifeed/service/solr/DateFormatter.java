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
        SOLR_DATE_FORMAT(ISODateTimeFormat.dateTime()), W3CDTF(
                ISODateTimeFormat.dateTimeNoMillis()), TIME_ONLY(ISODateTimeFormat.hourMinuteSecond()), DATE_ONLY(ISODateTimeFormat.yearMonthDay());
        DateTimeFormatter formatter;

        DateFormats(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        public DateTimeFormatter formatter() {
            return formatter;
        }
    }

    public static String format(int year, final int month, final int date,
            final DateFormats format) {
        DateTime dt = new DateTime(year, month, date, 0, 0, 0, 0,
                DateTimeZone.UTC);

        if (format.equals(DateFormats.SOLR_DATE_FORMAT)) {
            return dt.toString(ISODateTimeFormat.dateTime());
        } else {
            return dt.toString(format.formatter);
        }
    }

    public static Date parse(final String dateString, final DateFormats format) {
        return format.formatter.parseDateTime(dateString).toDate();
    }

    public static String format(final Date date, final DateFormats dateFormat) {
        DateTime dt = new DateTime(date.getTime());
        return dt.toString(dateFormat.formatter);
    }

    public static boolean validate(final String solrDateString, final DateFormats dateFormat) {
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
