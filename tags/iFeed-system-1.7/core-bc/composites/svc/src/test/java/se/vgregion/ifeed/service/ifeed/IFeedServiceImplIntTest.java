package se.vgregion.ifeed.service.ifeed;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

public class IFeedServiceImplIntTest {

	private IFeedService iFeedService;

	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("test-db-context.xml");
		iFeedService = (IFeedService) ctx.getBean("iFeedService");
	}

	@Test
	public void areFiltersRemovedInDb() {
		IFeed feed = new IFeed();
		feed = iFeedService.addIFeed(feed);

		IFeedFilter filter = new IFeedFilter(null, "fq", "fk");
		feed.addFilter(filter);

		feed = iFeedService.updateIFeed(feed);
		feed = iFeedService.getIFeed(feed.getId());

		feed.removeFilters();

		iFeedService.updateIFeed(feed);

		feed = iFeedService.getIFeed(feed.getId());

		Assert.assertEquals(0, feed.getFilters().size());
	}

	@Test
	public void updateIFeed() {
		Long id = 101l;
		IFeed newFeed = new IFeed();
		newFeed.setId(id);

		IFeed oldFeed = new IFeed();

		IFeedFilter filter = new IFeedFilter();
		oldFeed.addFilter(filter);
		newFeed.addFilter(filter);

		IFeed addedFeed = iFeedService.addIFeed(newFeed);
		Assert.assertNotNull(addedFeed.getId());
	}

}
