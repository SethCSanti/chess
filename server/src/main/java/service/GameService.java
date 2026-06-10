package service;

import dataaccess.DataAccess;
import model.*;
import dataaccess.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.List;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /** Creates a new chess game and returns the generated game ID. */
    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws BadRequestException, DataAccessException, UnauthorizedException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("User does not exist");
        } else if (request.gameName() == null) {
            throw new BadRequestException("Game name is required");
        } else {
            int gameID = dataAccess.createGame(new GameData(0, null, null, request.gameName(), null, false));
            return new CreateGameResult(gameID);
        }
    }

    /** Returns a list of all current chess games. */
    public ListGamesResult listGames(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("User does not exist");
        } else {
            List<GameData> games = dataAccess.listGames();
            return new ListGamesResult(games);
        }
    }

    /** Joins an existing chess game as the specified color. */
    public void joinGame(String authToken, JoinGameRequest request)
            throws BadRequestException, DataAccessException, UnauthorizedException, AlreadyTakenException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (request.playerColor() == null || (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK"))) {
            throw new BadRequestException("Invalid player color");
        }
        if (request.gameID() == null) {
            throw new BadRequestException("Game ID is required");
        }
        GameData game = dataAccess.getGame(request.gameID());
        GameData updatedGame = getGameData(request, game, auth);
        dataAccess.updateGame(updatedGame);
    }

    @NotNull
    private static GameData getGameData(JoinGameRequest request, GameData game, AuthData auth)
            throws BadRequestException, AlreadyTakenException {
        if (game == null) {
            throw new BadRequestException("Game not found");
        }
        if (request.playerColor().equals("WHITE") && game.whiteUsername() != null) {
            throw new AlreadyTakenException("White is already taken");
        }
        if (request.playerColor().equals("BLACK") && game.blackUsername() != null) {
            throw new AlreadyTakenException("Black is already taken");
        }
        String username = auth.username();
        GameData updatedGame;
        if (request.playerColor().equals("WHITE")) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game(), game.gameOver());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game(), game.gameOver());
        }
        return updatedGame;
    }
}
