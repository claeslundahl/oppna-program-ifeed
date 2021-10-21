package se.vgregion.ifeed.types;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FeedDocumentIndexId implements Serializable {

    private Long ifeedId;

    private String documentId;

    public FeedDocumentIndexId(Long ifeedId, String documentId) {
        this.ifeedId = ifeedId;
        this.documentId = documentId;
    }

    public FeedDocumentIndexId() {

    }

    public Long getIfeedId() {
        return ifeedId;
    }

    public void setIfeedId(Long ifeedId) {
        this.ifeedId = ifeedId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedDocumentIndexId that = (FeedDocumentIndexId) o;

        if (ifeedId != null ? !ifeedId.equals(that.ifeedId) : that.ifeedId != null) return false;
        return documentId != null ? documentId.equals(that.documentId) : that.documentId == null;
    }

    @Override
    public int hashCode() {
        int result = ifeedId != null ? ifeedId.hashCode() : 0;
        result = 31 * result + (documentId != null ? documentId.hashCode() : 0);
        return result;
    }

}
