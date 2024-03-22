package application.services;

public interface UserEventListener {
    void update(String updateType, String message);
}
