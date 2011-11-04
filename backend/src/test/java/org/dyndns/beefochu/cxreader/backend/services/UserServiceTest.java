package org.dyndns.beefochu.cxreader.backend.services;

import org.dyndns.beefochu.cxreader.backend.services.UserService;
import org.junit.Ignore;
import java.util.List;
import org.mockito.InOrder;
import java.util.LinkedList;
import javax.persistence.EntityManager;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import javax.persistence.TypedQuery;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import java.net.MalformedURLException;
import java.net.URL;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private static final String TESTUSER = "testuser";
    private TypedQuery<ReaderUser> userQuery;
    private InOrder inOrder;
    private ReaderUser user;
    private List<ReaderUser> users;
    private List<FeedUserRelation> feeds;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.userService = new UserService();

        this.userService.em = mock(EntityManager.class);
        this.userQuery = mock(TypedQuery.class);
        this.inOrder = inOrder(this.userQuery, this.userService.em);
        this.user = mock(ReaderUser.class);

        when(this.userService.em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class)).thenReturn(this.userQuery);

        users = new LinkedList<ReaderUser>();
        feeds = new LinkedList<FeedUserRelation>();

        when(this.user.getFeeds()).thenReturn(feeds);
        users.add(this.user);
    }

    @Test
    public void testGetFeedListUserDoesntExist() {
        when(this.userQuery.getResultList()).thenReturn(new LinkedList<ReaderUser>()).thenReturn(users);

        List<FeedUserRelation> feedsReturned = userService.getFeedList(TESTUSER);

        verifyUserExistenceChecked();
        verifyUserCreated();
        verifyUserExistenceChecked();
        assertSame(feeds, feedsReturned);
    }

    @Test
    public void testGetFeedListUserDoesExist() {
        when(this.userQuery.getResultList()).thenReturn(users);

        List<FeedUserRelation> feedsReturned = userService.getFeedList(TESTUSER);

        verifyUserExistenceChecked();
        inOrder.verifyNoMoreInteractions();
        assertSame(feeds, feedsReturned);
    }

    @Test
    public void testAddFeed() throws MalformedURLException, FeedUrlInvalidException, FeedAlreadyInListException {
        when(this.userQuery.getResultList()).thenReturn(users);
        
        FeedUserRelation feed = userService.addFeed(TESTUSER, new Feed(new URL("http://test.feed/x")));
        verifyFeedUserRelationPersisted(feed);
        assertTrue(this.feeds.contains(feed));
    }

    @Test(expected = FeedAlreadyInListException.class)
    public void testAddFeedAllreadyInList() throws FeedUrlInvalidException, FeedAlreadyInListException, MalformedURLException {
        when(this.userQuery.getResultList()).thenReturn(users);
        
        Feed feed = new Feed(new URL("http://test.feed/x"));
        
        final FeedUserRelation feedRelation = new FeedUserRelation(this.user, feed);
        this.feeds.add(feedRelation);

        userService.addFeed(TESTUSER, feed);
    }

    @Test
    @Ignore("not implemented")
    public void removeBookmark() {
        fail("not implemented");
    }

    @Test
    @Ignore("not implemented")
    public void removeBookmarkNotInList() {
        fail("not implemented");
    }

    private void verifyUserExistenceChecked() {
        inOrder.verify(this.userService.em).createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class);
        inOrder.verify(this.userQuery).setParameter("username", TESTUSER);
        inOrder.verify(this.userQuery).getResultList();
    }

    private void verifyUserCreated() {
        inOrder.verify(this.userService.em).persist(any(ReaderUser.class));
    }

    private void verifyFeedUserRelationPersisted(FeedUserRelation feed) {
        verify(this.userService.em).persist(feed);
    }
}
