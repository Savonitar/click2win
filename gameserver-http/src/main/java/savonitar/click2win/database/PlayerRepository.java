package savonitar.click2win.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByPlayerName(String playerName);

    @Query(value = "SELECT * FROM players ORDER BY rating DESC LIMIT 3", nativeQuery = true)
    List<Player> findTopPlayers();
}