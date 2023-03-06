package se.vgregion.ifeed.service.util;

import lombok.Data;
import se.vgregion.ifeed.types.Metadata;

@Data
public class MetadataItem {
    private String path;
    private String name;
    private Object code;
    private String glossaryName;
    private String id;
    private Object glossaryVersion;

    public Metadata toMetadata() {
        Metadata result = new Metadata();
        String withFirstLetterUppercase = name.substring(0, 1).toUpperCase() + name.substring(1);
        result.setKey(withFirstLetterUppercase);
        result.setName(withFirstLetterUppercase);
        result.setActive(true);
        return result;
    }

}
