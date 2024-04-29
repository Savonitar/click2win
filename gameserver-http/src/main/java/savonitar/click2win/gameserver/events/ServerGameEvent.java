package savonitar.click2win.gameserver.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServerGameEvent {
    int x;
    int y;
    boolean end;
    int score;
}