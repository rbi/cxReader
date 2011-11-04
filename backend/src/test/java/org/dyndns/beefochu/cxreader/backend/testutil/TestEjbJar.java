package org.dyndns.beefochu.cxreader.backend.testutil;

import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class TestEjbJar {
    
    private static final String JAR_NAME = "backend-1.0-SNAPSHOT.jar";
    
    public static Archive getTestEjbJar() {
     return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("target/"+JAR_NAME));
//             return ShrinkWrap.create(EnterpriseArchive.class)
//                .addAsModule(new File("target/" + JAR_NAME))
//                .addAsLibraries(new File("target/dependencies/").listFiles());
    }
}
