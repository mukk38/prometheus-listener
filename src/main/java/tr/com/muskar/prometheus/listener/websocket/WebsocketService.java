package tr.com.muskar.prometheus.listener.websocket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tr.com.muskar.prometheus.listener.model.response.AlertRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class WebsocketService {

    protected final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public void removeSession(WebSocketSession session) {
        try {
            sessions.remove(session.getId());
            session.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void newUserConnected(WebSocketSession session) {
        sessions.put(session.getId(),new SessionData(session));
    }
    public void sendPrometheusAlertToUser(AlertRule message) {
        sessions.values().forEach(sessionData -> {
            try {
                sessionData.sendMessage(objectMapper.writeValueAsString(message));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
