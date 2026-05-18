package server;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import request.LoginRequest;
import service.UserService;

public class LoginHandler {

    private final UserService userService;

    public LoginHandler(DataAccess dataAccess) {
        this.userService = new UserService(dataAccess);
    }

    public void handle(Context ctx) throws Exception {
        LoginRequest request = JsonUtils.fromJson(ctx.body(), LoginRequest.class);
        var result = userService.loginUser(request);
        ctx.status(200);
        ctx.result(JsonUtils.toJson(result));
    }
}