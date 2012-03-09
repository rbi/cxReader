package org.dyndns.beefochu.cxreader.frontend.web.testutil;

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import com.sun.appserv.security.ProgrammaticLogin;

public class LoginHelper {
	public static final String USER_NAME = "testUser";
	private static final Configuration config = new Configuration() {
		@Override
		public AppConfigurationEntry[] getAppConfigurationEntry(String name) {

			if ("default".equals(name) || "fileRealm".equals(name))
				return new AppConfigurationEntry[] { new AppConfigurationEntry(
						DummyLoginModul.class.getName(),
						LoginModuleControlFlag.REQUIRED,
						new HashMap<String, Boolean>()) };
			;
			return null;
		}

		@Override
		public void refresh() {
		}
	};

	public static void login() {
		Configuration.setConfiguration(config);
		ProgrammaticLogin login = new ProgrammaticLogin();
		try {
			login.login(USER_NAME, "test".toCharArray(), "fileRealm", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}