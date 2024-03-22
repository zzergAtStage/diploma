package application;

import application.models.NoUsersRegistered;
import application.models.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class UserRepository {
    private List<User> userRepo;


    public UserRepository(){
        this.userRepo = new ArrayList<>();
    }
    public boolean addUser(User user) {
        return userRepo.add(user);
    }

    public User getFirstUser() throws NoUsersRegistered {
        return userRepo.stream().findFirst().orElseThrow(NoUsersRegistered::new);
    }

    public User getUser(String username) throws NoUsersRegistered {
       return userRepo.stream()
               .filter(e -> e.getUserName().equalsIgnoreCase(username))
               .findFirst()
               .orElseThrow(NoUsersRegistered::new);
    }
}
