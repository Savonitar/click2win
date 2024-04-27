package savonitar.click2win;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import savonitar.click2win.protobuf.GameSessionServiceGrpc;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import savonitar.click2win.core.gameplay.EventSequenceGenerator;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GameSessionServiceImpl extends GameSessionServiceGrpc.GameSessionServiceImplBase {
    private final GameSessionProcessor gameSessionProcessor;
    private final EventSequenceGenerator eventSequenceGenerator;

    @Override
    public StreamObserver<PlayerClickedEvent> gameSession(StreamObserver<ServerGameEvent> responseObserver) {
        final String session = UUID.randomUUID().toString();
        log.info("New session started: {}", session);

        final PlayerGameSession playerGameSession = new PlayerGameSession(session);
        Flux<ServerGameEvent> serverGameEventFlux = eventSequenceGenerator.serverEventSequenceGenerator(playerGameSession,
                gameSessionProcessor);

        serverGameEventFlux
                .doOnNext(event -> {
                    log.info("Generated new event={}", event);
                    responseObserver.onNext(event);
                    log.info("Event={} has been sent to client", event);
                })
                .doOnComplete(() -> {
                    log.info("Match completed for session={}", playerGameSession);
                    ServerGameEvent event = gameSessionProcessor.calculateMatchResults(playerGameSession);
                    responseObserver.onNext(event);
                    responseObserver.onCompleted();
                    log.info("Match results={} has been sent to session={}", event, session);
                })
                .subscribe();

        return new PlayerClickedEventObserver(gameSessionProcessor, playerGameSession, responseObserver);
    }
}