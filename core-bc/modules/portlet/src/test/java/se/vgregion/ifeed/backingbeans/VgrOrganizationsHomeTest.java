package se.vgregion.ifeed.backingbeans;

import se.vgregion.common.utils.BeanMap;
import se.vgregion.ifeed.service.kiv.*;
import se.vgregion.ifeed.service.kiv.organization.Attributes;
import se.vgregion.ifeed.service.kiv.organization.Unit;
import se.vgregion.ldap.VgrOrganization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<VgrOrganization> fromLdap = VgrOrganizationsHome.fetchOrganizationsFromKivLdap();
        UnitSearchService uss = UnitSearchService.newInstanceFromConfig();
        uss.update();
        UnitsRoot fromService = uss.getUnitsRoot();
        RolesRoot rolesRoot = uss.getRolesRoot();

/*        for (Unit unit : fromService.getUnits()) {
            if (unit.getAttributes().getCn() != null) {
                System.out.println("Hittade 1! " + new BeanMap(unit.getAttributes()));
            }
        }
        System.out.println();

        for (Unit unit : rolesRoot.getUnits()) {
            if (unit.getAttributes().getCn() != null) {
                System.out.println("Hittade 2! " + new BeanMap(unit.getAttributes()));
            }
        }*/

       //  if(true) return;

        final List<VgrOrganization> allFromLdap = new ArrayList<>();
        fromLdap.forEach(i -> i.visit(org -> allFromLdap.add(org)));

        Map<String, VgrOrganization> fromLdapMaps = new HashMap<>();
                /*allFromLdap.stream().collect(Collectors.toMap(VgrOrganization::getHsaIdentity,
                        Function.identity()));*/
        allFromLdap.forEach(c -> fromLdapMaps.put(c.getHsaIdentity(), c));

        Map<String, Unit> fromServiceMaps = new HashMap<>(fromService.getUnits().stream()
                .collect(Collectors.toMap(unit -> unit.getAttributes().getHsaIdentity()[0], unit -> unit)));

        rolesRoot.getUnits().forEach(r -> fromServiceMaps.put(
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
            result.setHsaIdentity(unit.getAttributes().getHsaIdentity()[0]);
        }
        // result.setCn(unit.getAttributes().);
        // result.setIo(unit.getAttributes().getIo);
        return result;
    }

    static String toHsaId(Attributes attributes) {
        if (attributes != null && attributes.getHsaIdentity() != null && attributes.getHsaIdentity().length > 0)
            return attributes.getHsaIdentity()[0];
        return null;
    }

    public static void getOrganisationsFromKivLdap() {

    }


}
