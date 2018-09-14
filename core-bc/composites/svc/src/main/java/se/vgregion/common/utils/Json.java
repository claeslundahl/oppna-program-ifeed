package se.vgregion.common.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class Json {

    // private static LdapSupportService ldapSupportService;

    private static Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            Expose expose = f.getAnnotation(Expose.class);
            String getterNamePostfix = (f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1));
            try {
                f.getDeclaringClass().getMethod("get" + getterNamePostfix);
            } catch (NoSuchMethodException e) {
                try {
                    f.getDeclaringClass().getMethod("is" + getterNamePostfix);
                } catch (NoSuchMethodException e1) {
                    return true;
                }
            }
            if (expose != null) {
                if (!expose.serialize()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }).setDateFormat("yyyy-MM-dd HH:mm:ss SSS").create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String toJsonOld(Object obj) {
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

     public static <T> T toObject(Class<T> clazz, String json) {
        return gson.fromJson(json, clazz);
    }

/*
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
    }*/
}
