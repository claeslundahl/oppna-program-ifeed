package se.vgregion.ifeed.service.kiv;

import org.junit.Test;
import se.vgregion.ldap.VgrOrganization;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class OrganizationsServiceTest {

    public static void main(String[] args) {
        List<VgrOrganization> tops = OrganizationsService.fetchOrganizations();
        for (VgrOrganization top : tops) {
            System.out.println(top);
        }
    }

}
