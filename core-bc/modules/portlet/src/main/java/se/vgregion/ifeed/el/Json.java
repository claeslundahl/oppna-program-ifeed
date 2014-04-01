package se.vgregion.ifeed.el;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.vgregion.ifeed.formbean.BaseVgrOrganization;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ldap.LdapSupportService;

@Component
public class Json {

    private static LdapSupportService ldapSupportService;

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectMapper().writeValue(baos, obj);
            baos.flush();
            baos.close();

            String r = new String(baos.toByteArray(), "UTF-8");
            return r;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        Json.ldapSupportService = ldapSupportService;
    }
}
