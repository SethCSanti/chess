package client;

import java.util.Arrays;
import java.util.Scanner;

public class PreloginClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public PreloginClient(ServerFacade server) {
        this.server = server;
    }

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

    private String register(String[] params) throws Exception {
        if  (params.length >= 3) {
            var result = server.register(params[0], params[1], params[2]);
            var authToken = result.getAuthToken();
            var username = result.getUsername();
            var obj = new PostloginClient(server, authToken, username);
            obj.run();
            return "Registered and logged in as " + username + "\n";
            }
         else {
            return "Expected: <username> <password> <email>\n";
        }
    }

    private String login(String[] params) throws Exception {
        if  (params.length >= 2) {
            var result = server.login(params[0], params[1]);
            var authToken = result.getAuthToken();
            var username = result.getUsername();
            var obj = new PostloginClient(server, authToken, username);
            obj.run();
            return "Logged in as " + username + "\n";
        } else {
            return "Expected: <username> <password>\n";
        }
    }

    private void printPrompt() {
        System.out.print("\n[LOGGED_OUT] >>> ");
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