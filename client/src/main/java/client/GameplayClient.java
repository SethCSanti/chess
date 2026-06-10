package client;

import chess.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import java.util.Collection;

public class GameplayClient extends ClientBase implements ServerMessageObserver {
    private final String authToken;
    private final String playerColor;
    private ChessGame game;
    private WebSocketCommunicator ws;
    private final int gameID;

    public GameplayClient(ServerFacade server, String authToken, String playerColor, int gameID) throws Exception {
        super(server);
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.game = new ChessGame();

        String serverUrl = server.getServerUrl();
        ws = new WebSocketCommunicator(serverUrl, this);
        ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                game = ((LoadGameMessage) message).getGame();
                drawBoard();
            }
            case NOTIFICATION -> System.out.println("\n" + ((NotificationMessage) message).getMessage());
            case ERROR -> System.out.println("\nError: " + ((ErrorMessage) message).getErrorMessage());
        }
        printPrompt();
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    protected String eval(String input) throws Exception {
        String[] tokens = input.trim().split("\\s+");
        String cmd = tokens[0].toLowerCase();
        return switch (cmd) {
            case "leave" -> leave();
            case "redraw" -> redraw();
            case "move" -> makeMove(tokens);
            case "resign" -> resign();
            case "highlight" -> highlight(tokens);
            case "help" -> help();
            default -> "Unknown command. Type 'help' for options.\n";
        };
    }

    private String leave() throws Exception {
        ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        System.out.println("Left game.");
        return "quit";
    }

    private String redraw() {
        drawBoard();
        return "";
    }

    private String makeMove(String[] tokens) throws Exception {
        if (tokens.length < 3) {
            return "Expected: move <from> <to> [promotion]\n";
        }
        ChessPosition from = parsePosition(tokens[1]);
        ChessPosition to = parsePosition(tokens[2]);
        ChessPiece.PieceType promotion = null;
        if (tokens.length == 4) {
            promotion = parsePromotion(tokens[3]);
        }
        ws.sendCommand(new MakeMoveCommand(authToken, gameID, new ChessMove(from, to, promotion)));
        return "";
    }

    private String resign() throws Exception {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirm = new java.util.Scanner(System.in).nextLine().trim().toLowerCase();

        if (!confirm.equals("yes")) {
            System.out.println("Resignation cancelled.");
            return "";
        }

        ws.sendCommand(new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        ));

        return "";
    }

    private String highlight(String[] tokens) {
        if (tokens.length < 2) {
            return "Expected: highlight <position>\n";
        }
        ChessPosition pos = parsePosition(tokens[1]);
        Collection<ChessMove> validMoves = game.validMoves(pos);
        ui.BoardPrinter.drawWithHighlights(game.getBoard(), playerColor.equals("BLACK"), validMoves);
        return "";
    }

    private void drawBoard() {
        ui.BoardPrinter.draw(game.getBoard(), playerColor.equals("BLACK"));
    }

    private ChessPosition parsePosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }

        pos = pos.toLowerCase();

        char file = pos.charAt(0);
        char rank = pos.charAt(1);

        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Out of bounds position: " + pos);
        }

        int col = file - 'a' + 1;
        int row = Character.getNumericValue(rank);

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotion(String promo) {
        return switch (promo.toLowerCase()) {
            case "queen", "q" -> ChessPiece.PieceType.QUEEN;
            case "rook", "r" -> ChessPiece.PieceType.ROOK;
            case "bishop", "b" -> ChessPiece.PieceType.BISHOP;
            case "knight", "n" -> ChessPiece.PieceType.KNIGHT;
            default -> ChessPiece.PieceType.QUEEN;
        };
    }

    @Override
    protected void printPrompt() {
        System.out.print("\n[IN_GAME] >>> ");
    }

    @Override
    protected String help() {
        return """
                - move <from> <to> [promotion]  (e.g. move e2 e4)
                - highlight <position>           (e.g. highlight e2)
                - redraw
                - resign
                - leave
                - help
                """;
    }
}