package savonitar.click2win;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import savonitar.click2win.protobuf.GameSessionServiceGrpc;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GameSessionServiceTest {
    @Test
    public void client_subscribes_and_gets_proper_number_of_events() throws InterruptedException {
        // given
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        GameSessionServiceGrpc.GameSessionServiceStub stub = GameSessionServiceGrpc.newStub(channel);
        CountDownLatch onCompleted = new CountDownLatch(1);
        AtomicInteger onError = new AtomicInteger(0);
        AtomicInteger receivedServerEvents = new AtomicInteger(0);
        StreamObserver<PlayerClickedEvent> requestObserver = stub.gameSession(new StreamObserver<>() {
            @Override
            public void onNext(ServerGameEvent event) {
                assertNotNull(event);
                receivedServerEvents.incrementAndGet();
                log.info("Received event: {}", event);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error occurred: ", throwable);
                onError.incrementAndGet();
            }

            @Override
            public void onCompleted() {
                log.info("onCompleted called");
                onCompleted.countDown();
            }
        });

        // when
        requestObserver.onNext(PlayerClickedEvent.newBuilder().setX(10).setY(20).build());
        requestObserver.onNext(PlayerClickedEvent.newBuilder().setX(30).setY(40).build());
        requestObserver.onNext(PlayerClickedEvent.newBuilder().setX(50).setY(60).build());

        // then
        boolean sessionCompleted = onCompleted.await(30, TimeUnit.SECONDS);
        assertEquals(0, onError.get());
        assertEquals(5, receivedServerEvents.get());
        assertTrue(sessionCompleted);
    }
}