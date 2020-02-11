package se.vgregion.ifeed.el;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.ldap.BaseVgrOrganization;
import se.vgregion.ldap.VgrOrganization;
import se.vgregion.ldap.LdapSupportService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static se.vgregion.common.utils.Json.toJson;

public class IdentityUtil {

    private static LdapSupportService ldapSupportService;

    public static String vgrHsaIdToJson(String id, Integer maxHits) throws JsonGenerationException,
            JsonMappingException, IOException {

        List<VgrOrganization> findings = ldapSupportService.find(new BaseVgrOrganization("Ou=Org", id),
                VgrOrganization.class);

        if (maxHits != null && findings.size() > maxHits.intValue()) {
            VgrOrganization vo = new VgrOrganization();
            vo.setOu("Hsa-id strängen matchade " + findings.size() + " träffar.");
            findings = new ArrayList<VgrOrganization>();
            findings.add(vo);
        }

        return toJson(findings);
    }

    public static String dnToJson(String id, Integer maxHits) throws JsonGenerationException,
            JsonMappingException, IOException {

        BaseVgrOrganization sample = new BaseVgrOrganization();
        sample.setDn(id);

        List<VgrOrganization> findings = ldapSupportService.find(sample, VgrOrganization.class);

        if (maxHits != null && findings.size() > maxHits.intValue()) {
            VgrOrganization vo = new VgrOrganization();
            vo.setOu("Hsa-id strängen matchade " + findings.size() + " träffar.");
            findings = new ArrayList<VgrOrganization>();
            findings.add(vo);
        }

        return toJson(findings);
    }

    public LdapSupportService getLdapSupportService() {
        return ldapSupportService;
    }

    @Autowired(required = true)
    public void setLdapSupportService(LdapSupportService ldapSupportService) {
        IdentityUtil.ldapSupportService = ldapSupportService;
    }
}
