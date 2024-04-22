package savonitar.click2win.gameserver.handlers;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import savonitar.click2win.gameserver.EventSequenceGenerator;
import savonitar.click2win.gameserver.EventToResponseTransformer;
import savonitar.click2win.gameserver.gameplay.ServerGameEventFactory;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class StreamingHandler implements WebSocketHandler {
    @Autowired
    private Map<WebSocketSession, ServerGameEvent> sessionToLastTargetGameEvent;
    @Autowired
    private Map<WebSocketSession, AtomicInteger> sessionToPlayerHits;
    @Autowired
    private EventSequenceGenerator eventSequenceGenerator;
    @Autowired
    private EventToResponseTransformer eventToResponseTransformer;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("New session started: {}", session);

        Flux<ServerGameEvent> serverGameEventFlux = eventSequenceGenerator.serverEventSequenceGenerator(session,
                sessionToPlayerHits);

        session.receive()
                .doOnNext(payload -> {
                    log.info("Received event: {}", payload);
                    final byte[] payloadBytes = new byte[payload.getPayload().readableByteCount()];
                    payload.getPayload().read(payloadBytes);
                    PlayerClickedEvent playerClickedEvent;
                    try {
                        playerClickedEvent = PlayerClickedEvent.parseFrom(payloadBytes);
                        increaseScoreIfRequired(session, playerClickedEvent);
                        log.info("PlayerClicked: {}", playerClickedEvent);
                    } catch (InvalidProtocolBufferException e) {
                        log.error("Incorrect message received from client: {}", session, e);
                    }
                })
                .subscribe();

        return session
                .send(serverGameEventFlux.map(event -> eventToResponseTransformer.transformEventToResponse(session, event)))
                .then(session.send(Mono.fromSupplier(() -> sendMatchResults(session))))
                .then(session.close())
                .then(Mono.empty());
    }

    private WebSocketMessage sendMatchResults(WebSocketSession session) {
        sessionToLastTargetGameEvent.remove(session);
        int currentScore = sessionToPlayerHits.computeIfAbsent(session, ignored -> new AtomicInteger())
                .get();
        ServerGameEvent newTargetEvent = ServerGameEventFactory.matchCompleted(currentScore);
        sessionToLastTargetGameEvent.put(session, newTargetEvent);
        byte[] eventBytes = newTargetEvent.toByteArray();
        return session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(eventBytes));
    }

    private void increaseScoreIfRequired(WebSocketSession session, PlayerClickedEvent playerClickedEvent) {
        ServerGameEvent cachedEvent = sessionToLastTargetGameEvent.get(session);
        if (compareEvents(cachedEvent, playerClickedEvent)) {
            sessionToPlayerHits.computeIfAbsent(session, ignored -> new AtomicInteger())
                    .incrementAndGet();
        }
    }

    private boolean compareEvents(ServerGameEvent newTargetGameEvent, PlayerClickedEvent playerClickedEvent) {
        return newTargetGameEvent != null && playerClickedEvent != null
                && newTargetGameEvent.getTargetX() == playerClickedEvent.getX()
                && newTargetGameEvent.getTargetY() == playerClickedEvent.getY();
    }
}