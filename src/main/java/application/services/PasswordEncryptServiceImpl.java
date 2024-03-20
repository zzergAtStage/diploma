package application.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordEncryptServiceImpl implements PasswordEncryptService {
    /**
     * Create hash code of password
     *
     * @param password String raw pass
     * @return String hashed pass
     */
    @Override
    public String hashPassword(String password) {
        try {
            //static salt - there is just study proj
            String salt = "String.valueOf(Math.random() * 100)";
            String saltedPassword = password + salt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(saltedPassword.getBytes());

            // transform to string
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
