/**
 * 
 */
package se.vgregion.ifeed.service.alfresco.store;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author anders
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfo {

    private static final String VGR_NAMESPACE = "vgr:";
    private String nodeRef;
    @JsonProperty("mimetype")
    private String mimeType;
    private String type;
    private boolean published;
    @JsonProperty("availablefrom")
    private Double availableFrom;
    private Map<String, Object> metadata;

    DocumentInfo() {
        // Used By Jackson Json Mapper
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getType() {
        return StringUtils.substringAfter(type, VGR_NAMESPACE);
    }

    public boolean getPublished() {
        return published;
    }

    public Date getAvailableFrom() {
        return new Date(availableFrom.longValue());
    }

    @SuppressWarnings("unchecked")
    public <T> T getMetadataValue(String metadataKey, Class<T> object) {
        final T metadataValue = (T) metadata.get(metadataKey);
        return metadataValue;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @JsonProperty("properties")
    void setMetadata(Map<String, Object> metadata) {
        Map<String, Object> vgrMetadata = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (entry.getKey().contains(VGR_NAMESPACE)) {
                String keyNoNamespace = StringUtils.substringAfter(entry.getKey(), VGR_NAMESPACE);
                vgrMetadata.put(keyNoNamespace, entry.getValue());
            }
        }
        this.metadata = vgrMetadata;
    }

    public Set<String> getMetadataNames() {
        return metadata.keySet();
    }

    @Override
    public String toString() {
        final ToStringBuilder stringBuilder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("NodeRef: " + getNodeRef()).append("MimeType: " + getMimeType())
        .append("Type: " + getType()).append("Published: " + getPublished())
        .append("Available From: " + getAvailableFrom());

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            final Class<? extends Object> typeOfClass = entry.getValue().getClass();
            stringBuilder.append(entry.getKey() + ": " + getMetadataValue(entry.getKey(), typeOfClass));
        }
        return stringBuilder.toString();
    }
}
