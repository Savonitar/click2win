package savonitar.click2win;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import savonitar.click2win.gameserver.HTTPGameServerApplication;
import savonitar.click2win.gameserver.events.ServerGameEvent;
import savonitar.click2win.gameserver.events.PlayerClickedEvent;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = HTTPGameServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameSessionServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private int gameSessionDurationMs;

    @Test
    public void client_subscribes_and_gets_proper_number_of_events() throws InterruptedException {
        // given
        AtomicInteger eventsFromServer = new AtomicInteger(0);
        AtomicInteger hits = new AtomicInteger(0);
        AtomicReference<ServerGameEvent> lastEvent = new AtomicReference<>();
        CountDownLatch sessionLatch = new CountDownLatch(1);

        // when
        String session = startSession();
        log.info("after start session");
        Flux.interval(Duration.ofMillis(500))
                .map((Function<Long, Object>) aLong -> {
                    log.info("tick tick");
                    ServerGameEvent serverGameEvent = serverGameEvent(session);
                    log.info("Received ServerGameEvent: {}", serverGameEvent);
                    if (serverGameEvent != null) {
                        if (!serverGameEvent.isEnd()) {
                            if (!compareEvents(lastEvent.get(), serverGameEvent)) {
                                eventsFromServer.incrementAndGet();
                            }
                            if (hits.get() < 2) {
                                click(session, serverGameEvent.getX(), serverGameEvent.getY());
                                hits.incrementAndGet();
                            }
                        } else {
                            click(session, Integer.MAX_VALUE, Integer.MAX_VALUE);
                        }
                        lastEvent.set(serverGameEvent);
                    }
                    log.info("end tick");
                    return "";
                })
                .take(Duration.ofMillis(gameSessionDurationMs + 5000))
                .doOnComplete(sessionLatch::countDown)
                .subscribe();
        sessionLatch.await(gameSessionDurationMs + 5000, TimeUnit.MILLISECONDS);

        assertEquals(gameSessionDurationMs / 1000, eventsFromServer.get());
        assertEquals(2, hits.get());
        assertEquals(hits.get(), lastEvent.get().getScore());
        assertTrue(lastEvent.get().isEnd());
    }

    private String startSession() {
        ResponseEntity<String> serverGameEvent = restTemplate.postForEntity(getUrlStartSession(), HttpEntity.EMPTY, String.class);
        log.info("Start session={}", serverGameEvent.getBody());
        return serverGameEvent.getBody();
    }

    private ServerGameEvent serverGameEvent(String session) {
        ResponseEntity<ServerGameEvent> serverGameEvent = restTemplate.getForEntity(getUrlNext(session), ServerGameEvent.class);
        log.info("Next ServerGameEvent={}", serverGameEvent.getBody());
        return serverGameEvent.getBody();
    }

    private PlayerClickedEvent click(String session, int x, int y) {
        PlayerClickedEvent build = PlayerClickedEvent.builder()
                .x(x)
                .y(y)
                .build();
        ResponseEntity<PlayerClickedEvent> serverGameEvent = restTemplate.postForEntity(getUrlClick(session), build, PlayerClickedEvent.class);
        log.info("Click ServerGameEvent={}", serverGameEvent.getBody());
        return serverGameEvent.getBody();
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/gamesession/";
    }

    private String getUrlStartSession() {
        return getBaseUrl() + "start";
    }

    private String getUrlNext(String session) {
        return getBaseUrl() + "next?session=" + session;
//        return getBaseUrl() + "next";
    }

    private String getUrlClick(String session) {
        return getBaseUrl() + "click?session=" + session;
    }

    private boolean compareEvents(ServerGameEvent newTargetGameEvent, ServerGameEvent previousTargetEvent) {
        return newTargetGameEvent != null && previousTargetEvent != null
                && newTargetGameEvent.getX() == previousTargetEvent.getX()
                && newTargetGameEvent.getY() == previousTargetEvent.getY();
    }
}