package se.vgregion.ifeed.intsvc.atom;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

public interface IFeedFeedService {

    @GET
    @Produces({ "application/xml", "application/atom+xml;type=feed" })
    @Path("/{id}/feed")
    Feed getIFeed(@PathParam("id") Long id);

    @GET
    @Produces({ "application/xml", "application/atom+xml;type=entry" })
    @Path("/{id}")
    Entry getIFeedEntry(@PathParam("id") Long id);

}