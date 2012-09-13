package se.vgregion.ifeed.scheduler;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import se.vgregion.ifeed.service.metadata.MetadataService;

public class IFeedScheduledJobTest {

    @Test
    public void receive() {
        IFeedScheduledJob job = new IFeedScheduledJob();
        MetadataService service = mock(MetadataService.class);
        job.setService(service);
    }

}
