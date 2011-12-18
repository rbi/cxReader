package org.dyndns.beefochu.cxreader.backend.testutil;

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import com.sun.appserv.security.ProgrammaticLogin;

public class LoginHelper {
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

	public static void login(String username) {
		Configuration.setConfiguration(config);
		ProgrammaticLogin login = new ProgrammaticLogin();
		try {
			login.login(username, "test".toCharArray(), "fileRealm", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void logout() {
		ProgrammaticLogin login = new ProgrammaticLogin();
		login.logout();
	}
}