package savonitar.click2win;

import io.grpc.stub.StreamObserver;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;

public class PlayerClickedEventObserver implements StreamObserver<PlayerClickedEvent> {

    private final StreamObserver<ServerGameEvent> responseObserver;

    public PlayerClickedEventObserver(StreamObserver<ServerGameEvent> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(PlayerClickedEvent playerClickedEvent) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {

    }
}
