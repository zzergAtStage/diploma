package diploma.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class User {
    private String userName;
    private String userLastName;
    private int userId;
    @Setter
    private boolean online;
    public User(String name, String userLastName, int userId){
        this.userName = name;
        this.userLastName = userLastName;
        this.userId = userId;
        this.online = false;
    }
}
