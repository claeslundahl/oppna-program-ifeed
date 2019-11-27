package se.vgregion.ifeed.backingbeans;

import com.google.gson.GsonBuilder;
import org.junit.Ignore;
import org.junit.Test;
import se.vgregion.ifeed.formbean.VgrOrganization;

import java.util.ArrayList;
import java.util.List;

public class VgrOrganizationsHomeTest {

/*    @Ignore
    @Test
    public void loadChildrenFlat() {
        VgrOrganizationsHome vgrOrganizationsHome = new VgrOrganizationsHome();
        Application application = new Application();
        List<VgrOrganization> organizations = new ArrayList<VgrOrganization>();
        VgrOrganization organization = new VgrOrganization();
        vgrOrganizationsHome.loadChildrenFlat(application, organization);
    }*/

    public static void main(String[] args) {
        List<VgrOrganization> result = VgrOrganizationsHome.fetchOrganizationsFromKivLdap();
        VgrOrganizationsHome.getAllOrganizationsRootFromDiscCache();
        System.out.println(new GsonBuilder().create().toJson(result));
        System.out.println(result);
    }

}
