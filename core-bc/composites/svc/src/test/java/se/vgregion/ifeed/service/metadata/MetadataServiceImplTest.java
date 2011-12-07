package se.vgregion.ifeed.service.metadata;

import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.Metadata;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.VocabularyService;

public class MetadataServiceImplTest {

    MetadataServiceImpl serv;

    @Before
    public void setUp() throws Exception {
        VocabularyService port = mock(VocabularyService.class);
        JpaRepository<Metadata, Long, Long> repo = mock(JpaRepository.class);
        serv = new MetadataServiceImpl(port, repo);
    }

    @Test
    public void setMetadataRoots() {
        Collection<String> metadataRoots = mock(Collection.class);
        serv.setMetadataRoots(metadataRoots);
    }

    // @Test
    // public void importMetadata() {
    // serv.importMetadata();
    // }

    // @Test
    // public void testImportMetdata() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testGetVocabulary() {
    // fail("Not yet implemented");
    // }

}
