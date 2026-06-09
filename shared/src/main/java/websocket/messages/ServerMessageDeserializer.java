package websocket.messages;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {
    @Override
    public ServerMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("serverMessageType").getAsString();

        return switch (type) {
            case "LOAD_GAME" -> context.deserialize(jsonObject, LoadGameMessage.class);
            case "ERROR" -> context.deserialize(jsonObject, ErrorMessage.class);
            case "NOTIFICATION" -> context.deserialize(jsonObject, NotificationMessage.class);
            default -> throw new JsonParseException("Unknown server message type: " + type);
        };
    }
}