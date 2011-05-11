package se.vgregion.ifeed.metadata.service;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.ifeed.types.Metadata;

public interface MetadataService {

    void importMetadata();

    @Transactional
    void importMetdata(String rootMetadataName);

    Collection<Metadata> getVocabulary(String metadataNodeName);
}