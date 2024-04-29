package savonitar.click2win.gameserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import savonitar.click2win.database.Player;
import savonitar.click2win.database.PlayerService;

import java.util.List;

@RestController("/api/player")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @GetMapping("/rating")
    public Integer playerRating(@RequestParam String playerName) {
        Player player = playerService.getPlayerByName(playerName);
        if (player != null) {
            return player.getRating();
        }
        return 0;
    }

    @GetMapping("/top")
    public List<Player> getTopPlayers() {
        return playerService.getTopPlayers();
    }
}