package savonitar.click2win.gameserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import savonitar.click2win.database.Player;
import savonitar.click2win.database.PlayerService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @GetMapping("/rating")
    public Integer playerRating(@RequestParam String playerName) {
        log.info("Rating for player: {}", playerName);
        Player player = playerService.getPlayerByName(playerName);
        log.info("Player: {}", player);
        if (player != null) {
            return player.getRating();
        }
        return 0;
    }

    @GetMapping("/top")
    public List<Player> getTopPlayers() {
        return playerService.getTopPlayers();
    }

    @GetMapping("/count")
    public Long countPlayers() {
        return playerService.count();
    }
}