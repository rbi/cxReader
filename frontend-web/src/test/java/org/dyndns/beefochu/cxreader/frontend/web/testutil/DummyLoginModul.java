package org.dyndns.beefochu.cxreader.frontend.web.testutil;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class DummyLoginModul implements LoginModule {


	@Override
	public boolean abort() throws LoginException {
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		return true;
	}

	@Override
	public void initialize(Subject subject, CallbackHandler handler,
			Map<String, ?> sharedState, Map<String, ?> options) {
	}

	@Override
	public boolean login() throws LoginException {
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
	}
}