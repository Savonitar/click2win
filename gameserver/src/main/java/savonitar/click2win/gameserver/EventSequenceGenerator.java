package savonitar.click2win.gameserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import savonitar.click2win.gameserver.gameplay.ServerGameEventFactory;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EventSequenceGenerator {
    private final int gameSessionDurationMs;

    public EventSequenceGenerator(int gameSessionDurationMs) {
        this.gameSessionDurationMs = gameSessionDurationMs;
    }

    public Flux<ServerGameEvent> serverEventSequenceGenerator(WebSocketSession session,
                                                              Map<WebSocketSession, AtomicInteger> sessionToPlayerHits) {
        return Flux.interval(Duration.ofMillis(1000))
                .map(i -> {
                    int currentScore = sessionToPlayerHits
                            .computeIfAbsent(session, __ -> new AtomicInteger())
                            .get();
                    return ServerGameEventFactory.newTargetEvent(currentScore);
                })
                .take(Duration.ofMillis(gameSessionDurationMs));
    }
}