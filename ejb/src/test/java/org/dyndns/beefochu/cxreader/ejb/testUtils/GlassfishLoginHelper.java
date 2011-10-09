package org.dyndns.beefochu.cxreader.ejb.testUtils;

import com.sun.appserv.security.ProgrammaticLogin;
import javax.inject.Singleton;
import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.embedded.Server;

@Singleton
public class GlassfishLoginHelper implements LoginHelper {

    private static final String defaultPassword = "secret";
    private Server server;
    
    @Override
    public void loginUser(String username, String[] roles) {
        if(this.server == null)
            this.server = getRunningInstance();
        
        if(userExists(username))
            deleteUser(username);
        createUser(username, roles);
        
        new ProgrammaticLogin().login(username, defaultPassword.toCharArray());
    }

    @Override
    public void loginUser(String string, String role) {
        String[] roles = {role};
        loginUser(role, roles);
    }

    private void createUser(String username, String[] roles) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void deleteUser(String username) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean userExists(String username) {
        ActionReport report = executeCommand("list-file-users");
        System.out.println("----------------------------" + report.getMessage());
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ActionReport executeCommand(String command) {
        CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
        ActionReport report = server.getHabitat().getComponent(ActionReport.class);
        runner.getCommandInvocation(command, report).execute();
        return report;
    }

    private Server getRunningInstance() {
        return Server.getServer(Server.getServerNames().get(0));
    }
    
}
