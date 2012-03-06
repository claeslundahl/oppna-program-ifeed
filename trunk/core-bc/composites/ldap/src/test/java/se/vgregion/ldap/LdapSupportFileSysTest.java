package se.vgregion.ldap;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LdapSupportFileSysTest {

    LdapSupportFileSys support;

    List<String> path;

    File root;

    @Before
    public void setUp() throws Exception {
        path = Arrays.asList(new String[] { "Ou=org", "Ou=org1" });

        root = new File(
                "C:\\Projects\\Portal\\oppna-program-ifeed\\trunk\\core-bc\\composites\\ldap\\src\\test\\resources");
        support = new LdapSupportFileSys(root);
    }

    @Test
    @Ignore
    public void findChildNodes() {
        Bean bean = new Bean();
        bean.setDn("Ou=org");
        List<Bean> result = support.findChildNodes(bean);

    }

    @Test
    @Ignore
    public void findDirectory() {
        File result = support.findDirectory(root, path);
        System.out.println(result);
        Assert.assertTrue(result.exists());
    }

    @Test
    public void toFilePath() {
        String result = support.toFilePath(path);
        System.out.println(result);
    }

    public static class Bean implements HasCommonLdapFields {

        private String dn, ou;

        @Override
        public String getDn() {
            return dn;
        }

        @Override
        public void setDn(String dn) {
            this.dn = dn;
        }

        @Override
        public String getOu() {
            return ou;
        }

        @Override
        public void setOu(String ou) {
            this.ou = ou;
        }

    }

}
