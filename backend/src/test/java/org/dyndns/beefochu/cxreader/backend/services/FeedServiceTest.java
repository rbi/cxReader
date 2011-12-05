package org.dyndns.beefochu.cxreader.backend.services;

import org.dyndns.beefochu.cxreader.backend.services.FeedService;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import org.dyndns.beefochu.cxreader.backend.exceptions.ParsingException;
import org.dyndns.beefochu.cxreader.backend.services.parsers.FeedParser;
import java.util.NoSuchElementException;
import java.io.InputStream;
import java.util.Iterator;
import javax.enterprise.inject.Instance;
import java.util.List;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.junit.Ignore;
import org.junit.Test;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.junit.Before;
import org.mockito.InOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FeedServiceTest {

    private URL testFeed;
    private FeedService feedService;
    private TypedQuery<Feed> feedQuery;
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
        //It doesn't matter which file is used. It's just needet make
        //URL#openStream() not fail.
        this.testFeed = FeedServiceTest.class.getResource("/arquillian.xml");

        this.feedService.em = mock(EntityManager.class);
        this.feedQuery = mock(TypedQuery.class);
        when(this.feedService.em.createNamedQuery(Feed.FIND_BY_URL, Feed.class)).thenReturn(this.feedQuery);

        setUpFeedList();
        mockFeedParser();

        this.inOrder = inOrder(this.feedQuery, this.feedService.em, this.parser, this.feedService.parsers, this.parsers, this.newFeed);
    }

    @SuppressWarnings("unchecked")
    private void mockFeedParser() throws IOException, ParsingException {
        this.feedService.parsers = mock(Instance.class);
        this.parsers = mock(Iterator.class);
        FeedParser parser1 = mock(FeedParser.class);
        this.parser = mock(FeedParser.class);
        this.newFeed = mock(Feed.class);

        when(parser1.isFeedParsable(any(InputStream.class))).thenReturn(false);
        when(this.parser.isFeedParsable(any(InputStream.class))).thenReturn(true);
        when(this.parser.parse(any(InputStream.class))).thenReturn(this.newFeed);
        when(this.parsers.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(this.parsers.next()).thenReturn(parser1).thenReturn(this.parser).thenThrow(new NoSuchElementException());
        when(this.feedService.parsers.iterator()).thenReturn(this.parsers);
    }

    private void setUpFeedList() {
        this.feeds = new LinkedList<Feed>();
        this.feed = new Feed();
        feed.setUrl(testFeed);
        feeds.add(feed);
    }

    @Test
    public void findOrCreateFeedNotFoundTest() throws FeedUrlInvalidException, MalformedURLException {
        when(this.feedQuery.getResultList()).thenReturn(new LinkedList<Feed>()).thenReturn(feeds);
        Feed feedReturned = this.feedService.findOrCreate(testFeed);

        verifyTestFeedSearched();
        verifyFeedParserSearched();
        verifyTestFeedParsed();
        verifyFeedUrlSet();
        verifyFeedPersited();
        verifyTestFeedSearched();

        assertSame(feed, feedReturned);
    }

    @Test
    public void findOrCreateFeedFoundTest() throws MalformedURLException, FeedUrlInvalidException {
        when(this.feedQuery.getResultList()).thenReturn(this.feeds);
        Feed feedReturned = this.feedService.findOrCreate(testFeed);

        verifyTestFeedSearched();
        inOrder.verifyNoMoreInteractions();

        assertSame(feed, feedReturned);
    }

    @Test(expected = FeedUrlInvalidException.class)
    public void findOrCreateFeedNoParserAvailableTest() throws FeedUrlInvalidException, IOException {
        when(this.parser.isFeedParsable(any(InputStream.class))).thenReturn(false);
        this.feedService.findOrCreate(testFeed);
    }

    @Test(expected = FeedUrlInvalidException.class)
    public void findOrCreateParsingFailedTest() throws FeedUrlInvalidException, ParsingException, IOException {
        doThrow(ParsingException.class).when(this.parser).parse(any(InputStream.class));
        this.feedService.findOrCreate(testFeed);
    }

    @Test(expected = FeedUrlInvalidException.class)
    public void findOrCreateInvalidFeedTest() throws FeedUrlInvalidException, MalformedURLException {
        this.feedService.findOrCreate(new URL("file:/Non-Existing-File"));
    }

    @Test(expected = FeedUrlInvalidException.class)
    public void findOrCreateFeedToBigTest() throws IOException, FeedUrlInvalidException {
        File tmp = createTempFile(FeedService.MAX_FEED_SIZE + 1);

        //Should fail because file is to big.
        this.feedService.findOrCreate(tmp.toURI().toURL());
    }

    @Test
    public void findOrCreateFeedMaxSizeTest() throws IOException {
        File tmp = createTempFile(FeedService.MAX_FEED_SIZE);
        try {
            this.feedService.findOrCreate(tmp.toURI().toURL());
        } catch (FeedUrlInvalidException ex) {
            ex.printStackTrace();
            fail("Methode should not fail for streams that are " + FeedService.MAX_FEED_SIZE + " big.");
        }
    }

    private void verifyTestFeedSearched() {
        inOrder.verify(this.feedService.em).createNamedQuery(Feed.FIND_BY_URL, Feed.class);
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