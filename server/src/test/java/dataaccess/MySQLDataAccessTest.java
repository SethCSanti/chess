package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLDataAccessTest {

    private MySQLDataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    // CLEAR
    @Test
    void clearSuccess() {
        assertDoesNotThrow(() -> dataAccess.clear());
    }

    // CREATE USER
    @Test
    void createUserSuccess() throws DataAccessException {
        assertDoesNotThrow(() ->
                dataAccess.createUser(new UserData("alice", "pass", "alice@email.com")));
    }

    @Test
    void createUserDuplicateFails() throws DataAccessException {
        dataAccess.createUser(new UserData("alice", "pass", "alice@email.com"));
        assertThrows(DataAccessException.class, () ->
                dataAccess.createUser(new UserData("alice", "pass2", "alice2@email.com")));
    }

    // GET USER
    @Test
    void getUserSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("alice", "pass", "alice@email.com"));
        UserData result = dataAccess.getUser("alice");
        assertNotNull(result);
        assertEquals("alice", result.username());
    }

    @Test
    void getUserNotFound() throws DataAccessException {
        UserData result = dataAccess.getUser("nobody");
        assertNull(result);
    }

    // CREATE AUTH
    @Test
    void createAuthSuccess() throws DataAccessException {
        assertDoesNotThrow(() ->
                dataAccess.createAuth(new AuthData("token123", "alice")));
    }

    @Test
    void createAuthDuplicateFails() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        assertThrows(DataAccessException.class, () ->
                dataAccess.createAuth(new AuthData("token123", "bob")));
    }

    // GET AUTH
    @Test
    void getAuthSuccess() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        AuthData result = dataAccess.getAuth("token123");
        assertNotNull(result);
        assertEquals("alice", result.username());
    }

    @Test
    void getAuthNotFound() throws DataAccessException {
        AuthData result = dataAccess.getAuth("badtoken");
        assertNull(result);
    }

    // DELETE AUTH
    @Test
    void deleteAuthSuccess() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        dataAccess.deleteAuth("token123");
        assertNull(dataAccess.getAuth("token123"));
    }

    @Test
    void deleteAuthNonExistentDoesNotThrow() {
        assertDoesNotThrow(() -> dataAccess.deleteAuth("doesnotexist"));
    }

    // CREATE GAME
    @Test
    void createGameSuccess() throws DataAccessException {
        int id = dataAccess.createGame(new GameData(0, null, null, "testGame", null));
        assertTrue(id > 0);
    }

    @Test
    void createGameNullNameFails() {
        assertThrows(DataAccessException.class, () ->
                dataAccess.createGame(new GameData(0, null, null, null, null)));
    }

    // GET GAME
    @Test
    void getGameSuccess() throws DataAccessException {
        int id = dataAccess.createGame(new GameData(0, null, null, "testGame", null));
        GameData result = dataAccess.getGame(id);
        assertNotNull(result);
        assertEquals("testGame", result.gameName());
    }

    @Test
    void getGameNotFound() throws DataAccessException {
        GameData result = dataAccess.getGame(99999);
        assertNull(result);
    }

    // LIST GAMES
    @Test
    void listGamesSuccess() throws DataAccessException {
        dataAccess.createGame(new GameData(0, null, null, "game1", null));
        dataAccess.createGame(new GameData(0, null, null, "game2", null));
        var games = dataAccess.listGames();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        var games = dataAccess.listGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    // UPDATE GAME
    @Test
    void updateGameSuccess() throws DataAccessException {
        int id = dataAccess.createGame(new GameData(0, null, null, "testGame", null));
        GameData updated = new GameData(id, "alice", null, "testGame", null);
        assertDoesNotThrow(() -> dataAccess.updateGame(updated));
        assertEquals("alice", dataAccess.getGame(id).whiteUsername());
    }

    @Test
    void updateGameNonExistentDoesNotThrow() {
        assertDoesNotThrow(() ->
                dataAccess.updateGame(new GameData(99999, "alice", null, "testGame", null)));
    }
}