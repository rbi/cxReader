package org.dyndns.beefochu.cxreader.frontend.web.testutil;

import java.io.File;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class TestWar {

	private static final String VERSION = "1.0-SNAPSHOT";
	private static String WAR_NAME = "frontend-web-" + VERSION + ".war";
	private static String BACKEND_CLIENT_NAME = "backend-" + VERSION + "-client.jar";
	private static String DEPLOYMENT_PATH = "frontend-web-" + VERSION;
	// defined in arquillian.xml
	private static int HTTP_PORT = 12345;

	public static EnterpriseArchive getAssembly() {
		return ShrinkWrap
				.create(EnterpriseArchive.class, "frontendTest.ear")
				.addAsModule(getDummyBackend())
				.addAsModule(getTestWar())
				.addAsLibrary(new File("../backend/target/"+BACKEND_CLIENT_NAME));
	}

	public static WebArchive getTestWar() {
		return ShrinkWrap.createFromZipFile(WebArchive.class, new File(
				"target/" + WAR_NAME));
	}

	public static JavaArchive getDummyBackend() {
		return ShrinkWrap
				.create(JavaArchive.class, "readerDummy.jar")
				.addAsManifestResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsManifestResource("ejb-jar.xml")
				.addClasses(ReaderDummy.class);
	}

	public static String getUrl() {
		return "http://localhost:" + HTTP_PORT + "/" + DEPLOYMENT_PATH;
	}
}
