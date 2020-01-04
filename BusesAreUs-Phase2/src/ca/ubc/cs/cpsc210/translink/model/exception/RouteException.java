package ca.ubc.cs.cpsc210.translink.model.exception;

/**
 * Represents exception raised when there is an error with routes
 */
public class RouteException extends Exception {
    public RouteException(String msg) {
        super(msg);
    }
}
