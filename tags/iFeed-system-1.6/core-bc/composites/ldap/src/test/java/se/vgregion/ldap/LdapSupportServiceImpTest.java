package se.vgregion.ldap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LdapSupportServiceImpTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void mkFilter() {
        LdapSupportServiceImp serv = new LdapSupportServiceImp();

        Dummy dummy = new Dummy();
        String result = serv.mkFilter(dummy);
        Assert.assertEquals("(objectClass=*)", result);

        dummy.setBar("barValue");
        result = serv.mkFilter(dummy);
        Assert.assertEquals("(bar=barValue)", result);

        dummy.setBaz("bazValue");
        result = serv.mkFilter(dummy);
        Assert.assertEquals("(&(bar=barValue)(baz=bazValue))", result);

        dummy.setBar(null);
        result = serv.mkFilter(dummy);
        Assert.assertEquals("(baz=bazValue)", result);
    }

    public static class Dummy {

        private int foo;

        private String bar;

        private String baz;

        public int getFoo() {
            return foo;
        }

        public void setFoo(int foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBaz() {
            return baz;
        }

        public void setBaz(String baz) {
            this.baz = baz;
        }
    }

}