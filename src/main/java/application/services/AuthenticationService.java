package application.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private static AuthenticationService instance;

    private AuthenticationService(){

    }

    // Get instance
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    public boolean authenticateUser(String username, String password) {
        try {
            PasswordEncryptService encryptService = new PasswordEncryptServiceImpl();
            String encodedPass = encryptService.hashPassword(password);
            // 'cause we're making a study proj - our server authorization is simple
            URL url = new URL("https://localhost:9999/authenticate?username=" + username
                    + "&password=" + encodedPass);

            // open http connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // get response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();
            in.close();
            logger.info("Status {}", response);
            // check response
            return response.equals("authenticated");
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
