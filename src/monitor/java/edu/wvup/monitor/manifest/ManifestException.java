package edu.wvup.monitor.manifest;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "I code not because I have a problem to solve, but because there is
 * code within me, crying to get out."
 */
public class ManifestException extends Exception{
    public ManifestException() {
        super();
    }

    public ManifestException(String message) {
        super(message);
    }

    public ManifestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManifestException(Throwable cause) {
        super(cause);
    }


}
