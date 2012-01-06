package org.dyndns.beefochu.cxreader.backend.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.enterprise.inject.Instance;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.exceptions.ParsingException;
import org.dyndns.beefochu.cxreader.backend.services.FeedService.FindOrCreateReturn;
import org.dyndns.beefochu.cxreader.backend.services.parsers.FeedParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class FeedServiceTest {

	private URL testFeed;
	private FeedService feedService;
	private TypedQuery<Feed> feedQuery;
	private TypedQuery<Long> subscriberCountQuery;
	private InOrder inOrder;
	private List<Feed> feeds;
	private Feed feed;
	private FeedParser parser;
	private Iterator<FeedParser> parsers;
	private Feed newFeed;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws IOException, ParsingException {
		this.feedService = new FeedService();
		// It doesn't matter which file is used. It's just needed to make
		// URL#openStream() not fail.
		this.testFeed = FeedServiceTest.class.getResource("/arquillian.xml");

		this.feedService.em = mock(EntityManager.class);
		this.feedQuery = mock(TypedQuery.class);
		this.subscriberCountQuery = mock(TypedQuery.class);
		when(this.feedService.em.createNamedQuery(Feed.FIND_BY_URL, Feed.class))
				.thenReturn(this.feedQuery);
		when(
				this.feedService.em.createNamedQuery(
						FeedUserRelation.COUNT_FEED_SUBSCRIBERS, Long.class))
				.thenReturn(this.subscriberCountQuery);

		setUpFeedList();
		mockFeedParser();

		this.inOrder = inOrder(this.feedQuery, this.subscriberCountQuery,
				this.feedService.em, this.parser, this.feedService.parsers,
				this.parsers, this.newFeed);
	}

	@SuppressWarnings("unchecked")
	private void mockFeedParser() throws IOException, ParsingException {
		this.feedService.parsers = mock(Instance.class);
		this.parsers = mock(Iterator.class);
		FeedParser parser1 = mock(FeedParser.class);
		this.parser = mock(FeedParser.class);
		this.newFeed = mock(Feed.class);

		when(parser1.isFeedParsable(any(InputStream.class))).thenReturn(false);
		when(this.parser.isFeedParsable(any(InputStream.class))).thenReturn(
				true);
		when(this.parser.parse(any(InputStream.class)))
				.thenReturn(this.newFeed);
		when(this.parsers.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(this.parsers.next()).thenReturn(parser1).thenReturn(this.parser)
				.thenThrow(new NoSuchElementException());
		when(this.feedService.parsers.iterator()).thenReturn(this.parsers);
	}

	private void setUpFeedList() {
		this.feeds = new LinkedList<Feed>();
		this.feed = new Feed();
		this.feed.setUrl(testFeed);
		this.feeds.add(feed);
	}

	@Test
	public void findOrCreateFeedNotFoundTest() throws FeedUrlInvalidException,
			MalformedURLException {
		when(this.feedQuery.getResultList()).thenReturn(new LinkedList<Feed>())
				.thenReturn(feeds);
		FindOrCreateReturn returnVal = this.feedService.findOrCreate(testFeed);
		assertTrue(returnVal.isNew);

		verifyTestFeedSearched();
		verifyFeedParserSearched();
		verifyTestFeedParsed();
		verifyFeedUrlSet();
		verifyFeedPersited();
		verifyTestFeedSearched();

		assertSame(feed, returnVal.feed);
	}

	@Test
	public void findOrCreateFeedFoundTest() throws MalformedURLException,
			FeedUrlInvalidException {
		when(this.feedQuery.getResultList()).thenReturn(this.feeds);
		FindOrCreateReturn feedReturned = this.feedService.findOrCreate(testFeed);
		assertFalse(feedReturned.isNew);

		verifyTestFeedSearched();
		inOrder.verifyNoMoreInteractions();

		assertSame(feed, feedReturned.feed);
	}

	@Test(expected = FeedUrlInvalidException.class)
	public void findOrCreateFeedNoParserAvailableTest()
			throws FeedUrlInvalidException, IOException {
		when(this.parser.isFeedParsable(any(InputStream.class))).thenReturn(
				false);
		this.feedService.findOrCreate(testFeed);
	}

	@Test(expected = FeedUrlInvalidException.class)
	public void findOrCreateParsingFailedTest() throws FeedUrlInvalidException,
			ParsingException, IOException {
		doThrow(ParsingException.class).when(this.parser).parse(
				any(InputStream.class));
		this.feedService.findOrCreate(testFeed);
	}

	@Test(expected = FeedUrlInvalidException.class)
	public void findOrCreateInvalidFeedTest() throws FeedUrlInvalidException,
			MalformedURLException {
		this.feedService.findOrCreate(new URL("file:/Non-Existing-File"));
	}

	@Test(expected = FeedUrlInvalidException.class)
	public void findOrCreateFeedToBigTest() throws IOException,
			FeedUrlInvalidException {
		File tmp = createTempFile(FeedService.MAX_FEED_SIZE + 1);

		// Should fail because file is to big.
		this.feedService.findOrCreate(tmp.toURI().toURL());
	}

	@Test
	public void findOrCreateFeedMaxSizeTest() throws IOException {
		File tmp = createTempFile(FeedService.MAX_FEED_SIZE);
		try {
			this.feedService.findOrCreate(tmp.toURI().toURL());
		} catch (FeedUrlInvalidException ex) {
			ex.printStackTrace();
			fail("Methode should not fail for streams that are "
					+ FeedService.MAX_FEED_SIZE + " big.");
		}
	}

	@Test
	public void removeIfOrphanIsNotOrphanTest() {
		when(this.subscriberCountQuery.getSingleResult()).thenReturn(
				Long.valueOf(1));

		this.feedService.removeIfOrphan(feed);
		verifySubscriberForFeedQueried();
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void removeIfOrphanIsOrphanTest() {
		when(this.subscriberCountQuery.getSingleResult()).thenReturn(
				Long.valueOf(0));

		this.feedService.removeIfOrphan(feed);
		verifySubscriberForFeedQueried();
		inOrder.verify(this.feedService.em).remove(feed);
	}

	private void verifySubscriberForFeedQueried() {
		inOrder.verify(this.feedService.em).createNamedQuery(
				FeedUserRelation.COUNT_FEED_SUBSCRIBERS, Long.class);
		inOrder.verify(this.subscriberCountQuery).setParameter("feed",
				this.feed);
		inOrder.verify(this.subscriberCountQuery).getSingleResult();
	}

	private void verifyTestFeedSearched() {
		inOrder.verify(this.feedService.em).createNamedQuery(Feed.FIND_BY_URL,
				Feed.class);
		inOrder.verify(this.feedQuery).setParameter("url", testFeed.toString());
		inOrder.verify(this.feedQuery).getResultList();
	}

	private void verifyFeedPersited() {
		inOrder.verify(this.feedService.em).persist(any(Feed.class));
	}

	private void verifyFeedParserSearched() {
		inOrder.verify(this.feedService.parsers).iterator();
		inOrder.verify(this.parsers).hasNext();
		inOrder.verify(this.parsers).next();
		inOrder.verify(this.parsers).hasNext();
		inOrder.verify(this.parsers).next();
		try {
			inOrder.verify(this.parser).isFeedParsable(any(InputStream.class));
		} catch (IOException ex) {
			System.out.println(ex);
			fail("Unexcepted IOException thrown");
		}
	}

	private void verifyTestFeedParsed() {
		try {
			inOrder.verify(this.parser).parse(any(InputStream.class));
		} catch (ParsingException ex) {
			System.out.println(ex);
			fail("Unexcepted ParsingException thrown");
		} catch (IOException ex) {
			System.out.println(ex);
			fail("Unexcepted IOException thrown");
		}
	}

	private void verifyFeedUrlSet() {
		inOrder.verify(this.newFeed).setUrl(testFeed);
	}

	private File createTempFile(int sizeInByte) throws IOException {
		File tmp = File.createTempFile("cxReaderTestFile", ".xml");
		tmp.deleteOnExit();

		byte[] buffer = new byte[1024];

		OutputStream stream = new FileOutputStream(tmp);
		for (int i = 0; i < sizeInByte / 1024; i++) {
			stream.write(buffer);
		}
		stream.write(buffer, 0, sizeInByte % 1024);
		stream.close();

		assertEquals(sizeInByte, tmp.length());

		return tmp;
	}
}