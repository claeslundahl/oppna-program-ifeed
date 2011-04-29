package se.vgregion.ifeed.intsvc.atom;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.abdera.model.Feed;

import se.vgregion.ifeed.service.IFeedService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;


public interface IFeedFeedService {

	@GET
	@Produces({ "application/xml", "application/atom+xml;type=feed" })
	@Path("/feed/{id}")
	public abstract Feed getIFeed(@PathParam("id") Long id);

	/*	@GET
		@Produces({ "application/xml", "application/atom+xml;type=entry" })
		@Path("/entry/{id}")
		public abstract Entry getIFeedEntry(@PathParam("id") Long id);
	 */
	void setSolrQuery(IFeedSolrQuery solrQuery);
	IFeedSolrQuery getSolrQuery();
	
	void setFeedService(IFeedService feedService);
	IFeedService getFeedService();

}