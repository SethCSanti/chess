package client;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayClient {
    private final ServerFacade server;
    private final String authToken;
    private final String playerColor;
    private final Scanner scanner = new Scanner(System.in);

    public GameplayClient(ServerFacade server, String authToken, String playerColor) {
        this.server = server;
        this.authToken = authToken;
        this.playerColor = playerColor;
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
        // TODO - Step 6
        // if playerColor.equals("BLACK") → draw from black's perspective
        // otherwise (WHITE or OBSERVER) → draw from white's perspective
        System.out.println("[Board will be drawn here]");
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