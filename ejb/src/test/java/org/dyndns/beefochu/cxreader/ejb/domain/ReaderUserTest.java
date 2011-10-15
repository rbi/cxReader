package org.dyndns.beefochu.cxreader.ejb.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReaderUserTest {

    private ReaderUser user;

    @Before
    public void setUp() {
        this.user = new ReaderUser();
    }

    @Test
    public void testGetFeedList() {
        List<Feed> feeds = user.getFeedList();
        boolean[] exceptionThrown = {false, false};

        try {
            feeds.add(new Feed());
        } catch (UnsupportedOperationException e) {
            exceptionThrown[0] = true;
        }

        try {
            feeds.remove(new Feed());
        } catch (UnsupportedOperationException e) {
            exceptionThrown[1] = true;
        }

        assertTrue(exceptionThrown[0]);
        assertTrue(exceptionThrown[1]);
    }

    @Test
    @Ignore("Implementation follows")
    public void testAddFeed() throws MalformedURLException, FeedUrlInvalidException, FeedAlreadyInListException {
        user.addFeed(new URL("http://test.feed/x"));

        fail("Not implemented");
    }

    @Test(expected = FeedAlreadyInListException.class)
    public void testAddFeedAllreadyInList() throws FeedUrlInvalidException, FeedAlreadyInListException, MalformedURLException {
        user.addFeed(new URL("http://test.feed/x"));
        user.addFeed(new URL("http://test.feed/x"));
    }
}
