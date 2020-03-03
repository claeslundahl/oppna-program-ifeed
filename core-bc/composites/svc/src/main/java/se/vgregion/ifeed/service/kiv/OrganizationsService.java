package se.vgregion.ifeed.service.kiv;

import se.vgregion.common.utils.BeanMap;
import se.vgregion.common.utils.ComparableArrayList;
import se.vgregion.ifeed.service.kiv.organization.Attributes;
import se.vgregion.ifeed.service.kiv.organization.Unit;
import se.vgregion.ldap.VgrOrganization;

import java.util.*;
import java.util.regex.Pattern;

public class OrganizationsService {

    public static VgrOrganization loadAllOrganizationsRoot() {
        final VgrOrganization result = getVgrOrganization();
        result.getChildren().addAll(fetchOrganizations());
        result.init();
        return result;
    }

    static VgrOrganization getVgrOrganization() {
        VgrOrganization vgr = new VgrOrganization("Ou=org", "SE2321000131-E000000000001");
        vgr.setOu("Västra Götalandsregionen");
        vgr.setOpen(true);
        vgr.setLevel(0);
        return vgr;
    }

    public static List<VgrOrganization> fetchOrganizations() {
        UnitSearchService uss = UnitSearchService.newInstanceFromConfig();
        uss.update();
        List<Unit> units = new ArrayList<>();
        units.addAll(uss.getRolesRoot().getRoles());
        units.addAll(uss.getUnitsRoot().getUnits());
        List<VgrOrganization> result = toVgrOrganizations(units);

        // This is the top of the tree. Is not present in the service, strangely enough.
        VgrOrganization vgr = new VgrOrganization();
        vgr.setDn("ou=Org,o=VGR");
        vgr.setOu("Västra Götalandsregionen");
        vgr.setHsaIdentity("SE2321000131-E000000000001");
        result.add(vgr);

        VgrOrganization top = putEntriesInOrder(result, "[o=VGR, ou=Org]");
        //result = top.getChildren();
        vgr.getChildren().addAll(top.getChildren());
        result = Arrays.asList(vgr);
        result.forEach(c -> c.init());
        return result;
    }

    private static List<VgrOrganization> toVgrOrganizations(List<Map<String, Object>> fromThese) {
        if (fromThese == null || fromThese.isEmpty()) return new ArrayList<>();
        List<VgrOrganization> result = new ArrayList<>();
        for (Map<String, Object> item : fromThese) {
            VgrOrganization vo = new VgrOrganization();
            new BeanMap(vo).putAllApplicable(item);
            vo.getChildren().clear();
            List<Map<String, Object>> children = (List<Map<String, Object>>) item.get("children");
            vo.getChildren().addAll(toVgrOrganizations(children));
            Collections.sort(vo.getChildren(), Comparator.comparing(VgrOrganization::getLabel));
            result.add(vo);
        }
        return result;
    }

    static ComparableArrayList toIdList(Map<String, Object> item) {
        ComparableArrayList result = new ComparableArrayList();
        String[] parts = item.get("dn").toString().split(Pattern.quote(","));
        result.addAll(Arrays.asList(parts));
        Collections.reverse(result);
        return result;
    }

    static ComparableArrayList toIdList(VgrOrganization item) {
        ComparableArrayList result = new ComparableArrayList();
        String[] parts = item.getDn().split(Pattern.quote(","));
        result.addAll(Arrays.asList(parts));
        Collections.reverse(result);
        return result;
    }

    /*public static Map<String, Object> putEntriesInOrder(List<Map<String, Object>> items, String returnReversedDn) {
        NavigableMap<ComparableArrayList, Map<String, Object>> index = new TreeMap<>();
        items.forEach(i -> index.put(toIdList(i), i));
        Map<String, Object> top = null;
        for (List<String> id : index.keySet()) {
            Map<String, Object> current = index.get(id);
            ComparableArrayList parentId = new ComparableArrayList(id);
            if (id.toString().equals(returnReversedDn)) {
                if (top != null) {
                    throw new RuntimeException();
                }
                top = current;
                continue;
            }
            parentId.remove(parentId.size() - 1);
            Map<String, Object> parent = index.get(parentId);
            if (parent == null) continue;
            if (!parent.containsKey("children"))
                parent.put("children", new ArrayList<>());
            List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
            children.add(current);
        }
        return top;
    }*/

    public static VgrOrganization putEntriesInOrder(List<VgrOrganization> items, String returnReversedDn) {
        NavigableMap<ComparableArrayList, VgrOrganization> index = new TreeMap<>();
        items.forEach(i -> index.put(toIdList(i), i));
        VgrOrganization top = null;
        for (List<String> id : index.keySet()) {
            VgrOrganization current = index.get(id);
            ComparableArrayList parentId = new ComparableArrayList(id);
            if (id.toString().equalsIgnoreCase(returnReversedDn)) {
                if (top != null) {
                    throw new RuntimeException();
                }
                top = current;
                continue;
            }
            parentId.remove(parentId.size() - 1);
            VgrOrganization parent = index.get(parentId);
            if (parent == null) continue;
            parent.getChildren().add(current);
        }
        return top;
    }


    static VgrOrganization toVgrOrganization(Unit unit) {
        VgrOrganization result = new VgrOrganization();
        Attributes attributes = unit.getAttributes();
        new BeanMap(result).putAllApplicable(new BeanMap(attributes));
        result.setDn(unit.getDn());
        return result;
    }

    static List<VgrOrganization> toVgrOrganizations(Collection<Unit> units) {
        List<VgrOrganization> result = new ArrayList<>();
        for (Unit unit : units) {
            result.add(toVgrOrganization(unit));
        }
        return result;
    }

}
