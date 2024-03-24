package application;

import application.models.NoUsersRegistered;
import application.models.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
public class UserRepository {
    private final List<User> userRepo;
    private static UserRepository userRepository;

    private UserRepository(){
        this.userRepo = new ArrayList<>();
    }

    /**
     * The main repository to hold user information
     * @return Singleton instance of UserRepository
     */
    public static synchronized UserRepository getInstance(){
        if (userRepository == null){
            userRepository = new UserRepository();
        }
        return userRepository;
    }
    public User addUser(User user) {
        userRepo.add(user);
        return user;
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
