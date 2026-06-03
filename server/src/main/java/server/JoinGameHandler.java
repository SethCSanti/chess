package server;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import model.Response;
import request.JoinGameRequest;
import service.GameService;

public class JoinGameHandler {

    private final GameService gameService;

    public JoinGameHandler(DataAccess dataAccess) {
        this.gameService = new GameService(dataAccess);
    }

    public void handle(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        JoinGameRequest request = JsonUtils.fromJson(ctx.body(), JoinGameRequest.class);
        gameService.joinGame(authToken, request);
        ctx.status(200);
        ctx.result(JsonUtils.toJson(new Response()));
    }
}