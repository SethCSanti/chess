package result;

import server.Response;

public class RegisterResult extends Response {
    String username;
    String authToken;
    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
    public RegisterResult(String message) { super(message); }
}
