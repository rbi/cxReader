package org.dyndns.beefochu.cxreader.ejb.domain;

import javax.ejb.EJB;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import com.sun.appserv.security.ProgrammaticLogin;
import org.dyndns.beefochu.cxreader.ejb.Reader;
import org.jboss.arquillian.container.test.api.Deployment;
import java.io.File;
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

    @Deployment
    public static JavaArchive deploy() {
        return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/ejb-1.0-SNAPSHOT.jar"));
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetFeedList() {
        char [] pw =  {'t','e','s','t'};
        new ProgrammaticLogin().login("testuser",pw);
        
        assertTrue(reader.getFeedList().isEmpty());
        fail("should not work if not logged in");
    }
}
