package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final DataAccess dataAccess;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
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
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            int gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            connections.add(gameID, session);
            switch (command.getCommandType()) {
                case CONNECT   -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, new Gson().fromJson(ctx.message(), MakeMoveCommand.class));
                case LEAVE     -> leaveGame(session, username, command);
                case RESIGN    -> resign(session, username, command);
            }
        } catch (dataaccess.UnauthorizedException ex) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unauthorized"));
        } catch (Exception ex) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + ex.getMessage()));
        }
    }

    private String getUsername(String authToken) throws dataaccess.UnauthorizedException, dataaccess.DataAccessException {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) { throw new dataaccess.UnauthorizedException("Invalid auth token"); }
        return auth.username();
    }

    private void sendMessage(Session session, websocket.messages.ServerMessage message) {
        try {
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws Exception {
        GameData game = dataAccess.getGame(command.getGameID());
        if (game == null) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game does not exist"));
            return;
        }
        sendMessage(session, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game()));

        String notification;
        if (username.equals(game.whiteUsername())) {
            notification = username + " joined the game";
        } else if (username.equals(game.blackUsername())) {
            notification = username + " joined the game";
        } else {
            notification = username + " is observing";
        }
        connections.broadcast(command.getGameID(), session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification));
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        GameData gameData = dataAccess.getGame(command.getGameID());
        if (gameData == null) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game not found"));
            return;
        }
        if (gameData.gameOver()) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game is already over"));
            return;
        }
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());
        if (!isWhite && !isBlack) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: observers cannot make moves"));
            return;
        }

        try {
            gameData.game().makeMove(command.getMove());
        } catch (Exception ex) {
            sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + ex.getMessage()));
            return;
        }

        dataAccess.updateGame(gameData);
        connections.broadcastToAll(command.getGameID(), new LoadGameMessage(ServerMessage.ServerMessageType.ERROR, gameData.game()));

        String moveNotification = username + " made a move";
        connections.broadcast(command.getGameID(), session, new NotificationMessage(ServerMessage.ServerMessageType.ERROR, moveNotification));

        if (gameData.game().isInCheckmate(chess.ChessGame.TeamColor.WHITE)) {
            connections.broadcastToAll(command.getGameID(), new NotificationMessage(ServerMessage.ServerMessageType.ERROR, "White is in checkmate!"));
            dataAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), gameData.game(), true));
        } else if (gameData.game().isInCheck(chess.ChessGame.TeamColor.WHITE)) {
            connections.broadcastToAll(command.getGameID(), new NotificationMessage(ServerMessage.ServerMessageType.ERROR, "White is in check!"));
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws Exception {
        // TODO
    }

    private void resign(Session session, String username, UserGameCommand command) throws Exception {
        // TODO
    }
}