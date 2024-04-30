package savonitar.click2win.database;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Long count() {
        return playerRepository.count();
    }

    @Transactional
    public Player updateOrCreatePlayer(String playerName, Integer rating) {
        Player existingPlayer = playerRepository.findByPlayerName(playerName);

        if (existingPlayer != null) {
            // Player exists, update the rating
            existingPlayer.setRating(existingPlayer.getRating() + rating);
            return playerRepository.save(existingPlayer);
        } else {
            // Player does not exist, create a new player
            Player newPlayer = new Player();
            newPlayer.setPlayerName(playerName);
            newPlayer.setRating(rating);
            return playerRepository.save(newPlayer);
        }
    }
}