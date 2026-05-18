package result;

import model.GameData;
import server.Response;

import java.util.List;

public class ListGamesResult extends Response {
    List<GameData> games;
    public ListGamesResult(List<GameData> games) { this.games = games; }
    public ListGamesResult(String message) { super(message); }

    public List<GameData> getGames() {
        return games;
    }
}
