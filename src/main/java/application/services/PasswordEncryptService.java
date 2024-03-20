package application.services;

/**
 * Interface let us create encryptedPasswords
 */
public interface PasswordEncryptService {
    /**
     * Create hash code of password
     * @param password String raw pass
     * @return String hashed pass
     */
    String hashPassword(String password);

}
