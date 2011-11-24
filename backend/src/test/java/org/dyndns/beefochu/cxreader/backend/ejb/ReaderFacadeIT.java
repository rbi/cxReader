package org.dyndns.beefochu.cxreader.backend.ejb;

import org.jboss.shrinkwrap.api.Archive;
import org.dyndns.beefochu.cxreader.backend.Reader;
import java.net.URL;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.testutil.TestEjbJar;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReaderFacadeIT {
    private static final String TESTUSER = "testUser";
    private URL TESTFEEDURL = ReaderFacadeIT.class.getResource("/testfeed.xml");
    
    @EJB
    private Reader reader;
    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    @Deployment
    public static Archive deploy() {
        return TestEjbJar.getTestEjbJar();
    }
    
    @Before
    public void before() {
        //check precondition
        assertTrue(getTestUser().isEmpty());
    }

    @After
    public void cleanUp() throws Exception {
        utx.begin();
        for (ReaderUser user : getTestUser()) {
            deleteUserWithReferences(user);
        }
        utx.commit();
    }

    @Test
    public void testCreateUserIfNotExits() throws Exception {
        assertTrue(reader.getFeedList(TESTUSER).isEmpty());
        assertEquals(1, getTestUser().size());
    }
    
    @Test
    public void testAddBookmarkAndGetFeedList() throws FeedUrlInvalidException, FeedAlreadyInListException {
        FeedUserRelation relation = reader.bookmarkFeed(TESTUSER, TESTFEEDURL);
        assertEquals(relation.getFeed().getUrl(), TESTFEEDURL);
        
        List<FeedUserRelation> feedList = reader.getFeedList(TESTUSER);
        assertEquals(1, feedList.size());
        assertEquals(TESTFEEDURL, relation.getFeed().getUrl());
    }
    
    @Test
    public void testAddInvalidBookmark() throws FeedAlreadyInListException {
        boolean invalidUrl = false;
        try {
            reader.bookmarkFeed(TESTUSER, ReaderFacadeIT.class.getResource("/arquillian.xml"));
        } catch (FeedUrlInvalidException ex) {
            invalidUrl = true;
        }
        assertTrue(invalidUrl);
        assertEquals(0, reader.getFeedList(TESTUSER).size());
    }

    @Test
    public void testAddFeedBookmarkTwoTimes() throws FeedUrlInvalidException, FeedAlreadyInListException {
        boolean exceptionThrown = false;
        reader.bookmarkFeed(TESTUSER, TESTFEEDURL);
        try {
            reader.bookmarkFeed(TESTUSER, TESTFEEDURL);
        } catch (FeedAlreadyInListException ex) {
            exceptionThrown = true;
        }
        
        assertTrue(exceptionThrown);
        assertEquals(1, reader.getFeedList(TESTUSER).size());
    }
    
    private List<ReaderUser> getTestUser() {
        TypedQuery<ReaderUser> userQuery = em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class);
        userQuery.setParameter("username", TESTUSER);
        return userQuery.getResultList();
    }

    /**
     * 
     * Deletes a user and all entities that are referenced by that user,
     * even Reader and ReaderEntry entities.
     */
    private void deleteUserWithReferences(ReaderUser user) {
        for (FeedUserRelation relation : user.getFeeds()) {
            em.remove(relation.getFeed());
        }
        em.remove(user);
    }
}
