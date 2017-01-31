package se.vgregion.ifeed.service.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import se.vgr.metaservice.schema.ApelonClient;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.Metadata;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;

public class MetadataServiceImpl implements MetadataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataService.class);
    private static Map<String, CachedVocabulary> vocabularyCache = new HashMap<String, CachedVocabulary>();

    private ApelonClient apelon = null;
    private JpaRepository<Metadata, Long, Long> repo = null;

    private Collection<String> metadataRoots;

    public MetadataServiceImpl(ApelonClient apelon, JpaRepository<Metadata, Long, Long> repo) {
        this.apelon = apelon;
        this.repo = repo;
    }

    public void setMetadataRoots(Collection<String> metadataRoots) {
        this.metadataRoots = metadataRoots;
    }

    @Override
    @Transactional
    public void importMetadata() {
        for (String metadataRoot : metadataRoots) {
            /*if (repo.findByAttribute("name", metadataRoot).isEmpty()) {
                Metadata mr = new Metadata(metadataRoot);
                repo.persist(mr);
                repo.flush();
            }*/
            importMetadata(metadataRoot);
            repo.flush();
        }
    }

    @Override
    @Transactional
    public void importMetadata(String rootMetadataName) {
        Collection<Metadata> roots = repo.findByAttribute("name", rootMetadataName);
        if (!roots.isEmpty()) {
            for (Metadata root : roots) {
                repo.remove(root);
            }
        }
        Metadata root = new Metadata(rootMetadataName);
        updateCacheTree(root, StringUtils.EMPTY);
        if (root.getChildren().size() == 0) {
            String message = String.format(
                    "The Apelon service didn't return any result for %s. Roll back transaction.", rootMetadataName);
            LOGGER.error(message);
            throw new RuntimeException(message);
        }
        repo.store(root);
    }

    /*@Transactional
    void updateCacheTree(Metadata parent, String path) {
        NodeListResponseObjectType result = null;
        GetVocabularyRequest req = new GetVocabularyRequest();
        req.setRequestId(UUID.randomUUID().toString());
        String fullPath = path + (isBlank(path) ? "" : "/") + parent.getName();
        req.setPath(fullPath);

        result = apelon.getVocabulary(req);

        NodeListType nodes = result.getNodeList();
        for (NodeType node : nodes.getNode()) {
            LOGGER.info("Importing: {}/{}", new Object[]{fullPath, node.getName()});
            Metadata child = new Metadata(node.getName());
            parent.addChild(child);
            updateCacheTree(child, fullPath);
        }
    }
*/

    @Transactional
    void updateCacheTree(Metadata parent, String path) {
        ApelonClient apelon = new ApelonClient();
        String fullPath = path + (isBlank(path) ? "" : "/") + parent.getName();

        for (String vocal : apelon.fetchVocabulary(fullPath)) {
            LOGGER.info("Importing: {}/{}", new Object[]{fullPath, vocal});
            Metadata child = new Metadata(vocal);
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

    @Override
    public Collection<String> getVocabulary(String metadataNodeName) {
        LOGGER.debug("Get vocabulary for metadata: {}", metadataNodeName);
        List<String> vocabulary = Collections.emptyList();
        if (isBlank(metadataNodeName)) {
            return vocabulary;
        }
        CachedVocabulary cachedVocabulary = vocabularyCache.get(metadataNodeName);

        if (cachedVocabulary == null || cachedVocabulary.vocabulary.isEmpty()) {
            System.out.println("Inget vocabulär " + vocabularyCache.keySet() + "\n\n" + metadataNodeName);

        }

        LOGGER.debug("Reading vocabulary from source");
        Collection<Metadata> vocabularyNodes = repo.findByAttribute("name", metadataNodeName);

        if (!vocabularyNodes.isEmpty()) {
            vocabulary = new ArrayList<String>(vocabularyNodes.size());
            List<Metadata> metadataChildNodes = (List<Metadata>) vocabularyNodes.iterator().next().getChildren();
            for (Metadata metadata : metadataChildNodes) {
                vocabulary.add(metadata.getName());
            }
            Collections.sort(vocabulary);
        }
        vocabularyCache.put(metadataNodeName, new CachedVocabulary(vocabulary));
        LOGGER.debug("Vocabulary: " + vocabularyCache.get(metadataNodeName).getVocabulary());

        return Collections.unmodifiableCollection(vocabularyCache.get(metadataNodeName).getVocabulary());
    }

    /*private boolean emptyOrInvalidCache(CachedVocabulary cachedVocabulary) {
        return cachedVocabulary == null || !cachedVocabulary.isValid();
    }*/

}
