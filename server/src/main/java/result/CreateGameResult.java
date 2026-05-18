package result;

import server.Response;

public class CreateGameResult extends Response {
    Integer gameID;
    public CreateGameResult(Integer gameID) { this.gameID = gameID; }
    public CreateGameResult(String message) { super(message); }

    public Integer getGameID() {
        return gameID;
    }
}
