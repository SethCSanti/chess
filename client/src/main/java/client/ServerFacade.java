package client;

import com.google.gson.Gson;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final Gson gson = new Gson();

    private record ErrorResponse(String message) {}
    private final String baseUrl;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        var body = new RegisterRequest(username, password, email);
        var response = sendRequest("POST", "/user", body, null);
        return gson.fromJson(response, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws Exception {
        var body = new LoginRequest(username, password);
        var response = sendRequest("POST", "/session", body, null);
        return gson.fromJson(response, LoginResult.class);
    }

    public void logout(String authToken) throws Exception {
        sendRequest("DELETE", "/session", null, authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        var response = sendRequest("GET", "/game", null, authToken);
        return gson.fromJson(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        var body = new CreateGameRequest(gameName);
        var response = sendRequest("POST", "/game", body, authToken);
        return gson.fromJson(response, CreateGameResult.class);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        var body = new JoinGameRequest(playerColor, gameID);
        sendRequest("PUT", "/game", body, authToken);
    }

    public void clear() throws Exception {
        sendRequest("DELETE", "/db", null, null);
    }

    private String sendRequest(String method, String path, Object body, String authToken) throws Exception {
        URL url = URI.create(baseUrl + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            conn.setRequestProperty("Authorization", authToken);
        }

        if (body != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream();
                 OutputStreamWriter osw = new OutputStreamWriter(os)) {
                osw.write(gson.toJson(body));
            }
        }

        conn.connect();

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            try (InputStream es  = conn.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(es)) {
                var errorResponse = gson.fromJson(isr, ErrorResponse.class);
                throw new Exception(errorResponse.message());
            }
        }

        try (InputStream is = conn.getInputStream();
             InputStreamReader isr = new InputStreamReader(is)) {
            return new BufferedReader(isr).lines().reduce("", String::concat);
        }
    }

    public String getServerUrl() {
        return baseUrl;
    }
}