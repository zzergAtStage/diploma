package application.services;

import application.models.User;
import sun.net.ConnectionResetException;

public interface AuthenticationService {
    boolean authenticateUser(String serverTemplate, String username, char[] password) throws ConnectionResetException;

    void logOffUser(User firstUser);
}
