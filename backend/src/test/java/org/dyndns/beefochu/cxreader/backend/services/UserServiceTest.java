package org.dyndns.beefochu.cxreader.backend.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.AssertFalse;

import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

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
        this.user = mock(ReaderUser.class);
        this.inOrder = inOrder(this.userQuery, this.userService.em, this.user);

        when(this.userService.em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class)).thenReturn(this.userQuery);

        users = new LinkedList<ReaderUser>();
        feeds = new LinkedList<FeedUserRelation>();

        when(this.user.getFeeds()).thenReturn(feeds);
        users.add(this.user);
        
        when(this.userQuery.getResultList()).thenReturn(users);
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
        List<FeedUserRelation> feedsReturned = userService.getFeedList(TESTUSER);

        verifyUserExistenceChecked();
        verifyFeedUserRelationRetrived();
        inOrder.verifyNoMoreInteractions();
        assertSame(feeds, feedsReturned);
    }

	@Test
    public void testAddFeed() throws MalformedURLException, FeedUrlInvalidException, FeedAlreadyInListException {
        FeedUserRelation feed = userService.addFeed(TESTUSER, new Feed(new URL("http://test.feed/x")));
        verifyFeedUserRelationPersisted(feed);
        assertTrue(this.feeds.contains(feed));
    }

    @Test(expected = FeedAlreadyInListException.class)
    public void testAddFeedAllreadyInList() throws FeedUrlInvalidException, FeedAlreadyInListException, MalformedURLException {
        Feed feed = new Feed(new URL("http://test.feed/x"));
        
        final FeedUserRelation feedRelation = new FeedUserRelation(this.user, feed);
        this.feeds.add(feedRelation);

        userService.addFeed(TESTUSER, feed);
    }

    @Test
    public void removeBookmarkNotInList() {
    	FeedUserRelation param = createFeedUserRelationWithId(1, new Feed());
    	FeedUserRelation feed1 = createFeedUserRelationWithId(2, new Feed());
    	FeedUserRelation feed2 = createFeedUserRelationWithId(3, new Feed());
    	
    	this.feeds.add(feed1);
    	this.feeds.add(feed2);
    	
        assertNull(userService.removeBookmarkedFeed(TESTUSER, param));
        
        verifyUserExistenceChecked();
        verifyFeedUserRelationRetrived();
        inOrder.verifyNoMoreInteractions();
    }

	@Test
    public void removeBookmarkInList() {
		Feed feedToReturn = new Feed();
    	FeedUserRelation param = createFeedUserRelationWithId(1, new Feed());
    	FeedUserRelation feed1 = createFeedUserRelationWithId(2, new Feed());
    	FeedUserRelation feed2 = createFeedUserRelationWithId(1, feedToReturn);
    	
    	this.feeds.add(feed1);
    	this.feeds.add(feed2);
    	
        assertSame(feedToReturn, userService.removeBookmarkedFeed(TESTUSER, param));
        verifyUserExistenceChecked();
        verifyFeedUserRelationRetrived();
        assertFalse(this.feeds.contains(feed2));
        inOrder.verify(this.userService.em).remove(feed2);
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
    
    private void verifyFeedUserRelationRetrived() {
		inOrder.verify(this.user).getFeeds();
	}
    
    private FeedUserRelation createFeedUserRelationWithId(int id, Feed feed) {
		FeedUserRelation rel = new FeedUserRelation(this.user, feed);
		
		try {
			Field idField = FeedUserRelation.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(rel, id);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rel;
	}
}
