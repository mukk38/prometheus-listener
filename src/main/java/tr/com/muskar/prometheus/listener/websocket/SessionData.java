package tr.com.muskar.prometheus.listener.websocket;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Data
public class SessionData {

    private final WebSocketSession session;
    private final Lock sessionLock;
    private final static int TRY_LOCK_TIMEOUT_FOR_SEND = 5;

    public SessionData(WebSocketSession session){
        this.session = session;
        this.sessionLock = new ReentrantLock();
    }

    public void sendMessage(String message) {
        try {
            while (true) {
                if (sessionLock.tryLock(TRY_LOCK_TIMEOUT_FOR_SEND, TimeUnit.MILLISECONDS)) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } finally {
                        sessionLock.unlock();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("SessionData - sendMessage - ", e);
            throw new RuntimeException(e);
        }
    }
}
