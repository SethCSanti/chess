package server;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import request.CreateGameRequest;
import service.GameService;

public class CreateGameHandler {

    private final GameService gameService;

    public CreateGameHandler(DataAccess dataAccess) {
        this.gameService = new GameService(dataAccess);
    }

    public void handle(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        CreateGameRequest request = JsonUtils.fromJson(ctx.body(), CreateGameRequest.class);
        var result = gameService.createGame(authToken, request);
        ctx.status(200);
        ctx.result(JsonUtils.toJson(result));
    }
}