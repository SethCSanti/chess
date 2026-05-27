package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import io.javalin.*;

public class Server {
    {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        // store handlers as fields
        ClearHandler clearHandler = new ClearHandler(dataAccess);
        RegisterHandler registerHandler = new RegisterHandler(dataAccess);
        LoginHandler loginHandler = new LoginHandler(dataAccess);
        LogoutHandler logoutHandler = new LogoutHandler(dataAccess);
        ListGamesHandler listGamesHandler = new ListGamesHandler(dataAccess);
        CreateGameHandler createGameHandler = new CreateGameHandler(dataAccess);
        JoinGameHandler joinGameHandler = new JoinGameHandler(dataAccess);

        // register routes
        javalin.delete("/db", clearHandler::handle);
        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        javalin.get("/game", listGamesHandler::handle);
        javalin.post("/game", createGameHandler::handle);
        javalin.put("/game", joinGameHandler::handle);

        javalin.exception(dataaccess.UnauthorizedException.class, (ex, ctx) -> {
            ctx.status(401);
            ctx.result(JsonUtils.toJson(new Response("Error: " + ex.getMessage())));
        });
        javalin.exception(dataaccess.BadRequestException.class, (ex, ctx) -> {
            ctx.status(400);
            ctx.result(JsonUtils.toJson(new Response("Error: " + ex.getMessage())));
        });
        javalin.exception(dataaccess.AlreadyTakenException.class, (ex, ctx) -> {
            ctx.status(403);
            ctx.result(JsonUtils.toJson(new Response("Error: " + ex.getMessage())));
        });
        javalin.exception(dataaccess.DataAccessException.class, (ex, ctx) -> {
            ctx.status(500);
            ctx.result(JsonUtils.toJson(new Response("Error: " + ex.getMessage())));
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
