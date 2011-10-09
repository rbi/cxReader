package org.dyndns.beefochu.cxreader.ejb.testUtils;

public interface LoginHelper {
    /**
     * Loggs in a user
     * @param username the name for the user to logg in
     * @param roles the roles the logged in user should have
     */
    public void loginUser(String username, String [] roles);

    /**
     * @see #loginUser(java.lang.String, java.lang.String[])
     */
    public void loginUser(String string, String role);
}
