package savonitar.click2win.protobuf;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: api.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GameSessionServiceGrpc {

  private GameSessionServiceGrpc() {}

  public static final String SERVICE_NAME = "GameSessionService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<PlayerClickedEvent,
      ServerGameEvent> getGameSessionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GameSession",
      requestType = PlayerClickedEvent.class,
      responseType = ServerGameEvent.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<PlayerClickedEvent,
      ServerGameEvent> getGameSessionMethod() {
    io.grpc.MethodDescriptor<PlayerClickedEvent, ServerGameEvent> getGameSessionMethod;
    if ((getGameSessionMethod = GameSessionServiceGrpc.getGameSessionMethod) == null) {
      synchronized (GameSessionServiceGrpc.class) {
        if ((getGameSessionMethod = GameSessionServiceGrpc.getGameSessionMethod) == null) {
          GameSessionServiceGrpc.getGameSessionMethod = getGameSessionMethod =
              io.grpc.MethodDescriptor.<PlayerClickedEvent, ServerGameEvent>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GameSession"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  PlayerClickedEvent.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ServerGameEvent.getDefaultInstance()))
              .setSchemaDescriptor(new GameSessionServiceMethodDescriptorSupplier("GameSession"))
              .build();
        }
      }
    }
    return getGameSessionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GameSessionServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GameSessionServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GameSessionServiceStub>() {
        @Override
        public GameSessionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GameSessionServiceStub(channel, callOptions);
        }
      };
    return GameSessionServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GameSessionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GameSessionServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GameSessionServiceBlockingStub>() {
        @Override
        public GameSessionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GameSessionServiceBlockingStub(channel, callOptions);
        }
      };
    return GameSessionServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GameSessionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GameSessionServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GameSessionServiceFutureStub>() {
        @Override
        public GameSessionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GameSessionServiceFutureStub(channel, callOptions);
        }
      };
    return GameSessionServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<PlayerClickedEvent> gameSession(
        io.grpc.stub.StreamObserver<ServerGameEvent> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getGameSessionMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GameSessionService.
   */
  public static abstract class GameSessionServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return GameSessionServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GameSessionService.
   */
  public static final class GameSessionServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GameSessionServiceStub> {
    private GameSessionServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected GameSessionServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GameSessionServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<PlayerClickedEvent> gameSession(
        io.grpc.stub.StreamObserver<ServerGameEvent> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getGameSessionMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GameSessionService.
   */
  public static final class GameSessionServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GameSessionServiceBlockingStub> {
    private GameSessionServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected GameSessionServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GameSessionServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GameSessionService.
   */
  public static final class GameSessionServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GameSessionServiceFutureStub> {
    private GameSessionServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected GameSessionServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GameSessionServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_GAME_SESSION = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GAME_SESSION:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.gameSession(
              (io.grpc.stub.StreamObserver<ServerGameEvent>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGameSessionMethod(),
          io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
            new MethodHandlers<
              PlayerClickedEvent,
              ServerGameEvent>(
                service, METHODID_GAME_SESSION)))
        .build();
  }

  private static abstract class GameSessionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GameSessionServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return GameServiceProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GameSessionService");
    }
  }

  private static final class GameSessionServiceFileDescriptorSupplier
      extends GameSessionServiceBaseDescriptorSupplier {
    GameSessionServiceFileDescriptorSupplier() {}
  }

  private static final class GameSessionServiceMethodDescriptorSupplier
      extends GameSessionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GameSessionServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GameSessionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GameSessionServiceFileDescriptorSupplier())
              .addMethod(getGameSessionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
