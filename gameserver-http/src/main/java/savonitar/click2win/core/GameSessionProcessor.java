package savonitar.click2win.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import savonitar.click2win.core.gameplay.ServerGameEventFactory;
import savonitar.click2win.gameserver.events.PlayerClickedEvent;
import savonitar.click2win.gameserver.events.ServerGameEvent;

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
        int currentScore = sessionToPlayerHits.computeIfAbsent(session, ignored -> new AtomicInteger())
                .get();
        ServerGameEvent newTargetEvent = ServerGameEventFactory.matchCompleted(currentScore);
        sessionToLastTargetGameEvent.put(session, newTargetEvent);
        return newTargetEvent;
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
            sessionToLastTargetGameEvent.remove(session);
        }
    }

    private boolean compareEvents(ServerGameEvent cachedServerEvent, PlayerClickedEvent playerClickedEvent) {
        return cachedServerEvent != null && playerClickedEvent != null
                && cachedServerEvent.getX() == playerClickedEvent.getX()
                && cachedServerEvent.getY() == playerClickedEvent.getY()
                && !cachedServerEvent.isEnd();
    }
}