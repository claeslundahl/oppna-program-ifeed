package se.vgregion.ifeed.metadata.service;

import static org.apache.commons.lang.StringUtils.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
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
    private static Map<String, CachedMetadata> metadataCache = new HashMap<String, CachedMetadata>();

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

    public void importMetadata() {
        for (String metadataRoot : metadataRoots) {
            importMetdata(metadataRoot);
        }
    }

    @Transactional
    public void importMetdata(String rootMetadataName) {
        Metadata root = repo.findByAttribute("name", rootMetadataName);
        if (root != null) {
            repo.remove(root);
        }
        root = new Metadata(rootMetadataName);
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

    private static class CachedMetadata {
        private Date time;
        private Collection<Metadata> metadataChildNodes;

        public CachedMetadata(List<Metadata> metadata) {
            this.metadataChildNodes = metadata;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            time = cal.getTime();
        }

        public Date getTime() {
            return time;
        }

        public Collection<Metadata> getMetadataChildNodes() {
            return metadataChildNodes;
        }
    }

    public Collection<Metadata> getVocabulary(String metadataNodeName) {
        CachedMetadata cachedMetadataNode = metadataCache.get(metadataNodeName);
        Date now = new Date();
        if(cachedMetadataNode == null || cachedMetadataNode.getTime().after(now)) {
            Metadata metadataNode = repo.findByAttribute("name", metadataNodeName);
            List<Metadata> metadataChildNodes = (List<Metadata>)metadataNode.getChildren();
            Collections.sort(metadataChildNodes);
            metadataCache.put(metadataNodeName, new CachedMetadata(metadataChildNodes));
        }
        return Collections.unmodifiableCollection(metadataCache.get(metadataNodeName).getMetadataChildNodes());
    }
}
