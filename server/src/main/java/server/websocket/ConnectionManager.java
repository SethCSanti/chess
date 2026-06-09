package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Session session) {
        for (Set<Session> sessions : connections.values()) {
            sessions.remove(session);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        broadcastHelper(gameID, excludeSession, message);
    }

    public void broadcastToAll(int gameID, ServerMessage message) throws IOException {
        broadcastHelper(gameID, null, message);
    }

    private void broadcastHelper(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        Set<Session> sessions = connections.get(gameID);
        if (sessions == null) { return; }
        String msg = new Gson().toJson(message);
        for (Session session : sessions) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                session.getRemote().sendString(msg);
            }
        }
    }
}