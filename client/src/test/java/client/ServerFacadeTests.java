package client;

import org.junit.jupiter.api.*;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws Exception {
        facade.clear();
    }

    // --- register ---

    @Test
    void registerSuccess() throws Exception {
        RegisterResult result = facade.register("seth", "pass123", "seth@email.com");
        assertNotNull(result.getAuthToken());
        assertEquals("seth", result.getUsername());
    }

    @Test
    void registerDuplicateFails() throws Exception {
        facade.register("seth", "pass123", "seth@email.com");
        assertThrows(Exception.class, () ->
                facade.register("seth", "pass456", "seth2@email.com"));
    }

    // --- login ---

    @Test
    void loginSuccess() throws Exception {
        facade.register("seth", "pass123", "seth@email.com");
        LoginResult result = facade.login("seth", "pass123");
        assertNotNull(result.getAuthToken());
        assertEquals("seth", result.getUsername());
    }

    @Test
    void loginWrongPasswordFails() throws Exception {
        facade.register("seth", "pass123", "seth@email.com");
        assertThrows(Exception.class, () ->
                facade.login("seth", "wrongpass"));
    }

    // --- logout ---

    @Test
    void logoutSuccess() throws Exception {
        RegisterResult result = facade.register("seth", "pass123", "seth@email.com");
        assertDoesNotThrow(() -> facade.logout(result.getAuthToken()));
    }

    @Test
    void logoutInvalidTokenFails() {
        assertThrows(Exception.class, () ->
                facade.logout("not-a-real-token"));
    }

    // --- listGames ---

    @Test
    void listGamesSuccess() throws Exception {
        RegisterResult result = facade.register("seth", "pass123", "seth@email.com");
        facade.createGame(result.getAuthToken(), "testGame");
        result.ListGamesResult listResult = facade.listGames(result.getAuthToken());
        assertNotNull(listResult.getGames());
        assertEquals(1, listResult.getGames().size());
    }

    @Test
    void listGamesUnauthorizedFails() {
        assertThrows(Exception.class, () ->
                facade.listGames("bad-token"));
    }

    // --- createGame ---

    @Test
    void createGameSuccess() throws Exception {
        RegisterResult result = facade.register("seth", "pass123", "seth@email.com");
        result.CreateGameResult gameResult = facade.createGame(result.getAuthToken(), "testGame");
        assertNotNull(gameResult.getGameID());
        assertTrue(gameResult.getGameID() > 0);
    }

    @Test
    void createGameUnauthorizedFails() {
        assertThrows(Exception.class, () ->
                facade.createGame("bad-token", "testGame"));
    }

    // --- joinGame ---

    @Test
    void joinGameSuccess() throws Exception {
        RegisterResult result = facade.register("seth", "pass123", "seth@email.com");
        result.CreateGameResult gameResult = facade.createGame(result.getAuthToken(), "testGame");
        assertDoesNotThrow(() ->
                facade.joinGame(result.getAuthToken(), "WHITE", gameResult.getGameID()));
    }

    @Test
    void joinGameColorTakenFails() throws Exception {
        RegisterResult result = facade.register("seth", "pass123", "seth@email.com");
        result.CreateGameResult gameResult = facade.createGame(result.getAuthToken(), "testGame");
        facade.joinGame(result.getAuthToken(), "WHITE", gameResult.getGameID());
        assertThrows(Exception.class, () ->
                facade.joinGame(result.getAuthToken(), "WHITE", gameResult.getGameID()));
    }
}