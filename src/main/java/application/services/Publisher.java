package application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Publisher{
    public static final String LOGON = "logOn";
    public static final String LOGOUT = "logOut" ;
    private static Publisher instance;
    private Map<String, List<UserEventListener>> listeners = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private Publisher(String... operations){
        for(String operation: operations){
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    public static synchronized Publisher getInstance() {
        if (instance == null) {
            instance = new Publisher(Publisher.LOGON,Publisher.LOGOUT);
        }
        return instance;
    }
    public void subscribe(String eventType, UserEventListener listener){
        List<UserEventListener> receivers = listeners.get(eventType);
        logger.error("Subscribed " + listener.getClass().getName() + " to even: " + eventType);
        receivers.add(listener);
    }

    public void unsubscribe(String eventType, UserEventListener listener){
        List<UserEventListener> receivers = listeners.get(eventType);
        receivers.remove(listener);
    }
    public void notify(String eventType, String message){
        List<UserEventListener> receivers = listeners.get(eventType);
        logger.info("Notifying receivers with event: " + eventType);
        for (UserEventListener listener: receivers){
            listener.update(eventType, message);
        }
    }
}
