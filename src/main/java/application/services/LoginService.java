package application.services;

import diploma.models.User;

import java.net.Inet4Address;

public interface LoginService {

    String loginUser(User user, Inet4Address server, char[] password);
    User getCurrentUser();
}
