package se.vgregion.ifeed.service.solr;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateFormatterTest {

    @Test
    public void formatTextDate() {
        String result = DateFormatter.formatTextDate("2014-08-29T06:44:03Z");
        System.out.println(result);
    }
}