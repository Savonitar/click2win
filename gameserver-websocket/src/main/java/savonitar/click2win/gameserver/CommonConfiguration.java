package savonitar.click2win.gameserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import savonitar.click2win.gameserver.handlers.StreamingHandler;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.core.GameSessionProcessor;
import savonitar.core.PlayerGameSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
class CommonConfiguration {

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

    @Bean
    public StreamingHandler streamingHandler() {
        return new StreamingHandler();
    }

    @Bean
    public HandlerMapping handlerMapping(StreamingHandler streamingHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/gamesession", streamingHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }
}