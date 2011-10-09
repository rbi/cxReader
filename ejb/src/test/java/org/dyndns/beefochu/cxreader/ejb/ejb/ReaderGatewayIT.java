package org.dyndns.beefochu.cxreader.ejb.ejb;

import org.dyndns.beefochu.cxreader.ejb.testUtils.GlassfishLoginHelper;
import org.dyndns.beefochu.cxreader.ejb.util.Roles;
import org.dyndns.beefochu.cxreader.ejb.testUtils.LoginHelper;
import javax.ejb.EJB;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.dyndns.beefochu.cxreader.ejb.Reader;
import org.jboss.arquillian.container.test.api.Deployment;
import java.io.File;
import javax.ejb.EJBAccessException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReaderGatewayIT {

    @EJB
    private Reader reader;
    //TODO get @Inject working
    private LoginHelper loginHelper = new GlassfishLoginHelper();

    @Deployment
    public static JavaArchive deploy() {
        return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/ejb-1.0-SNAPSHOT.jar"));
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetFeedList() {
        loginHelper.loginUser("testUser", Roles.USER);

        assertTrue(reader.getFeedList().isEmpty());
    }

    @Test(expected = EJBAccessException.class)
    public void testNotLoggedIn() {
        reader.getFeedList();
    }
}
