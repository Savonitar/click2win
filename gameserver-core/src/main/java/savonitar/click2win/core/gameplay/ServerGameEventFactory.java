package savonitar.click2win.core.gameplay;

import lombok.experimental.UtilityClass;
import savonitar.click2win.protobuf.ServerGameEvent;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class ServerGameEventFactory {

    public static ServerGameEvent newTargetEvent(int score) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return ServerGameEvent.newBuilder()
                .setTargetX(random.nextInt(100))
                .setTargetY(random.nextInt(100))
                .setScore(score)
                .setEnd(false)
                .build();
    }

    public static ServerGameEvent matchCompleted(int score) {
        return ServerGameEvent.newBuilder()
                .setScore(score)
                .setEnd(true)
                .build();
    }
}