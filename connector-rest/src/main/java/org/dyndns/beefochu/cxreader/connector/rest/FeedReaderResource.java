package org.dyndns.beefochu.cxreader.connector.rest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.dyndns.beefochu.cxreader.backend.Reader;

@Named
@Path("/")
public class FeedReaderResource {

	@Inject
	Reader reader;
	
    public FeedReaderResource() {
    }

    @GET
    @Path("feeds/")
    @Produces({MediaType.APPLICATION_XML, "text/x-opml"})
    public String getFeedList() {
		return "Test";

    }
}
