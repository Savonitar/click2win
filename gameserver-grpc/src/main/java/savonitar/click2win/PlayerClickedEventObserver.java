package savonitar.click2win;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;

@Slf4j
@RequiredArgsConstructor
public class PlayerClickedEventObserver implements StreamObserver<PlayerClickedEvent> {

    private final GameSessionProcessor gameSessionProcessor;
    private final PlayerGameSession playerGameSession;
    private final StreamObserver<ServerGameEvent> responseObserver;

    @Override
    public void onNext(PlayerClickedEvent playerClickedEvent) {
        gameSessionProcessor.processClientEvent(playerGameSession, playerClickedEvent);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error during the client interaction", t);
    }

    @Override
    public void onCompleted() {
        responseObserver.onCompleted();
    }
}