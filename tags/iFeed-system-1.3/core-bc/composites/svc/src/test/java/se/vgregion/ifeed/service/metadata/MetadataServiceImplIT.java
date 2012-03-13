package se.vgregion.ifeed.service.metadata;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.Metadata;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.VocabularyService;

public class MetadataServiceImplIT {

    MetadataService serv;
    VocabularyService port;
    JpaRepository<Metadata, Long, Long> repo;

    @Before
    public void setUp() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("test-context.xml");
        port = (VocabularyService) ctx.getBean(VocabularyService.class);
        repo = (JpaRepository<Metadata, Long, Long>) ctx.getBean("metadataRepo");
        serv = (MetadataService) ctx.getBean("metadataService");
    }

    @Test @Ignore
    public void importMetadata() {
        serv.importMetadata();
    }

}
