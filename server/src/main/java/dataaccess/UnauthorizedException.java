package dataaccess;

/**
 * Indicates the request was not authorized or did not have an auth token
 */
public class UnauthorizedException extends Exception{
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable ex) {
        super(message, ex);
    }
}
