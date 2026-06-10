package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final DataAccess dataAccess;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private String toChess(chess.ChessPosition pos) {
        char file = (char) ('a' + pos.getColumn() - 1);
        int rank = pos.getRow();
        return "" + file + rank;
    }

    private GameData markGameOver(GameData gameData) {
        return new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game(),
                true
        );
    }

    private void sendMessage(Session session, ServerMessage message) {
        try {
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendError(Session session, Exception ex) {
        String msg = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? "Invalid request"
                : ex.getMessage();

        sendMessage(session,
                new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg));
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connections.remove(ctx.session);
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        Session session = ctx.session;

        try {
            UserGameCommand base = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            int gameID = base.getGameID();
            String username = getUsername(base.getAuthToken());

            connections.add(gameID, session);

            switch (base.getCommandType()) {
                case CONNECT -> connect(session, username, base);
                case MAKE_MOVE -> makeMove(session, username,
                        new Gson().fromJson(ctx.message(), MakeMoveCommand.class));
                case LEAVE -> leaveGame(session, username, base);
                case RESIGN -> resign(session, username, base);
            }

        } catch (dataaccess.UnauthorizedException ex) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unauthorized"));
        } catch (Exception ex) {
            sendError(session, ex);
        }
    }

    private String getUsername(String authToken)
            throws dataaccess.UnauthorizedException, dataaccess.DataAccessException {

        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new dataaccess.UnauthorizedException("Invalid auth token");
        }
        return auth.username();
    }

    private void connect(Session session, String username, UserGameCommand command) throws Exception {
        GameData game = dataAccess.getGame(command.getGameID());

        if (game == null) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game does not exist"));
            return;
        }

        sendMessage(session,
                new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game()));

        String notification;
        if (username.equals(game.whiteUsername())) {
            notification = username + " joined as WHITE";
        } else if (username.equals(game.blackUsername())) {
            notification = username + " joined as BLACK";
        } else {
            notification = username + " is observing";
        }

        connections.broadcast(command.getGameID(), session,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification));
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        GameData gameData = dataAccess.getGame(command.getGameID());

        if (gameData == null) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game not found"));
            return;
        }

        if (gameData.gameOver()) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is already over"));
            return;
        }

        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if (!isWhite && !isBlack) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Observers cannot make moves"));
            return;
        }

        chess.ChessGame.TeamColor playerColor =
                isWhite ? chess.ChessGame.TeamColor.WHITE : chess.ChessGame.TeamColor.BLACK;

        if (gameData.game().getTeamTurn() != playerColor) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "It is not your turn"));
            return;
        }

        try {
            gameData.game().makeMove(command.getMove());
        } catch (chess.InvalidMoveException ex) {
            String msg = (ex.getMessage() == null || ex.getMessage().isBlank())
                    ? "Invalid move"
                    : ex.getMessage();

            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg));
            return;
        }

        // FIXED: properly persist updated game state
        dataAccess.updateGame(gameData);

        connections.broadcastToAll(command.getGameID(),
                new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game()));

        var move = command.getMove();

        String moveMsg = username + " moved from " +
                toChess(move.getStartPosition()) +
                " to " +
                toChess(move.getEndPosition());

        connections.broadcast(command.getGameID(), session,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMsg));

        // CHECK / CHECKMATE / STALEMATE (FIXED USERNAME OUTPUT)
        for (chess.ChessGame.TeamColor color : chess.ChessGame.TeamColor.values()) {

            String playerName = (color == chess.ChessGame.TeamColor.WHITE)
                    ? gameData.whiteUsername()
                    : gameData.blackUsername();

            if (gameData.game().isInCheckmate(color)) {
                connections.broadcastToAll(command.getGameID(),
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                playerName + " is in checkmate!"));

                dataAccess.updateGame(markGameOver(gameData));
                return;
            }

            if (gameData.game().isInStalemate(color)) {
                connections.broadcastToAll(command.getGameID(),
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                "Stalemate!"));

                dataAccess.updateGame(markGameOver(gameData));
                return;
            }

            if (gameData.game().isInCheck(color)) {
                connections.broadcastToAll(command.getGameID(),
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                playerName + " is in check!"));
            }
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws Exception {
        GameData gameData = dataAccess.getGame(command.getGameID());

        if (username.equals(gameData.whiteUsername())) {
            GameData updated = new GameData(
                    gameData.gameID(),
                    null,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game(),
                    gameData.gameOver()
            );
            dataAccess.updateGame(updated);

        } else if (username.equals(gameData.blackUsername())) {
            GameData updated = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game(),
                    gameData.gameOver()
            );
            dataAccess.updateGame(updated);
        }

        connections.remove(session);

        connections.broadcast(command.getGameID(), session,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        username + " left the game"));
    }

    private void resign(Session session, String username, UserGameCommand command) throws Exception {
        GameData gameData = dataAccess.getGame(command.getGameID());

        boolean isPlayer =
                username.equals(gameData.whiteUsername()) ||
                        username.equals(gameData.blackUsername());

        if (!isPlayer) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Observers cannot resign"));
            return;
        }

        if (gameData.gameOver()) {
            sendMessage(session,
                    new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Game is already over"));
            return;
        }

        GameData updated = markGameOver(gameData);
        dataAccess.updateGame(updated);

        connections.broadcastToAll(command.getGameID(),
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        username + " resigned"));
    }
}