package org.dyndns.beefochu.cxreader.ejb.ejb;

import java.net.MalformedURLException;
import java.net.URL;
import javax.transaction.NotSupportedException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;
import org.junit.Ignore;
import org.dyndns.beefochu.cxreader.ejb.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.ejb.domain.FeedUserRelation;
import javax.annotation.Resource;
import org.dyndns.beefochu.cxreader.ejb.domain.ReaderUser;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ejb.EJB;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.dyndns.beefochu.cxreader.ejb.Reader;
import org.jboss.arquillian.container.test.api.Deployment;
import javax.transaction.UserTransaction;
import org.dyndns.beefochu.cxreader.ejb.testutil.TestEjbJar;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReaderFacadeIT {
    private static final String TESTUSER = "testUser";
    private String TESTFEEDURL = "http://test.tdl/test.xml";
    
    @EJB
    private Reader reader;
    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    @Deployment
    public static JavaArchive deploy() {
        return TestEjbJar.getTestEjbJar();
    }

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
    public void testAddBookmarkAndGetFeedList() throws MalformedURLException, FeedUrlInvalidException, FeedAlreadyInListException {
        assertTrue(reader.getFeedList(TESTUSER).isEmpty());
        FeedUserRelation relation = reader.bookmarkFeed(TESTUSER, new URL(TESTFEEDURL));
        assertEquals(relation.getFeed().getUrl(), new URL(TESTFEEDURL));
        assertTrue(reader.getFeedList(TESTUSER).contains(relation));
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
