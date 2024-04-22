package savonitar.click2win.gameserver;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StreamingHandlerTest {

    @Test
    public void client_subscribes_and_gets_proper_number_of_events() {
        // given
        WebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = URI.create("ws://localhost:8080/gamesession");
        List<ServerGameEvent> serverGameEvents = new ArrayList<>();

        // when
        client.execute(uri, webSocketSession -> webSocketSession
                .send(Flux.just())
                .and(webSocketSession.receive()
                        .doOnNext(payload -> {
                                    final byte[] payloadBytes = new byte[payload.getPayload().readableByteCount()];
                                    payload.getPayload().read(payloadBytes);
                                    ServerGameEvent serverGameEvent;
                                    try {
                                        serverGameEvent = ServerGameEvent.parseFrom(payloadBytes);
                                        serverGameEvents.add(serverGameEvent);
                                        log.info("ServerGameEvent: {}", serverGameEvent);
                                    } catch (InvalidProtocolBufferException e) {
                                        throw new RuntimeException("Incorrect message format", e);
                                    }
                                }
                        ))
                .then()
        ).block(Duration.ofSeconds(60));

        // then
        Assert.assertEquals(serverGameEvents.size(), 5);
    }
}