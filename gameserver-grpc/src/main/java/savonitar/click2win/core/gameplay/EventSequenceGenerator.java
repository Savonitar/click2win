package savonitar.click2win.core.gameplay;

import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import savonitar.click2win.protobuf.ServerGameEvent;

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