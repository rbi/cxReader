package org.beefochu.dyndns.cxreader.ejb.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.beefochu.dyndns.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.beefochu.dyndns.cxreader.ejb.exceptions.FeedUrlInvalidException;
import org.junit.Before;
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
        boolean [] exceptionThrown = {false, false};
        
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
    public void testAddFeed() throws MalformedURLException, FeedUrlInvalidException, FeedAlreadyInListException {
        user.addFeed(new URL("http://test.feed/x"));
        
        fail("Not implemented");
    }
}
