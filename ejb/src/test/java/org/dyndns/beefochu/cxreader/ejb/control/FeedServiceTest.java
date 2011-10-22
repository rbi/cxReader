package org.dyndns.beefochu.cxreader.ejb.control;

import java.util.NoSuchElementException;
import java.io.InputStream;
import java.util.Iterator;
import javax.enterprise.inject.Instance;
import java.util.List;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;
import org.junit.Test;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import org.dyndns.beefochu.cxreader.ejb.domain.Feed;
import org.junit.Before;
import static org.junit.Assert.*;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

public class FeedServiceTest {

    private URL testFeed;
    private FeedService feedService = new FeedService();
    private TypedQuery<Feed> feedQuery;
    private InOrder inOrder;
    private List<Feed> feeds;
    private Feed feed;
    private FeedParser parser;
    private Iterator<FeedParser> parsers;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        //We only need an existing file so that URL#openStream() doesn't fail.
        //It doesn't matter what file this is.
        this.testFeed = FeedServiceTest.class.getResource("/arquillian.xml");
        this.feedService.em = mock(EntityManager.class);
        this.feedQuery = mock(TypedQuery.class);
        when(this.feedService.em.createNamedQuery(Feed.FIND_BY_URL, Feed.class)).thenReturn(this.feedQuery);
        
        setUpFeedList();
        mockFeedParser();
        
        this.inOrder = inOrder(this.feedQuery, this.feedService.em, this.parser, this.feedService.parsers, this.parsers);
    }

    @SuppressWarnings("unchecked")
    private void mockFeedParser() {
        this.feedService.parsers = mock(Instance.class);
        this.parsers = mock(Iterator.class);
        FeedParser parser1 = mock(FeedParser.class);
        this.parser = mock(FeedParser.class);

        when(parser1.isFeedParsable(any(InputStream.class))).thenReturn(false);
        when(this.parser.isFeedParsable(any(InputStream.class))).thenReturn(true);
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
        verifyFeedPersited();
        verifyTestFeedSearched();
        verifyTestFeedUpdated();

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

    @Test(expected=FeedUrlInvalidException.class)
    public void findOrCreateFeedNoParserAvailableTest() throws FeedUrlInvalidException {
        when(this.parser.isFeedParsable(any(InputStream.class))).thenReturn(false);
        this.feedService.findOrCreate(testFeed);
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
    }

    private void verifyTestFeedUpdated() {
        inOrder.verify(this.parser).update(any(Feed.class), any(InputStream.class));
    }
}