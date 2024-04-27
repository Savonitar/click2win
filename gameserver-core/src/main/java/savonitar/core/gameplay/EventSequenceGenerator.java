package savonitar.core.gameplay;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.core.GameSessionProcessor;
import savonitar.core.PlayerGameSession;

import java.time.Duration;

@RequiredArgsConstructor
public class EventSequenceGenerator {
    private final int gameSessionDurationMs;

    public Flux<ServerGameEvent> serverEventSequenceGenerator(PlayerGameSession session,
                                                              GameSessionProcessor gameSessionProcessor) {
        return Flux.interval(Duration.ofMillis(1000))
                .map(i -> gameSessionProcessor.targetEvent(session))
                .take(Duration.ofMillis(gameSessionDurationMs));
    }
}