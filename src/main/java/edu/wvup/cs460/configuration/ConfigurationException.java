package edu.wvup.cs460.configuration;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Exception representing a configuration problem.
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}
