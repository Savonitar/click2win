package savonitar.click2win.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import savonitar.click2win.core.gameplay.EventSequenceGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class CommonConfiguration {

    @Bean
    public Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<PlayerGameSession, AtomicInteger> sessionToPlayerHits() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public EventSequenceGenerator eventSequenceGenerator(@Value("${game.session.duration:5000}") int gameSessionDurationMs) {
        return new EventSequenceGenerator(gameSessionDurationMs);
    }

    @Bean
    public GameSessionProcessor gameSessionProcessor(Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent,
                                                     Map<PlayerGameSession, AtomicInteger> sessionToPlayerHits) {
        return new GameSessionProcessor(sessionToLastTargetGameEvent, sessionToPlayerHits);
    }
}