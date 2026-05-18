package server;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import request.RegisterRequest;
import service.UserService;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(DataAccess dataAccess) {
        this.userService = new UserService(dataAccess);
    }

    public void handle(Context ctx) throws Exception {
        RegisterRequest request = JsonUtils.fromJson(ctx.body(), RegisterRequest.class);
        var result = userService.registerUser(request);
        ctx.status(200);
        ctx.result(JsonUtils.toJson(result));
    }
}