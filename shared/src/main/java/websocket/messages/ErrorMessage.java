package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;
    public ErrorMessage(ServerMessageType type, String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
