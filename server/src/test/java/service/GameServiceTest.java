package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private String authToken;

    @BeforeEach
    public void setUp() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        UserService userService = new UserService(dataAccess);
        RegisterResult result = userService.registerUser(new RegisterRequest("seth", "pass123", "seth@email.com"));
        authToken = result.getAuthToken();
    }

    @Test
    public void createGameSuccess() throws Exception {
        CreateGameResult result = gameService.createGame(authToken, new CreateGameRequest("testGame"));
        assertNotNull(result.getGameID());
        assertTrue(result.getGameID() > 0);
    }

    @Test
    public void createGameUnauthorized() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.createGame("badtoken", new CreateGameRequest("testGame")));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        gameService.createGame(authToken, new CreateGameRequest("testGame"));
        ListGamesResult result = gameService.listGames(authToken);
        assertNotNull(result.getGames());
        assertEquals(1, result.getGames().size());
    }

    @Test
    public void listGamesUnauthorized() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.listGames("badtoken"));
    }

    @Test
    public void joinGameSuccess() throws Exception {
        CreateGameResult created = gameService.createGame(authToken, new CreateGameRequest("testGame"));
        assertDoesNotThrow(() ->
                gameService.joinGame(authToken, new JoinGameRequest("WHITE", created.getGameID())));
    }

    @Test
    public void joinGameColorAlreadyTaken() throws Exception {
        CreateGameResult created = gameService.createGame(authToken, new CreateGameRequest("testGame"));
        gameService.joinGame(authToken, new JoinGameRequest("WHITE", created.getGameID()));
        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame(authToken, new JoinGameRequest("WHITE", created.getGameID())));
    }

    @Test
    public void joinGameBadGameID() {
        assertThrows(BadRequestException.class, () ->
                gameService.joinGame(authToken, new JoinGameRequest("WHITE", null)));
    }

    @Test
    public void joinGameBadColor() {
        assertThrows(BadRequestException.class, () ->
                gameService.joinGame(authToken, new JoinGameRequest("GREEN", 1)));
    }
}