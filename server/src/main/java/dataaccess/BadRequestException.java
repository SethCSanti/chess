package dataaccess;

/**
 * Indicates the request could not be completed
 */
public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
