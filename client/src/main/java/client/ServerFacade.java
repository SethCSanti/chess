package client;

import com.google.gson.Gson;
import request.*;
import result.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String baseUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        // TODO
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
        // TODO
        // 1. open connection to baseUrl + path
        // 2. set method, headers (Content-Type, Authorization if not null)
        // 3. if body not null, write gson.toJson(body) to output stream
        // 4. check status code — if not 200, read error stream and throw exception
        // 5. read and return response body as String
    }
}