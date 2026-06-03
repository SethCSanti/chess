package client;

import chess.ChessGame;

import java.util.Arrays;
import java.util.Scanner;

public class PostloginClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private final String authToken;
    private final String username;
    private java.util.List<model.GameData> gameList = new java.util.ArrayList<>();

    public PostloginClient(ServerFacade server, String authToken, String username) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
    }

    /** Starts the postlogin REPL loop, accepting input until the user logs out. */
    public void run() {
        System.out.println("Welcome to Chess! Type 'help' to get started.");
        System.out.print(help());

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
    private String eval(String input) throws Exception {
        String[] tokens = input.toLowerCase().trim().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "logout" -> logout();
            case "list" -> list();
            case "help" -> help();
            case "create" -> create(params);
            case "join" -> join(params);
            case "observe" -> observe(params);
            default -> "Unknown command. Type 'help' for options.\n";
        };
    }

    /** Logs out the current user and returns to the prelogin REPL. */
    private String logout() throws Exception {
        server.logout(authToken);
        System.out.println("Logged out successfully.");
        return "quit";
    }

    /** Retrieves and displays all available games from the server. */
    private String list() throws Exception {
        var result = server.listGames(authToken);
        gameList = result.getGames();
        if (gameList.isEmpty()) {
            return "No games available.\n";
        }
        var sb = new StringBuilder();
        for (int i = 0; i < gameList.size(); i++) {
            var game = gameList.get(i);
            sb.append(String.format("%d. %s | White: %s | Black: %s%n",
                    i + 1,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "open",
                    game.blackUsername() != null ? game.blackUsername() : "open"));
        }
        return sb.toString();
    }

    /** Creates a new chess game on the server with the given name. */
    private String create(String[] params) throws Exception {
        if (params.length >= 1) {
            server.createGame(authToken, params[0]);
            return "Game '" + params[0] + "' created! Type 'list' to see all games.\n";
        }
        return "Expected: <game name>\n";
    }

    /** Joins an existing game as the specified color and transitions to the gameplay REPL. */
    private String join(String[] params) throws Exception {
        if (params.length >= 2) {
            int index;
            try {
                index = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                return "Game number must be a valid integer.\n";
            }
            if (gameList.isEmpty()) {
                return "Please type 'list' first to see available games.\n";
            }
            if (index < 0 || index >= gameList.size()) {
                return "Invalid game number.\n";
            }
            String color = params[1].toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                return "Color must be WHITE or BLACK.\n";
            }
            int gameID = gameList.get(index).gameID();
            server.joinGame(authToken, color, gameID);
            new GameplayClient(server, authToken, color, new ChessGame()).run();
            return "";
        }
        return "Expected: <game number> <WHITE|BLACK>\n";
    }

    /** Observes an existing game from the white perspective and transitions to the gameplay REPL. */
    private String observe(String[] params) throws Exception {
        if (params.length >= 1) {
            int index;
            try {
                index = Integer.parseInt(params[0]) - 1;
            } catch (NumberFormatException e) {
                return "Game number must be a valid integer.\n";
            }
            if (gameList.isEmpty()) {
                return "Please type 'list' first to see available games.\n";
            }
            if (index < 0 || index >= gameList.size()) {
                return "Invalid game number.\n";
            }
            new GameplayClient(server, authToken, "OBSERVER", new ChessGame()).run();
            return "";
        }
        return "Expected: <game number>\n";
    }

    /** Prints the postlogin prompt showing the current username. */
    private void printPrompt() {
        System.out.print("\n[" + username + "] >>> ");
    }

    /** Returns the list of available postlogin commands. */
    private String help() {
        return """
                - list
                - create <game name>
                - join <game number> <WHITE|BLACK>
                - observe <game number>
                - logout
                - help
                """;
    }
}