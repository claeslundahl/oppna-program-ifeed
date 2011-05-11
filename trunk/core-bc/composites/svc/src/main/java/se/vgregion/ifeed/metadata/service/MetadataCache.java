package se.vgregion.ifeed.metadata.service;

import org.springframework.transaction.annotation.Transactional;

public interface MetadataCache {

    void updateCache();

    @Transactional
    void updateCache(String rootMetadataName);

}