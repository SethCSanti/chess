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

    private String eval(String input) {
        String[] tokens = input.toLowerCase().trim().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        return switch (cmd) {
            case "leave" -> "quit";
            case "redraw" -> redraw();
            case "help" -> help();
            default -> "Unknown command. Type 'help' for options.\n";
        };
    }

    private String redraw() {
        drawBoard();
        return "";
    }

    private void drawBoard() {
        ui.BoardPrinter.draw(game.getBoard(), playerColor.equals("BLACK"));
    }

    private void printPrompt() {
        System.out.print("\n[IN_GAME] >>> ");
    }

    private String help() {
        return """
                - redraw
                - leave
                - help
                """;
    }
}