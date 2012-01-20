package org.dyndns.beefochu.cxreader.connector.rest;

import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;
import static javax.xml.datatype.DatatypeConstants.GREATER;
import static javax.xml.datatype.DatatypeConstants.JANUARY;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntry;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.AtomEntry;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.AtomLink;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.AtomPersonConstruct;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.CxReader;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Body;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Head;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Opml;
import org.dyndns.beefochu.cxreader.connector.rest.jaxb.opml.Outline;

@Stateless
@Path("/")
public class FeedReaderResource {

	@Inject
	Reader reader;
	@Inject
	DatatypeFactory xmlTypesFactory;
	@Context
	UriInfo uriInfo;

	// reuse one calendar object because they are big (~0.5 kb each)
	private GregorianCalendar gregCal = new GregorianCalendar();

	public FeedReaderResource() {
	}

	@GET
	@Path("feeds/")
	@Produces({ MediaType.APPLICATION_XML, "text/x-opml" })
	public Opml getFeedList() {
		Opml opml = new Opml();
		opml.setVersion("2.0");
		opml.setHead(new Head());
		opml.setBody(new Body());

		for (FeedUserRelation relation : reader.getFeedList()) {
			Feed feed = relation.getFeed();

			Outline outline = new Outline();
			outline.setType("rss");
			outline.setText(feed.getName());
			outline.setXmlUrl(feed.getUrl().toString());
			outline.getOtherAttributes().put(XmlNames.OPML_EXTENSION_FEED_ID,
					Long.toString(feed.getId()));

			opml.getBody().getOutline().add(outline);
		}

		return opml;
	}

	/**
	 * Generates an XML document in the Atom Syndication Format based on a
	 * FeedUserRelation object.
	 * 
	 * It is the FeedUserRelation object used whose associated Feed object has
	 * the id that is passed as a parameter and whose associated ReaderUser is
	 * the user that is currently logged on. The reason to use the id of the
	 * associated Feed is, so that two users who have subscribed to the same
	 * feed can use the same URL.
	 * 
	 * The generated document confirms to "atom-subset.xsd" which defines a
	 * subset of the Atom Syndication Format. It is extended by informations
	 * about the read status for the currently logged in user. The format of
	 * these additional informations is defined in
	 * "atom-extension-user-entry-relations.xsd".
	 * 
	 * The time zone of all dates in the generated document is currently UTC.
	 * This may change in the future.
	 * 
	 * @param id
	 *            The id of Feed object for the FeedUserRelation object the atom
	 *            feed should be created for.
	 * @param newerThan
	 *            Only feed entries that are newer than this parameter should be
	 *            returned. This parameter is expected to be in ms since
	 *            01.01.1970 00:00:00 UTC.
	 */
	@GET
	@Path("feeds/{id:([0-9]+)}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML })
	public org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.Feed getFeed(
			@PathParam("id") long id, @QueryParam("newerThan") Date newerThan) {

		if (newerThan == null)
			newerThan = new Date(0);

		FeedUserRelation feed = getFeedUserRelation(id);

		org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.Feed atomFeed = createBasicFeed(
				feed, id);

		List<FeedEntryUserRelation> entries = reader
				.getEntries(feed, newerThan);

		for (FeedEntryUserRelation entry : entries) {
			AtomEntry newEntry = createAtomEntry(entry);
			if (newEntry.getUpdated().compare(atomFeed.getUpdated()) == GREATER) {
				atomFeed.setUpdated(newEntry.getUpdated());
			}
			atomFeed.getEntry().add(newEntry);
		}

		return atomFeed;
	}

	/**
	 * Set's the read status for this FeedEntry object.
	 * 
	 * @param feedId
	 *            The id of the Feed object this FeedEntry belongs to.
	 * @param entryId
	 *            The id of the FeedEntry object.
	 * @param read
	 *            true if entry is read, false if not. If no value is supplied
	 *            this defaults to true
	 */
	@PUT
	@Path("feeds/{feedId:([0-9]+)}/{entryId:([0-9]+)}")
	public void setReadStatus(@PathParam("feedId") long feedId,
			@PathParam("entryId") long entryId,
			@DefaultValue("true") @QueryParam("read") boolean read) {

		FeedEntryUserRelation rel = getFeedEntryUserRelation(feedId, entryId);
		reader.setReadStatus(rel, read);
	}

	/**
	 * Redirects to the url of the link whith rel="alternate" of this FeedEntry.
	 * 
	 * @param feedId
	 *            The id of the Feed object this FeedEntry belongs to.
	 * @param entryId
	 *            The id of the FeedEntry object.
	 */
	@GET
	@Path("feeds/{feedId:([0-9]+)}/{entryId:([0-9]+)}")
	public Response getFeedContent(@PathParam("feedId") long feedId,
			@PathParam("entryId") long entryId) {

		FeedEntryUserRelation rel = getFeedEntryUserRelation(feedId, entryId);

		try {
			return Response.seeOther(rel.getFeedEntry().getUrl().toURI())
					.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param feedId
	 *            the id of the Feed object for the FeedUserRelation object that
	 *            should contain the FeedEntryUserRelation that is searched for.
	 * @param entryId
	 *            the id of the FeedEntry object for the FeedEntryUserRelation
	 *            object that is searched for.
	 * @throws WebApplicationException
	 *             if the user has not subscribed to any feed with id=feedId or
	 *             the feed doesn't contain any entries with id=entryId
	 */
	private FeedEntryUserRelation getFeedEntryUserRelation(long feedId,
			long entryId) {
		// TODO optimize me: search for id directly in sql query
		FeedUserRelation feed = getFeedUserRelation(feedId);

		List<FeedEntryUserRelation> entries = reader.getEntries(feed, new Date(
				0));
		
		FeedEntryUserRelation rel = null;
		for(int i = 0; i < entries.size(); i++) {
			FeedEntryUserRelation relCur = entries.get(i);
			if(relCur.getFeedEntry().getId() == entryId) {
				rel = relCur;
				break;
			}
		}
		
		if (rel == null)
			throw new WebApplicationException(Status.NOT_FOUND);
		
		return rel;
	}

	/**
	 * 
	 * @param id
	 *            the id of the Feed object for the FeedUserRelation object that
	 *            is searched for
	 * @throws WebApplicationException
	 *             if the user has not subscribed to any feed with the id
	 */
	private FeedUserRelation getFeedUserRelation(long id) {
		// TODO optimize me: search for id directly in sql query
		FeedUserRelation feed = null;
		List<FeedUserRelation> feeds = reader.getFeedList();
		for (int i = 0; i < feeds.size(); i++) {
			FeedUserRelation feedCur = feeds.get(i);
			if (feedCur.getFeed().getId() == id) {
				feed = feedCur;
				break;
			}
		}

		if (feed == null)
			throw new WebApplicationException(Status.NOT_FOUND);

		return feed;
	}

	private org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.Feed createBasicFeed(
			FeedUserRelation feed, long id) {
		String entryUri = this.uriInfo.getBaseUriBuilder().path("feeds/")
				.path("" + id).build().toString();

		AtomLink feedLink = new AtomLink();
		feedLink.setHref(entryUri);
		feedLink.setRel("self");

		// TODO set real author
		AtomPersonConstruct author = new AtomPersonConstruct();
		author.setName("unknown");

		org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.Feed atomFeed = new org.dyndns.beefochu.cxreader.connector.rest.jaxb.atom.Feed();
		atomFeed.setAuthor(author);
		atomFeed.setId(entryUri);
		atomFeed.setLink(feedLink);
		atomFeed.setTitle(feed.getFeed().getName());
		// TODO check if this really creates a date that is older then every
		// date that a FeedEntry could have.
		atomFeed.setUpdated(this.xmlTypesFactory.newXMLGregorianCalendar(1900,
				JANUARY, 1, 0, 0, 0, 0, FIELD_UNDEFINED));

		return atomFeed;
	}

	private AtomEntry createAtomEntry(FeedEntryUserRelation entryRelation) {
		FeedEntry entry = entryRelation.getFeedEntry();

		// TODO set real author
		AtomPersonConstruct author = new AtomPersonConstruct();
		author.setName("unknown");

		AtomLink entryLink = new AtomLink();
		entryLink.setHref(entry.getUrl().toString());
		entryLink.setRel("alternate");

		gregCal.setTimeInMillis(entry.getLastUpdate().getTime());
		XMLGregorianCalendar xmlCal = this.xmlTypesFactory
				.newXMLGregorianCalendar(gregCal);

		CxReader extensions = new CxReader();
		extensions.setId(BigInteger.valueOf(entry.getId()));
		extensions.setRead(entryRelation.isRead());

		AtomEntry atomEntry = new AtomEntry();
		atomEntry.setAuthor(author);
		atomEntry.setId(entry.getUrl().toString());
		atomEntry.setLink(entryLink);
		atomEntry.setSummary(entry.getSummary());
		atomEntry.setTitle(entry.getTitle());
		atomEntry.setUpdated(xmlCal);
		atomEntry.setAny(extensions);

		return atomEntry;
	}
}
