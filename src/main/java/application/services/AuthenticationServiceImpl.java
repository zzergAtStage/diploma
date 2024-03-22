package application.services;

import application.models.User;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.ConnectionResetException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class AuthenticationServiceImpl implements AuthenticationService{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private static AuthenticationServiceImpl instance;
    private Publisher userEvents;
    private AuthenticationServiceImpl() {
        BasicConfigurator.configure();
        PropertyConfigurator.configure("src/main/resources/log4j.properties");
        userEvents = Publisher.getInstance();
    }

    // Get instance of singleton
    public static synchronized AuthenticationServiceImpl getInstance() {
        if (instance == null) {
            instance = new AuthenticationServiceImpl();
        }
        return instance;
    }
    @Override
    public boolean authenticateUser(String serverTemplate, String username, char[] password) throws ConnectionResetException {
        try {


            String server;
            if (!serverTemplate.isEmpty()) {
                server = serverTemplate;
            } else {
                server = "localhost:9999";
            }
            PasswordEncryptService encryptService = new PasswordEncryptServiceImpl();
            String encodedPass = encryptService.hashPassword(password);
            // 'cause we're making a study proj - our server authorization is simple
            URL url = new URL("https://" + server + "/authenticate");

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            // skip cert checks
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());

            // tune the http connector
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String data = username + ":" + String.valueOf(password);
            String dataRequestBody = "username="+username + "&password=" + String.valueOf(password);//encodedPass;
            String encoding = Base64.getEncoder().encodeToString((data).getBytes(StandardCharsets.UTF_8));

            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("Content-Type", "application/text");
            // add credentials as payload
            connection.getOutputStream().write(dataRequestBody.getBytes(StandardCharsets.UTF_8));
            int responseCode = connection.getResponseCode();

            logger.debug("Status {}", responseCode);
            Publisher.getInstance().notify(Publisher.LOGON,"User is logged");
            // check response
            return (responseCode >= 200 && responseCode < 300);

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ConnectionResetException(e.getMessage());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logOffUser(User firstUser) {
        //as we use http basic authentication, we won't send any request to the server, just drop auth field
        Publisher.getInstance().notify(Publisher.LOGOUT,"User is logged off");
        firstUser.setAuthenticated(false);
    }


}
