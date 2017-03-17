package se.vgregion.ifeed.client;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

public class UtilTest extends TestCase {

    @Test
    public void testTimeStampTodate() {
        String s = Util.timeStampTodate("2014-03-11T10:21:38Z");
        System.out.println(s);
    }

    @Test
    public void testIsTimeStampPassed() {
        boolean b = Util.isTimeStampPassed("2014-03-11T10:21:38Z");
        Assert.assertTrue(b);

        b = Util.isTimeStampPassed("2033-03-11T10:21:38Z");
        Assert.assertTrue(!b);

        b = Util.isTimeStampPassed(null);
        Assert.assertTrue(!b);

        b = Util.isTimeStampPassed(" ");
        Assert.assertTrue(!b);
    }

}