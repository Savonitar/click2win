package savonitar.click2win.core;

import savonitar.click2win.core.gameplay.ServerGameEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class GameSessionProcessor {
    private final Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent;
    private final Map<PlayerGameSession, AtomicInteger> sessionToPlayerHits;

    public void processClientEvent(PlayerGameSession session, PlayerClickedEvent playerClickedEvent) {
        increaseScoreIfRequired(session, playerClickedEvent);
    }

    public ServerGameEvent calculateMatchResults(PlayerGameSession session) {
        sessionToLastTargetGameEvent.remove(session);
        int currentScore = sessionToPlayerHits.computeIfAbsent(session, ignored -> new AtomicInteger())
                .get();
        ServerGameEvent newTargetEvent = ServerGameEventFactory.matchCompleted(currentScore);
        sessionToLastTargetGameEvent.put(session, newTargetEvent);
        return newTargetEvent;
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