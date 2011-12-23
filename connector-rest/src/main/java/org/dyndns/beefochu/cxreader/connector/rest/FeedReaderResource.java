package org.dyndns.beefochu.cxreader.connector.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Body;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Head;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Opml;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Outline;

@Stateless
@Path("/")
public class FeedReaderResource {

	@Inject
	Reader reader;
	
    public FeedReaderResource() {
    }

    @GET
    @Path("feeds/")
    @Produces({MediaType.APPLICATION_XML, "text/x-opml"})
    public Opml getFeedList() {
    	Opml opml = new Opml();
    	opml.setVersion("2.0");
    	opml.setHead(new Head());
    	opml.setBody(new Body());
    	
    	for(FeedUserRelation relation : reader.getFeedList()) {
    		Feed feed = relation.getFeed();
    		
    		Outline outline = new Outline();
    		outline.setType("rss");
    		outline.setText(feed.getName());
    		outline.setXmlUrl(feed.getUrl().toString());
    		outline.getOtherAttributes().put(XmlNames.OPML_EXTENSION_FEED_ID, Long.toString(feed.getId()));
    		
    		opml.getBody().getOutline().add(outline);
    	}
    	
    	return opml;
    }
}
