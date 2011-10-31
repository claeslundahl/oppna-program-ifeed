package se.vgregion.ifeed.viewer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.IFeed;

@Controller
public class IFeedViewerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IFeedViewerController.class);

	private IFeedService iFeedService;
	private IFeedSolrQuery solrQuery;

	@Autowired
	public IFeedViewerController(IFeedSolrQuery solrQuery, IFeedService iFeedService) {
		super();
		this.solrQuery = solrQuery;
		this.iFeedService = iFeedService;
	}

	@RequestMapping(value = "/feed/{id}")
	public String getIFeed(@PathVariable("id") Long id, Model model) {
		System.out.println("IFeedViewerController.getIFeed()");
		// Retrieve feed from store
		IFeed retrievedFeed = iFeedService.getIFeed(id);

		// Throw 404 if the feed doesn't exist
		if (retrievedFeed == null) {
			// throw new WebApplicationException(Response.Status.NOT_FOUND);
			// TODO create a html view of the error to
			// enhance the browser experience
		}
		System.out.println(iFeedService);
		List<Map<String, Object>> result = solrQuery.getIFeedResults(retrievedFeed);

		System.out.println(result);
		model.addAttribute("result", result);
		return "documentList";
	}

	@RequestMapping(value = "/document/{id}/details")
	public String details(@PathVariable String id) throws UnsupportedEncodingException {
		String decodedId = URLDecoder.decode(id, "UTF-8");
		System.out.println("Hej " + decodedId);
		return "documentDetails";
	}

}
