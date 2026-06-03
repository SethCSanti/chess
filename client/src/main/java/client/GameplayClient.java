package client;

import chess.ChessGame;

import java.util.Scanner;

public class GameplayClient {
    private final ServerFacade server;
    private final String authToken;
    private final String playerColor;
    private final Scanner scanner = new Scanner(System.in);
    private chess.ChessGame game;

    public GameplayClient(ServerFacade server, String authToken, String playerColor, ChessGame chessGame) {
        this.server = server;
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.game = chessGame;
    }

    /** Starts the gameplay REPL loop, drawing the board immediately and accepting input until the user leaves. */
    public void run() {
        drawBoard();

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
    }

    /** Parses the input and dispatches to the appropriate command method. */
    private String eval(String input) {
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
    private void printPrompt() {
        System.out.print("\n[IN_GAME] >>> ");
    }

    /** Returns the list of available gameplay commands. */
    private String help() {
        return """
                - redraw
                - leave
                - help
                """;
    }
}