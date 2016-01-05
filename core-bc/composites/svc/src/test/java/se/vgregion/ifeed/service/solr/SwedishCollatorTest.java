package se.vgregion.ifeed.service.solr;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by clalu4 on 2016-01-04.
 */
public class SwedishCollatorTest {

    @Test
    public void haveUmlautChars() {
        boolean b = SwedishCollator.haveUmlautChars("Den här har umlauts i sig");
        Assert.assertTrue(b);
    }

    @Test
    public void replaceCharWhenBothHaveItOnSamePlace() {
        SwedishCollator.Pair pair = new SwedishCollator.Pair("häj", "sön");
        SwedishCollator.replaceCharWhenBothHaveItOnSamePlace(pair);
        Assert.assertEquals("hbj", pair.one);
        Assert.assertEquals("son", pair.two);
    }

}