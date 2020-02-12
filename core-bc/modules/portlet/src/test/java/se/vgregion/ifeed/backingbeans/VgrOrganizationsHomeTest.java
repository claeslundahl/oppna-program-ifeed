package se.vgregion.ifeed.backingbeans;

import se.vgregion.common.utils.BeanMap;
import se.vgregion.ifeed.service.kiv.*;
import se.vgregion.ifeed.service.kiv.organization.Attributes;
import se.vgregion.ifeed.service.kiv.organization.Roles;
import se.vgregion.ifeed.service.kiv.organization.Unit;
import se.vgregion.ifeed.service.kiv.organization.Units;
import se.vgregion.ldap.VgrOrganization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VgrOrganizationsHomeTest {

    public static void main(String[] args) {
        List<VgrOrganization> fromLdap = VgrOrganizationsHome.fetchOrganizations();
        UnitSearchService uss = UnitSearchService.newInstanceFromConfig();
        uss.update();
        Units fromService = uss.getUnitsRoot();
        Roles rolesRoot = uss.getRolesRoot();

        final List<VgrOrganization> allFromLdap = new ArrayList<>();
        fromLdap.forEach(i -> i.visit(org -> allFromLdap.add(org)));

        Map<String, VgrOrganization> fromLdapMaps = new HashMap<>();
        allFromLdap.forEach(c -> fromLdapMaps.put(c.getHsaIdentity(), c));

        Map<String, Unit> fromServiceMaps = new HashMap<>(fromService.getUnits().stream()
                .collect(Collectors.toMap(unit -> unit.getAttributes().getHsaIdentity().get(0), unit -> unit)));

        rolesRoot.getRoles().forEach(r -> fromServiceMaps.put(
                toHsaId(r.getAttributes()), r
        ));

        System.out.println("From ldap comes: " + allFromLdap.size() + " " + fromLdapMaps.size());
        System.out.println("From service comes: " + fromService.getUnits().size() + " " + fromServiceMaps.size());

        fromLdapMaps.keySet().removeAll(fromServiceMaps.keySet());
        System.out.println(fromLdapMaps.size());
        List<VgrOrganization> notInactive = new ArrayList<>();
        for (String hsaId : fromLdapMaps.keySet()) {
            VgrOrganization item = fromLdapMaps.get(hsaId);
            String text = new BeanMap(item).toString();
            if (text.contains("Inaktiva vårdgivare och vårdenheter")) {
                System.out.println(text);
            } else {
                notInactive.add(item);
            }
        }

        System.out.println();
        System.out.println("Inte inaktiva " + notInactive.size());
        for (VgrOrganization vgrOrganization : notInactive) {
            System.out.println(new BeanMap(vgrOrganization));
        }
    }

    static VgrOrganization toVgrOrganization(Unit unit) {
        VgrOrganization result = new VgrOrganization();
        result.setDn(unit.getDn());
        if (unit.getAttributes().getHsaIdentity() != null) {
            result.setHsaIdentity(unit.getAttributes().getHsaIdentity().get(0));
        }
        return result;
    }

    static String toHsaId(Attributes attributes) {
        if (attributes != null && attributes.getHsaIdentity() != null && attributes.getHsaIdentity().size() > 0)
            return attributes.getHsaIdentity().get(0);
        return null;
    }

}
