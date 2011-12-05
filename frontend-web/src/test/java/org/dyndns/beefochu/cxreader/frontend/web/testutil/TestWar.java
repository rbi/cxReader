package org.dyndns.beefochu.cxreader.frontend.web.testutil;

import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class TestWar {

    private static String WAR_NAME = "frontend-web-1.0-SNAPSHOT.war";
    private static String DEPLOYMENT_PATH = "frontend-web-1.0-SNAPSHOT";
    //defined in arquillian.xml
    private static int HTTP_PORT = 12345;

    public static Archive getTestWar() {
        return ShrinkWrap.createFromZipFile(WebArchive.class, new File("target/" + WAR_NAME));
    }

    public static String getUrl() {
        return "http://localhost:" + HTTP_PORT + "/" + DEPLOYMENT_PATH;
    }
}
