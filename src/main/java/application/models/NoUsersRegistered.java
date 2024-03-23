package application.models;

import org.apache.log4j.lf5.LogLevel;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NoUsersRegistered extends Throwable{
    Logger logger = Logger.getAnonymousLogger();

    /**
     * Constructs a new throwable with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     */
    public NoUsersRegistered() {
        logger.log(Level.WARNING, "Не найден текущий пользователь в приложении");
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "\nНе найден текущий пользователь в приложении";
    }

}
