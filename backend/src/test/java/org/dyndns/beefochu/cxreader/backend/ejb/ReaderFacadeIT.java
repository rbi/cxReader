package org.dyndns.beefochu.cxreader.backend.ejb;

import org.junit.After;
import org.jboss.shrinkwrap.api.Archive;
import java.net.URL;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import javax.annotation.Resource;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ejb.EJB;
import org.dyndns.beefochu.cxreader.backend.Reader;
import org.jboss.arquillian.container.test.api.Deployment;
import javax.transaction.UserTransaction;
import org.dyndns.beefochu.cxreader.backend.testutil.TestEjbJar;
import org.jboss.arquillian.junit.Arquillian;
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
        assertTrue(getTestUser().isEmpty());
        assertTrue(reader.getFeedList(TESTUSER).isEmpty());
        assertEquals(1, getTestUser().size());
    }
    
    @Test
    public void testAddBookmarkAndGetFeedList() throws FeedUrlInvalidException, FeedAlreadyInListException {
        assertTrue(reader.getFeedList(TESTUSER).isEmpty());
        FeedUserRelation relation = reader.bookmarkFeed(TESTUSER, TESTFEEDURL);
        assertEquals(relation.getFeed().getUrl(), TESTFEEDURL);
        
        List<FeedUserRelation> feedList = reader.getFeedList(TESTUSER);
        assertEquals(1, feedList.size());
        assertEquals(TESTFEEDURL, relation.getFeed().getUrl());
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
