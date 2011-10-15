package org.dyndns.beefochu.cxreader.ejb.testutil;

import java.io.File;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class TestEjbJar {
    
    private static final String JAR_NAME = "ejb-1.0-SNAPSHOT.jar";
    
    public static JavaArchive getTestEjbJar() {
     return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/"+JAR_NAME));
    }
}
