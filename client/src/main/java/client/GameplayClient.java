package client;

import chess.ChessGame;

public class GameplayClient extends ClientBase {
    private final String authToken;
    private final String playerColor;
    private chess.ChessGame game;

    public GameplayClient(ServerFacade server, String authToken, String playerColor, ChessGame chessGame) {
        super(server);
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.game = chessGame;
    }

    /** Draws the board immediately then starts the gameplay REPL loop until the user leaves. */
    @Override
    public void run() {
        drawBoard();
        super.run();
    }

    /** Parses the input and dispatches to the appropriate command method. */
    @Override
    protected String eval(String input) {
        String[] tokens = input.toLowerCase().trim().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        return switch (cmd) {
            case "leave" -> {
                System.out.println("Left game.");
                yield "quit";
            }
            case "redraw" -> redraw();
            case "help" -> help();
            default -> "Unknown command. Type 'help' for options.\n";
        };
    }

    /** Redraws the current board state to the console. */
    private String redraw() {
        drawBoard();
        return "";
    }

    /** Draws the board from the correct perspective based on the player's color. */
    private void drawBoard() {
        ui.BoardPrinter.draw(game.getBoard(), playerColor.equals("BLACK"));
    }

    /** Prints the gameplay prompt indicating the user is in a game. */
    @Override
    protected void printPrompt() {
        System.out.print("\n[IN_GAME] >>> ");
    }

    /** Returns the list of available gameplay commands. */
    @Override
    protected String help() {
        return """
                - redraw
                - leave
                - help
                """;
    }
}