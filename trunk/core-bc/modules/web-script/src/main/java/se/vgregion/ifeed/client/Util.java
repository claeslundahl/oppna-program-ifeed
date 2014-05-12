package se.vgregion.ifeed.client;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by clalu4 on 2014-04-24.
 */
public class Util {

    static DateTimeFormat sdf = new DateTimeFormat("yyyy-MM-dd", new DefaultDateTimeFormatInfo()) {};

    private static Set<String> timeStampFieldNames = new HashSet<String>(
            Arrays.asList("dc.date.issued", "dc.date.validfrom", "dc.date.validto")
    );

    // "2014-03-11T10:21:38Z"
    //static DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

    static String currentTextDate;

    static {
        currentTextDate = sdf.format(new Date());
    }

    static String validToKey = "dc.date.validto", validFromKey = "dc.date.validfrom";

    public static String timeStampTodate(String asText) {
        if (asText != null && !"".equals(asText.trim())) {
            return asText.substring(0, asText.indexOf("T"));
        } else {
            return "";
        }
    }

    public static String formatValueForDisplay(HasGetter entry, String key) {
        if (timeStampFieldNames.contains(key)) {
            return timeStampTodate(entry.get(key));
        }
        return entry.get(key);
    }

    public static boolean isTimeStampPassed(String timeStampAsText) {
        if (timeStampAsText == null || "".equals(timeStampAsText.trim())) {
            return false;
        }
        timeStampAsText = timeStampAsText.substring(0, 10);
        return timeStampAsText.compareTo(currentTextDate) < 0;
    }

    native public static void consoleLog(Object message) /*-{
      if (window['console']) console.log(message);
    }-*/;
}
