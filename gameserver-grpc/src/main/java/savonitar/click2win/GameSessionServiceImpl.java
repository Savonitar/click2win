package savonitar.click2win;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import savonitar.click2win.protobuf.GameSessionServiceGrpc;
import savonitar.click2win.protobuf.PlayerClickedEvent;
import savonitar.click2win.protobuf.ServerGameEvent;

@Slf4j
@GrpcService
public class GameSessionServiceImpl extends GameSessionServiceGrpc.GameSessionServiceImplBase {
    @Override
    public StreamObserver<PlayerClickedEvent> gameSession(StreamObserver<ServerGameEvent> responseObserver) {
        return super.gameSession(responseObserver);
    }
}
