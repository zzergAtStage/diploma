package application.models;


public class UserCredentials {
    private final int userId;
    int userPasswordHash = 0;

    public UserCredentials(int userId, int hashPassword) {
        this.userId = userId;
        this.userPasswordHash = hashPassword;
    }
}
