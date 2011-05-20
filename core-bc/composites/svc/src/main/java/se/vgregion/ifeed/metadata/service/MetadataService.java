package se.vgregion.ifeed.metadata.service;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public interface MetadataService {

    @Transactional
    void importMetadata();

    @Transactional
    void importMetdata(String rootMetadataName);


    Collection<String> getVocabulary(String metadataNodeName);
}