package tr.com.muskar.prometheus.listener.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final WebsocketService websocketService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("connection established");
        websocketService.newUserConnected(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            websocketService.removeSession(session);
        } catch (Exception ex) {
            log.error("SocketHandler - afterConnectionClosed - ", ex);
            throw new RuntimeException(ex);
        }
    }

}
