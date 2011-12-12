package org.dyndns.beefochu.cxreader.backend.testutil;

import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class TestEjbJar {

    private static final String JAR_NAME = "backend-1.0-SNAPSHOT.jar";

    @SuppressWarnings("rawtypes")
	public static Archive getTestEjbJar() {
        return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/" + JAR_NAME));
        		//.addAsManifestResource(new File("src/test/resources/glassfish-ejb-jar.xml"), "/glassfish-ejb-jar.xml");
    }
}
