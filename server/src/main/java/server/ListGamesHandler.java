package server;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import service.GameService;

public class ListGamesHandler {

    private final GameService gameService;

    public ListGamesHandler(DataAccess dataAccess) {
        this.gameService = new GameService(dataAccess);
    }

    public void handle(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        var result = gameService.listGames(authToken);
        ctx.status(200);
        ctx.result(JsonUtils.toJson(result));
    }
}