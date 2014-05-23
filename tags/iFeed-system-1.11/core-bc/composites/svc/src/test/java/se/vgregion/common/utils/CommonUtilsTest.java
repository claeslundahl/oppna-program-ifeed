package se.vgregion.common.utils;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;

import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.types.IFeed;

public class CommonUtilsTest {

    @Test
    public void getEnum() {
        try {
            Class<SortDirection> clazz = null;
            CommonUtils.getEnum(clazz, "");
            fail("Should throw " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException npe) {
            Assert.assertTrue(true);
        }

        SortDirection result = CommonUtils.getEnum(SortDirection.class, "asc");
        Assert.assertEquals("asc", result.toString());
    }

    @Test
    public void isNull() {
        Assert.assertTrue(CommonUtils.isNull(null));

        Assert.assertFalse(CommonUtils.isNull(1));
        Assert.assertFalse(CommonUtils.isNull("boo bar baz"));
    }

    @Test
    public void serialize() {
        IFeed ifeed = new IFeed();
        String s = CommonUtils.toString(ifeed);
        IFeed unpacked = (IFeed) CommonUtils.toObject(s);
    }

}
