package application.services;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordEncryptServiceImpl implements PasswordEncryptService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordEncryptServiceImpl.class);

    /**
     * Create hash code of password
     *
     * @param password String raw pass
     * @return String hashed pass
     */
    @Override
    public String hashPassword(char[] password) {
        try {
            //static salt - there is just study proj
            String salt = "String.valueOf(Math.random() * 100)";
            String saltedPassword = Arrays.toString(password) + salt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(saltedPassword.getBytes());

            // transform to string
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
