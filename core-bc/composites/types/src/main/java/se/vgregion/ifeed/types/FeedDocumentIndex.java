package se.vgregion.ifeed.types;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "feed_document_index")
public class FeedDocumentIndex {

    @EmbeddedId
    private FeedDocumentIndexId id;

}
