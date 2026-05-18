package result;

import server.Response;

public class LoginResult extends Response {
    String username;
    String authToken;
    public LoginResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
    public LoginResult(String message) { super(message); }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}