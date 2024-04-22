package savonitar.click2win.gameserver;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.util.Map;

@Component
public class EventToResponseTransformer {

    private final Map<WebSocketSession, ServerGameEvent> sessionToLastTargetGameEvent;

    public EventToResponseTransformer(Map<WebSocketSession, ServerGameEvent> sessionToLastTargetGameEvent) {
        this.sessionToLastTargetGameEvent = sessionToLastTargetGameEvent;
    }

    public WebSocketMessage transformEventToResponse(WebSocketSession session,
                                                     ServerGameEvent serverGameEvent) {
        sessionToLastTargetGameEvent.put(session, serverGameEvent);
        byte[] eventBytes = serverGameEvent.toByteArray();
        return session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(eventBytes));
    }
}