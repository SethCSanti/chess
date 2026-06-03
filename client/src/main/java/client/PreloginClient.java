package client;

import java.util.Arrays;
import java.util.Scanner;

public class PreloginClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public PreloginClient(ServerFacade server) {
        this.server = server;
    }

    /** Starts the prelogin REPL loop, accepting input until the user quits. */
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
            case "register" -> register(params);
            case "login" -> login(params);
            case "help" -> help();
            case "quit" -> "quit";
            default -> "Unknown command. Type 'help' for options.\n";
        };
    }

    /** Registers a new user and transitions to the postlogin REPL on success. */
    private String register(String[] params) throws Exception {
        if (params.length >= 3) {
            var result = server.register(params[0], params[1], params[2]);
            new PostloginClient(server, result.getAuthToken(), result.getUsername()).run();
            return "";
        }
        return "Expected: <username> <password> <email>\n";
    }

    /** Logs in an existing user and transitions to the postlogin REPL on success. */
    private String login(String[] params) throws Exception {
        if (params.length >= 2) {
            var result = server.login(params[0], params[1]);
            new PostloginClient(server, result.getAuthToken(), result.getUsername()).run();
            return "";
        }
        return "Expected: <username> <password>\n";
    }

    /** Prints the prelogin prompt indicating the user is logged out. */
    private void printPrompt() {
        System.out.print("\n[LOGGED_OUT] >>> ");
    }

    /** Returns the list of available prelogin commands. */
    private String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                - help
                """;
    }
}