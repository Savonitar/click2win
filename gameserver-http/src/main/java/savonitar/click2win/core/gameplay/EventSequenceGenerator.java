package savonitar.click2win.core.gameplay;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import savonitar.click2win.gameserver.events.ServerGameEvent;

import java.time.Duration;

@RequiredArgsConstructor
public class EventSequenceGenerator {
    private final int gameSessionDurationMs;
    private final int eventGenerationIntervalMs;

    public Flux<ServerGameEvent> serverEventSequenceGenerator(PlayerGameSession session,
                                                              GameSessionProcessor gameSessionProcessor) {
        return Flux.interval(Duration.ofMillis(eventGenerationIntervalMs))
                .map(i -> gameSessionProcessor.targetEvent(session))
                .take(Duration.ofMillis(gameSessionDurationMs + eventGenerationIntervalMs));
    }
}