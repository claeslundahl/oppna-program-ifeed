package se.vgregion.ifeed.service.metadata;

import org.junit.Before;
import org.junit.Test;
import se.vgr.metaservice.schema.ApelonClient;
import se.vgr.metaservice.schema.node.v2.NodeType;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.Metadata;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MetadataServiceImplTest {

    MetadataServiceImpl serv;
    ApelonClient port;
    JpaRepository<Metadata, Long, Long> repo;

    @Before
    public void setUp() throws Exception {
        port = mock(ApelonClient.class);
        repo = mock(JpaRepository.class);
        serv = new MetadataServiceImpl(port, repo);
    }

    @Test
    public void setMetadataRoots() {
        Collection<String> metadataRoots = mock(Collection.class);
        serv.setMetadataRoots(metadataRoots);
    }

    public static void main(String[] args) throws Exception {
        URL url = MetadataServiceImplTest.class.getResource(MetadataServiceImplTest.class.getSimpleName()
                + ".class");
        System.out.println(url);
        File file = new File(url.getFile());
        Package pack = MetadataServiceImplTest.class.getPackage();
        String[] pathParts = pack.getName().split(Pattern.quote("."));
        for (int i = 0, j = pathParts.length + 1; i < j; i++) {
            file = file.getParentFile();
        }
        System.out.println(file);
    }

    @Test
    public void importMetadata() {
        final List<String> calledArg = new ArrayList<String>();
        serv = new MetadataServiceImpl(port, repo) {
            @Override
            public void importMetadata(String rootMetadataName) {
                calledArg.add(rootMetadataName);
            }
        };
        Collection<String> metadataRoots = new ArrayList<String>();
        metadataRoots.add("root1");
        metadataRoots.add("root2");
        metadataRoots.add("root3");
        serv.setMetadataRoots(metadataRoots);
        serv.importMetadata();
        assertTrue(calledArg.contains("root1"));
        assertTrue(calledArg.contains("root2"));
        assertTrue(calledArg.contains("root3"));
    }

    @Test
    public void updateCacheTree() {

    }

    @Test
    public void importMetdata() {
        serv = new MetadataServiceImpl(port, repo) {
            @Override
            void updateCacheTree(Metadata parent, String path) {
                parent.addChild(new Metadata("test"));
            }
        };
        List<String> nlrot = new ArrayList<>();
        // NodeListType value = mock(NodeListType.class);
        List<NodeType> getNodeValue = new ArrayList<NodeType>();
        getNodeValue.add(new NodeType());
        // when(value.getNode()).thenReturn(getNodeValue);
        //when(nlrot.getNodeList()).thenReturn(value);
        when(port.fetchVocabulary(any(String.class))).thenReturn(nlrot);
        Collection<Metadata> metaDataResults = new ArrayList<Metadata>();
        metaDataResults.add(new Metadata("root1"));
        when(repo.findByAttribute("name", "root1")).thenReturn(metaDataResults);

        serv.importMetadata("root1");

        verify(repo).remove(any(Metadata.class));
        verify(repo).store(any(Metadata.class));
    }

}
