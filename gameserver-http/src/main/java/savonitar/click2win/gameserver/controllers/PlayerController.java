package savonitar.click2win.gameserver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController("/api/player")
public class PlayerController {

    @GetMapping("rating")
    public Integer playerRating(@RequestParam String playerName) {
        Random random = new Random();
        return random.nextInt(100);
    }
}