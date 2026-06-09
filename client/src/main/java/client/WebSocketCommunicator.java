package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageDeserializer;
import com.google.gson.GsonBuilder;

import java.net.URI;

@ClientEndpoint
public class WebSocketCommunicator extends Endpoint {

    private Session session;
    private final ServerMessageObserver observer;

    public WebSocketCommunicator(String url, ServerMessageObserver observer) throws Exception {
        this.observer = observer;
        URI uri = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer())
                        .create();
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                observer.notify(serverMessage);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void sendCommand(UserGameCommand command) throws Exception {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}