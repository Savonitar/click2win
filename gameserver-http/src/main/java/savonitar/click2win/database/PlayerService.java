package savonitar.click2win.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}