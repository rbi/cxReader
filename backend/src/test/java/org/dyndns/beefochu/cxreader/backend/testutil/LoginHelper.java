package org.dyndns.beefochu.cxreader.backend.testutil;

import com.sun.appserv.security.ProgrammaticLogin;

public class LoginHelper {

	private static final String PASSWORT = "test";
	
	public static void login(String username) {
		ProgrammaticLogin login = new ProgrammaticLogin();
		try {
			login.login(username, PASSWORT.toCharArray(), "fileRealm", true);
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