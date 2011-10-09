package org.dyndns.beefochu.cxreader.ejb.ejb;

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
import java.io.File;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReaderGatewayIT {

    private static final String TESTUSER = "testUser";
    
    @EJB
    private Reader reader;
    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    @Deployment
    public static JavaArchive deploy() {
        return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/ejb-1.0-SNAPSHOT.jar"));
    }

    @Test
    public void testGetFeedList() throws Exception {
        try {
            assertTrue(getTestUser().isEmpty());
            assertTrue(reader.getFeedList(TESTUSER).isEmpty());
            reader.save();
            assertEquals(1, getTestUser().size());
        } finally {
            utx.begin();
            for(ReaderUser user: getTestUser())
                em.remove(user);
            utx.commit();
        }
    }

    private List<ReaderUser> getTestUser() {
        TypedQuery<ReaderUser> userQuery = em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class);
        userQuery.setParameter("username", TESTUSER);
        return userQuery.getResultList();
    }
}
