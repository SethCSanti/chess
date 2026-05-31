package client;

import com.google.gson.Gson;
import request.*;
import result.*;

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
        // TODO
    }

    public void logout(String authToken) throws Exception {
        // TODO
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        // TODO
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        // TODO
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        // TODO
    }

    public void clear() throws Exception {
        // TODO
    }

    private String sendRequest(String method, String path, Object body, String authToken) throws Exception {
        URL url = new URL(baseUrl + path);
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
                throw new Exception(errorResponse.message);
            }
        }

        try (InputStream is = conn.getInputStream();
             InputStreamReader isr = new InputStreamReader(is)) {
            return new BufferedReader(isr).lines().reduce("", String::concat);
        }
    }
}