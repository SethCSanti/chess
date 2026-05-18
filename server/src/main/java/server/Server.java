package server;

import dataaccess.MemoryDataAccess;
import io.javalin.*;

public class Server {

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

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
