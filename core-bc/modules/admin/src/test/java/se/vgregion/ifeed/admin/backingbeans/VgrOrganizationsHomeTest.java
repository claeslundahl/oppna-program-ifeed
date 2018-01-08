package se.vgregion.ifeed.admin.backingbeans;

import org.junit.Ignore;
import org.junit.Test;
import se.vgregion.ifeed.admin.formbean.VgrOrganization;

import java.util.ArrayList;
import java.util.List;

public class VgrOrganizationsHomeTest {

    @Ignore
    @Test
    public void loadChildrenFlat() {
        VgrOrganizationsHome vgrOrganizationsHome = new VgrOrganizationsHome();
        Application application = new Application();
        List<VgrOrganization> organizations = new ArrayList<VgrOrganization>();
        VgrOrganization organization = new VgrOrganization();
        vgrOrganizationsHome.loadChildrenFlat(application, organization);
    }

}