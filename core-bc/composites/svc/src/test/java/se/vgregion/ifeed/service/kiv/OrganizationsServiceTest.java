package se.vgregion.ifeed.service.kiv;

import org.junit.Test;
import se.vgregion.ldap.VgrOrganization;

import java.util.List;

import static org.junit.Assert.*;

public class OrganizationsServiceTest {

    @Test
    public void fetchOrganizations() {
        List<VgrOrganization> tops = OrganizationsService.fetchOrganizations();
        for (VgrOrganization top : tops) {
            System.out.println(top);
        }
    }
}
