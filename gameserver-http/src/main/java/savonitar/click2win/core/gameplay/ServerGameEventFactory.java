package savonitar.click2win.core.gameplay;

import lombok.experimental.UtilityClass;
import savonitar.click2win.gameserver.events.ServerGameEvent;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class ServerGameEventFactory {

    public static ServerGameEvent newTargetEvent(int score) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return ServerGameEvent.builder()
                .x(random.nextInt(420))
                .y(random.nextInt(420))
                .score(score)
                .end(false)
                .build();
    }

    public static ServerGameEvent matchCompleted(int score) {
        return ServerGameEvent.builder()
                .score(score)
                .end(true)
                .build();
    }
}