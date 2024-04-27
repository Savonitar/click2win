package savonitar.core;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.core.gameplay.ServerGameEventFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class GameSessionProcessor {
    private final Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent;
    private final Map<PlayerGameSession, AtomicInteger> sessionToPlayerHits;

    public void processClientEvent(PlayerGameSession session, byte[] payload) {
        log.info("Received event: {}", payload);
        PlayerClickedEvent playerClickedEvent;
        try {
            playerClickedEvent = PlayerClickedEvent.parseFrom(payload);
            increaseScoreIfRequired(session, playerClickedEvent);
            log.info("PlayerClicked: {}", playerClickedEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error("Incorrect message received from client: {}", session, e);
        }
    }

    public byte[] calculateMatchResults(PlayerGameSession session) {
        sessionToLastTargetGameEvent.remove(session);
        int currentScore = sessionToPlayerHits.computeIfAbsent(session, ignored -> new AtomicInteger())
                .get();
        ServerGameEvent newTargetEvent = ServerGameEventFactory.matchCompleted(currentScore);
        sessionToLastTargetGameEvent.put(session, newTargetEvent);
        return newTargetEvent.toByteArray();
    }

    public byte[] transformEventToResponse(PlayerGameSession session,
                                           ServerGameEvent serverGameEvent) {
        sessionToLastTargetGameEvent.put(session, serverGameEvent);
        return serverGameEvent.toByteArray();
    }

    public ServerGameEvent targetEvent(PlayerGameSession session) {
        int currentScore = sessionToPlayerHits
                .computeIfAbsent(session, __ -> new AtomicInteger())
                .get();
        return ServerGameEventFactory.newTargetEvent(currentScore);
    }

    private void increaseScoreIfRequired(PlayerGameSession session, PlayerClickedEvent playerClickedEvent) {
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