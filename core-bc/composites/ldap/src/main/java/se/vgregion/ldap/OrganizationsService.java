package se.vgregion.ldap;

import se.vgregion.common.utils.BeanMap;
import se.vgregion.common.utils.ComparableArrayList;

import java.util.*;
import java.util.regex.Pattern;

public class OrganizationsService {

    public static VgrOrganization loadAllOrganizationsRoot() {
        final VgrOrganization result = getVgrOrganization();
        /*result.setDn("Ou=org");
        result.setOpen(true);*/
        // loadAllOrganizations(result);
        result.getChildren().addAll(fetchOrganizationsFromKivLdap());
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

    public static List<VgrOrganization> fetchOrganizationsFromKivLdap() {
        LdapApi kiv = LdapApi.newInstanceFromConfig();
        kiv.setResultLimit(1_000_000);
        List<Map<String, Object>> items = kiv.query("objectClass=organizationalUnit");
        items.addAll(kiv.query("objectClass=organizationalRole"));

        Map<String, Object> top = putEntriesInOrder(items, "[o=VGR, ou=Org]");

        List<VgrOrganization> result = toVgrOrganizations((List<Map<String, Object>>) top.get("children"));
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

    public static Map<String, Object> putEntriesInOrder(List<Map<String, Object>> items, String returnReversedDn) {
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
    }

}
