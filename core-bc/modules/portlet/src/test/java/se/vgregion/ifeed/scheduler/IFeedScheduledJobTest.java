package se.vgregion.ifeed.scheduler;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.metadata.MetadataServiceImpl;

public class IFeedScheduledJobTest {

    @Test
    public void receive() {
        IFeedScheduledJob job = new IFeedScheduledJob();
        MetadataService service = new MetadataServiceImpl(null, null);
        job.setService(service);
    }

}
