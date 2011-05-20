package se.vgregion.ifeed.metadata.service;

import static org.apache.commons.lang.StringUtils.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import se.vgr.metaservice.schema.node.v2.NodeListType;
import se.vgr.metaservice.schema.node.v2.NodeType;
import se.vgr.metaservice.schema.response.v1.NodeListResponseObjectType;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.Metadata;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.GetVocabularyRequest;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.VocabularyService;

public class MetadataServiceImpl implements MetadataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataService.class);
    private static Map<String, CachedVocabulary> vocabularyCache = new HashMap<String, CachedVocabulary>();

    private VocabularyService port;
    private JpaRepository<Metadata, Long, Long> repo;

    private Collection<String> metadataRoots;

    public MetadataServiceImpl(VocabularyService port, JpaRepository<Metadata, Long, Long> repo) {
        this.port = port;
        this.repo = repo;
    }

    public void setMetadataRoots(Collection<String> metadataRoots) {
        this.metadataRoots = metadataRoots;
    }

    @Transactional
    public void importMetadata() {
        for (String metadataRoot : metadataRoots) {
            importMetdata(metadataRoot);
        }
    }

    @Transactional
    public void importMetdata(String rootMetadataName) {
        Collection<Metadata> roots = repo.findByAttribute("name", rootMetadataName);
        if (!roots.isEmpty()) {
            for (Metadata root : roots) {
                repo.remove(root);
            }
        }
        Metadata root = new Metadata(rootMetadataName);
        updateCacheTree(root, StringUtils.EMPTY);
        repo.store(root);
    }

    @Transactional
    private void updateCacheTree(Metadata parent, String path) {

        GetVocabularyRequest req = new GetVocabularyRequest();
        req.setRequestId(UUID.randomUUID().toString());
        String fullPath = path + (isBlank(path) ? "" : "/") + parent.getName();
        req.setPath(fullPath);

        NodeListResponseObjectType result = port.getVocabulary(req);

        NodeListType nodes = result.getNodeList();
        for (NodeType node : nodes.getNode()) {
            LOGGER.info("Importing: {}/{}", new Object[] { fullPath, node.getName() });
            Metadata child = new Metadata(node.getName());
            parent.addChild(child);
            updateCacheTree(child, fullPath);
        }
    }

    private static class CachedVocabulary {
        private Date time;
        private Collection<String> vocabulary;

        public CachedVocabulary(List<String> vocabulary) {
            this.vocabulary = vocabulary;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            time = cal.getTime();
        }

        public Date getTime() {
            return time;
        }

        public Collection<String> getVocabulary() {
            return vocabulary;
        }

        public boolean isValid() {
            return getTime().after(new Date());
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public Collection<String> getVocabulary(String metadataNodeName) {
        LOGGER.debug("Get vocabulary for metadata: {}", metadataNodeName);
        List<String> vocabulary = Collections.emptyList();
        if(isBlank(metadataNodeName)) {
            return vocabulary;
        }
        CachedVocabulary cachedVocabulary = vocabularyCache.get(metadataNodeName);

        if(emptyOrInvalidCache(cachedVocabulary)) {
            LOGGER.debug("Reading vocabulary from source");
            Collection<Metadata> vocabularyNodes = repo.findByAttribute("name", metadataNodeName);

            if(!vocabularyNodes.isEmpty()) {
                vocabulary = new ArrayList<String>(vocabularyNodes.size());
                List<Metadata> metadataChildNodes = (List<Metadata>)vocabularyNodes.iterator().next().getChildren();
                for (Metadata metadata : metadataChildNodes) {
                    vocabulary.add(metadata.getName());
                }
                Collections.sort(vocabulary);
            }
            vocabularyCache.put(metadataNodeName, new CachedVocabulary(vocabulary));
        } else {
            LOGGER.debug("Reading vocabulary from cache");
        }
        LOGGER.debug("Vocabulary: " + vocabularyCache.get(metadataNodeName).getVocabulary());

        return Collections.unmodifiableCollection(vocabularyCache.get(metadataNodeName).getVocabulary());
    }

    private boolean emptyOrInvalidCache(CachedVocabulary cachedVocabulary) {
        return cachedVocabulary == null || !cachedVocabulary.isValid();
    }
}
