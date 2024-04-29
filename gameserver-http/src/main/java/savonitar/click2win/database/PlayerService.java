package savonitar.click2win.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getPlayerByName(String playerName) {
        return playerRepository.findByPlayerName(playerName);
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> getTopPlayers() {
        return playerRepository.findTopPlayers();
    }
}