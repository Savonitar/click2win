package savonitar.click2win.gameserver;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import savonitar.click2win.gameserver.handlers.StreamingHandler;

import java.util.HashMap;
import java.util.Map;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @Bean
    public HandlerMapping handlerMapping(StreamingHandler streamingHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/streaming", streamingHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }
}