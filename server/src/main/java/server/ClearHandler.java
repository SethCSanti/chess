package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(DataAccess dataAccess) {
        this.clearService = new ClearService(dataAccess);
    }

    public void handle(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.result(JsonUtils.toJson(new Response()));
    }
}