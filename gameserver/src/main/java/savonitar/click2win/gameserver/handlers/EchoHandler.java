package savonitar.click2win.gameserver.handlers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoHandler {
    @GetMapping("/echo")
    public String echo(@RequestParam(value = "echo", defaultValue = "Empty echo") String echo) {
        return echo;
    }
}