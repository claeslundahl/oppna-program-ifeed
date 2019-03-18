package se.vgregion.ifeed.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.web.util.UriTemplate;

import se.vgregion.ifeed.formbean.IFeedFormBeanList;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery.SortDirection;
import se.vgregion.ifeed.types.IFeed;

@Ignore
public class IFeedControllerTest {

	/*IFeedService iFeedService;
	UriTemplate iFeedAtomFeed;

	@Before
	public void setUp() throws Exception {
		iFeedService = mock(IFeedService.class);
		iFeedAtomFeed = mock(UriTemplate.class);
	}

	@Test
	public void showIFeeds() {
		showIFeeds(true);
		showIFeeds(false);
	}

	public void showIFeeds(boolean forUser) {
		IFeedController controller = mkControllerNoHandlersPopulate();

		Model model = mock(Model.class);
		if (forUser) {
			when(model.asMap()).thenReturn(Collections.singletonMap("currentView", (Object) "viewMine"));
		}
		int delta = 1;
		int cur = 2;
		String orderByCol = null;
		SortDirection orderByType = null;
		RenderRequest request = mock(RenderRequest.class);

		String result = controller.showIFeeds(model, delta, cur, orderByCol, orderByType, request);

		if (forUser) {
			verify(iFeedService, times(1)).getUserIFeeds(anyString());
		} else {
			assertEquals("index", result);
		}
	}

	@Test
	public void handleViewPagination() {
		IFeedController controller = new IFeedController(iFeedService, iFeedAtomFeed);
		List<IFeed> viewList = new ArrayList<IFeed>();
		for (int i = 0; i < 105; i++) {
			IFeed feed = new IFeed();
			feed.setId((long) i);
			viewList.add(feed);
		}

		List<IFeed> result = controller.handleViewPagination(viewList, 10, 1);
		assertEquals(10, result.size());
		assertEquals(viewList.subList(0, 10), result);
	}

	@Test
	public void showUserIFeeds() {
		IFeedController controller = mkControllerNoHandlersPopulate();
		Model model = mock(Model.class);
		int delta = 10;
		int cur = 1;
		String orderByCol = "id";
		SortDirection orderByType = SortDirection.asc;
		RenderRequest request = mock(RenderRequest.class);

		String result = controller.showUserIFeeds(model, delta, cur, orderByCol, orderByType, request);
		assertEquals("index", result);

	}

	@Test
	public void handleViewSort() {
		IFeedController controller = new IFeedController(iFeedService, iFeedAtomFeed);
		List<IFeed> viewList = new ArrayList<IFeed>();
		String orderByCol = "id";
		SortDirection orderByType = SortDirection.desc;

		for (long i = 0; i < 10; i++) {
			IFeed feed = new IFeed();
			feed.setId(i);
		}

		List<IFeed> result = controller.handleViewSort(viewList, orderByCol, orderByType);
		long expextedId = 10;
		for (IFeed feed : result) {
			expextedId--;
			assertEquals(expextedId, feed.getId().longValue());
		}

	}

	@Test
	public void showAllIFeeds() {
		IFeedController controller = mkControllerNoHandlersPopulate();
		Model model = mock(Model.class);
		int delta = 10;
		int cur = 1;
		String orderByCol = "id";
		SortDirection orderByType = SortDirection.asc;
		RenderRequest request = mock(RenderRequest.class);
		String result = controller.showAllIFeeds(model, delta, cur, orderByCol, orderByType);
		assertEquals("index", result);
	}

	@Test
	public void populateModel() {
		final IFeedController controller = new IFeedController(iFeedService, iFeedAtomFeed);
		Model model = mock(Model.class);
		int delta = 7;
		List<IFeed> iFeeds = new ArrayList<IFeed>();
		int numberOfIfeeds = 10;
		String orderCol = "id";
		SortDirection orderType = SortDirection.desc;
		String viewName = "";

		controller.populateModel(model, iFeeds, numberOfIfeeds, delta, orderCol, orderType, viewName);

		verify(model, times(1)).addAttribute(eq("numberOfIfeeds"), eq(numberOfIfeeds));
		verify(model, times(1)).addAttribute(eq("ifeeds"), eq(new IFeedFormBeanList(iFeeds, iFeedAtomFeed)));
		verify(model, times(1)).addAttribute(eq("delta"), eq(delta));
		verify(model, times(1)).addAttribute(eq("orderByCol"), eq(orderCol));
		verify(model, times(1)).addAttribute(eq("orderByType"), eq(orderType));
		verify(model, times(1)).addAttribute(eq("toolbarItem"), eq(viewName));
		verify(model, times(1)).addAttribute(eq("currentView"), eq(viewName));
	}

	private IFeedController mkControllerNoHandlersPopulate() {
		final IFeedController controller = new IFeedController(iFeedService, iFeedAtomFeed) {
			@Override
			protected List<IFeed> handleViewPagination(List<IFeed> viewList, int delta, int pageNumber) {
				return new ArrayList<IFeed>(viewList);
			}

			@Override
			protected List<IFeed> handleViewSort(List<IFeed> viewList, String orderByCol, SortDirection orderByType) {
				return new ArrayList<IFeed>(viewList);
			}

			@Override
			protected void populateModel(Model model, List<IFeed> iFeeds, int numberOfIfeeds, int delta,
			        String orderCol, SortDirection orderType, String viewName) {
			}
		};
		return controller;
	}*/
}
