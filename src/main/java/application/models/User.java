package application.models;

import application.services.UserEventListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class User {
    private String userName;
    private transient char[] password;

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        //notify all subscribers
        isAuthenticated = authenticated;

    }

    private boolean isAuthenticated;
    public User(String name, char[] password){
        this.userName = name;
        this.password = password;
        this.isAuthenticated = false;
    }
}

