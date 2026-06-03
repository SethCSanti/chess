package client;

import java.util.Arrays;
import java.util.Scanner;

public abstract class ClientBase {
    protected final ServerFacade server;
    protected final Scanner scanner = new Scanner(System.in);

    public ClientBase(ServerFacade server) {
        this.server = server;
    }

    /** Starts the REPL loop, accepting input until quit is returned. */
    public void run() {
        System.out.print(help());
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /** Parses the input and dispatches to the appropriate command method. */
    protected abstract String eval(String input) throws Exception;

    /** Prints the prompt for the current state. */
    protected abstract void printPrompt();

    /** Returns the list of available commands for the current state. */
    protected abstract String help();
}