package org.dyndns.beefochu.cxreader.backend.ejb;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.testutil.LoginHelper;
import org.dyndns.beefochu.cxreader.backend.testutil.TestEjbJar;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ReaderFacadeIT {
	private static final String TESTUSER = "testUser";
	private static final String TESTUSER2 = "testUser2";
	private URL TESTFEEDURL = ReaderFacadeIT.class.getResource("/testfeed.xml");

	@EJB
	private Reader reader;
	@PersistenceContext
	private EntityManager em;
	@Resource
	private UserTransaction utx;

	@SuppressWarnings("rawtypes")
	@Deployment
	public static Archive deploy() {
		return TestEjbJar.getTestEjbJar();
	}

	@Before
	public void before() {
		// check precondition
		assertTrue(getTestUser().isEmpty());
		LoginHelper.login(TESTUSER);
	}

	@After
	public void cleanUp() throws Exception {
		LoginHelper.logout();
		utx.begin();
		for (ReaderUser user : getTestUser()) {
			deleteUserWithReferences(user);
		}
		utx.commit();
	}

	@Test
	public void testCreateUserIfNotExits() throws Exception {
		assertTrue(reader.getFeedList().isEmpty());
		assertEquals(1, getTestUser().size());
	}

	@Test
	public void testAddBookmarkAndGetFeedList() throws FeedUrlInvalidException,
			FeedAlreadyInListException {
		FeedUserRelation relation = reader.bookmarkFeed(TESTFEEDURL);
		assertEquals(relation.getFeed().getUrl(), TESTFEEDURL);

		List<FeedUserRelation> feedList = reader.getFeedList();
		assertEquals(1, feedList.size());
		assertEquals(TESTFEEDURL, relation.getFeed().getUrl());
		
		//Feed should contain entries
		assertTrue(reader.getEntries(feedList.get(0), new Date(0)).size() != 0);
	}

	@Test
	public void testAddInvalidBookmark() throws FeedAlreadyInListException {
		boolean invalidUrl = false;
		try {
			reader.bookmarkFeed(ReaderFacadeIT.class
					.getResource("/arquillian.xml"));
		} catch (FeedUrlInvalidException ex) {
			invalidUrl = true;
		}
		assertTrue(invalidUrl);
		assertEquals(0, reader.getFeedList().size());
	}

	@Test
	public void testAddFeedBookmarkTwoTimes() throws FeedUrlInvalidException,
			FeedAlreadyInListException {
		boolean exceptionThrown = false;
		reader.bookmarkFeed(TESTFEEDURL);
		try {
			reader.bookmarkFeed(TESTFEEDURL);
		} catch (FeedAlreadyInListException ex) {
			exceptionThrown = true;
		}

		assertTrue(exceptionThrown);
		assertEquals(1, reader.getFeedList().size());
	}

	@Test
	public void testRemoveFeedBookmarkNotBookmarked() throws FeedUrlInvalidException, FeedAlreadyInListException {
		FeedUserRelation rel = reader.bookmarkFeed(TESTFEEDURL);
		LoginHelper.logout();
		
		LoginHelper.login(TESTUSER2);
		reader.removeBookmarkedFeed(rel);
		LoginHelper.logout();
		

		LoginHelper.login(TESTUSER);
		boolean found = true;
		for(FeedUserRelation feed : reader.getFeedList()) {
			if(feed.getFeed().getUrl().equals(TESTFEEDURL))
				found = true;
		}
		assertTrue(found);
	}
	
	@Test
	public void testRemoveFeedBookmarkFeedGetsOrphan() throws FeedUrlInvalidException, FeedAlreadyInListException {
		FeedUserRelation rel = reader.bookmarkFeed(TESTFEEDURL);
		reader.removeBookmarkedFeed(rel);
		
		assertEquals("Orphan feed should have bean deleted but wasn't.", 0, findFeed(TESTFEEDURL).size());
	}

	@Test
	public void testRemoveFeedBookmarkFeedNotOrphan() throws FeedUrlInvalidException, FeedAlreadyInListException {
		reader.bookmarkFeed(TESTFEEDURL);
		LoginHelper.logout();
		
		LoginHelper.login(TESTUSER2);
		FeedUserRelation rel = reader.bookmarkFeed(TESTFEEDURL);
		reader.removeBookmarkedFeed(rel);
		
		assertEquals("Feed was not orphan but was deleted anyway.", 1, findFeed(TESTFEEDURL).size());
	}
	
	@Test
	public void testGetAllFeedEntries() throws FeedUrlInvalidException, FeedAlreadyInListException {
		FeedUserRelation relation = reader.bookmarkFeed(TESTFEEDURL);
		List<FeedEntryUserRelation> entries = reader.getEntries(relation, new Date(0));
		assertEquals(2, entries.size());
		
		//title of first is clear because it's ordered by date
		assertEquals("An Example Entry", entries.get(0).getFeedEntry().getTitle());
		assertEquals("Another Example Entry", entries.get(1).getFeedEntry().getTitle());
	}
	
	@Test
	public void testGetFeedEntriesNewerThan() throws FeedUrlInvalidException, FeedAlreadyInListException {
		FeedUserRelation relation = reader.bookmarkFeed(TESTFEEDURL);
		
		List<FeedEntryUserRelation> entries = reader.getEntries(relation, new Date(1303224088104L));
		assertEquals(1, entries.size());
		assertEquals("An Example Entry", entries.get(0).getFeedEntry().getTitle());
		
		entries = reader.getEntries(relation, new Date(1303224088105L));
		assertEquals(0, entries.size());
	}
	
	@Test
	public void testSetReadStatus() throws FeedUrlInvalidException, FeedAlreadyInListException {
		FeedUserRelation relation = reader.bookmarkFeed(TESTFEEDURL);
		
		List<FeedEntryUserRelation> entries = reader.getEntries(relation, new Date(0));
		assertFalse(entries.get(0).isRead());
		
		reader.setReadStatus(entries.get(0), true);
		entries = reader.getEntries(relation, new Date(0));
		assertTrue(entries.get(0).isRead());

		reader.setReadStatus(entries.get(0), false);
		entries = reader.getEntries(relation, new Date(0));
		assertFalse(entries.get(0).isRead());
	}
	
	private List<Feed> findFeed(URL feedUrl) {
		TypedQuery<Feed> feeds = em.createNamedQuery(Feed.FIND_BY_URL, Feed.class);
		feeds.setParameter("url", feedUrl.toString());
		return feeds.getResultList();
	}
	
	private List<ReaderUser> getTestUser() {
		List<ReaderUser> users = new ArrayList<ReaderUser>(2);
		TypedQuery<ReaderUser> userQuery = em.createNamedQuery(
				ReaderUser.FIND_BY_NAME, ReaderUser.class);
		userQuery.setParameter("username", TESTUSER);
		users.addAll(userQuery.getResultList());
		
		
		userQuery.setParameter("username", TESTUSER2);
		users.addAll(userQuery.getResultList());
		
		return users;
	}

	/**
	 * 
	 * Deletes a user and all entities that are referenced by that user, even
	 * Reader and ReaderEntry entities.
	 */
	private void deleteUserWithReferences(ReaderUser user) {
		for (FeedUserRelation relation : user.getFeeds()) {
			em.remove(relation.getFeed());
		}
		em.remove(user);
	}
}
