package server;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import model.Response;
import request.LogoutRequest;
import service.UserService;

public class LogoutHandler {

    private final UserService userService;

    public LogoutHandler(DataAccess dataAccess) {
        this.userService = new UserService(dataAccess);
    }

    public void handle(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logoutUser(new LogoutRequest(authToken));
        ctx.status(200);
        ctx.result(JsonUtils.toJson(new Response()));
    }
}