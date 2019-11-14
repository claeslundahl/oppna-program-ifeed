package se.vgregion.ifeed.formbean;

import java.util.Date;

import junit.framework.Assert;

import se.vgregion.common.utils.BeanMap;
import org.junit.Before;
import org.junit.Test;

public class FilterFormBeanTest {

    FilterFormBean bean;

    @Before
    public void setUp() throws Exception {
        bean = new FilterFormBean();
    }

    @Test
    public void testGetterSetterMirrorsEachOther() {
        BeanMap bm = new BeanMap(bean);
        bm.setBean(bean);
        for (Object key : bm.keySet()) {
            Class<?> type = bm.getType(key.toString());
            Object value = null;

            if (type.isAssignableFrom(String.class)) {
                value = "value";
            }
            if (type.isAssignableFrom(Integer.class) || type.isPrimitive()) {
                value = 101;
            }

            if (value != null) {
                bm.put((String) key, value);
                Assert.assertEquals(value, bm.get(key));
            }
        }
    }

    @Test
    public void setFilterValueDate() {
        Date d = new Date(0);
        bean.setFilterValue(d);

        int year = bean.getValidFromYear();
        int month = bean.getValidFromMonth();
        int day = bean.getValidFromDay();

        Assert.assertEquals(1970, year);
        Assert.assertEquals(0, month);
        Assert.assertEquals(1, day);
    }



}
