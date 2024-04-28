package savonitar.click2win.gameserver.handlers;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.core.gameplay.EventSequenceGenerator;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;

@Slf4j
@Component
public class StreamingHandler implements WebSocketHandler {
    @Autowired
    private EventSequenceGenerator eventSequenceGenerator;

    @Autowired
    private GameSessionProcessor gameSessionProcessor;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("New session started: {}", session);
        final PlayerGameSession playerGameSession = new PlayerGameSession(session.getId());
        Flux<ServerGameEvent> serverGameEventFlux = eventSequenceGenerator.serverEventSequenceGenerator(playerGameSession,
                gameSessionProcessor);

        session.receive()
                .doOnNext(payload -> {
                    log.info("Received event: {}", payload);
                    final byte[] payloadBytes = new byte[payload.getPayload().readableByteCount()];
                    payload.getPayload().read(payloadBytes);
                    PlayerClickedEvent playerClickedEvent;
                    try {
                        playerClickedEvent = PlayerClickedEvent.parseFrom(payloadBytes);
                        gameSessionProcessor.processClientEvent(playerGameSession, playerClickedEvent);
                        log.info("PlayerClicked: {}", playerClickedEvent);
                    } catch (InvalidProtocolBufferException e) {
                        log.error("Incorrect message received from client: {}", session, e);
                    }
                })
                .subscribe();

        return session
                .send(serverGameEventFlux.map(event -> {
                    final byte[] gameEvent = gameSessionProcessor.transformEventToResponse(playerGameSession, event);
                    return session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(gameEvent));
                }))
                .then(session.send(Mono.fromSupplier(() -> {
                    final byte[] matchResults = gameSessionProcessor.calculateMatchResults(playerGameSession).toByteArray();
                    return session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(matchResults));
                })))
                .then(session.close())
                .then(Mono.empty());
    }
}