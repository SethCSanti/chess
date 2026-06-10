package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    /** Clears all users, auth tokens, and games from memory. */
    @Override
    public void clear() throws DataAccessException {
        users.clear();
        auths.clear();
        games.clear();
    }

    /** Stores a new user in memory. */
    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    /** Returns the user with the given username, or null if not found. */
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    /** Creates a new game with a unique ID and stores it in memory. */
    @Override
    public int createGame(GameData game) throws DataAccessException {
        int id = nextGameID++;
        games.put(id, new GameData(id, null, null, game.gameName(), new ChessGame(), false));
        return id;
    }

    /** Returns the game with the given ID, or null if not found. */
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    /** Returns a list of all games currently in memory. */
    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    /** Overwrites an existing game with updated data. */
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    /** Stores a new auth token in memory. */
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    /** Returns the auth data for the given token, or null if not found. */
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    /** Deletes the auth token from memory, logging the user out. */
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}
