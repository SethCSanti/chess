package result;

import model.Response;

public class RegisterResult extends Response {
    String username;
    String authToken;
    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
    public RegisterResult(String message) { super(message); }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
