package client;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public GameplayClient(ServerFacade server, String authToken, String color) {
        this.server = server;
    }

    public void run() {
        System.out.println("Welcome to Chess! Type 'help' to get started.");
        System.out.print(help());

        var result = "";

        // call board drawing method once written
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
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "leave" -> leave(params);
            case "redraw" -> redraw(params);
            case "help" -> help();
            default -> "Unknown command. Type 'help' for options.\n";
        };
    }

    private String leave(String[] params) {
        // TODO
    }

    private String redraw(String[] params) {
        // TODO
    }

    private void printPrompt() {
        System.out.print("\n[IN_GAME] >>> ");
    }

    private String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                - help
                """;
    }
}