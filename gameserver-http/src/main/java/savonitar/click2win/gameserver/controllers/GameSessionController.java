package savonitar.click2win.gameserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import savonitar.click2win.core.gameplay.EventSequenceGenerator;
import savonitar.click2win.database.Player;
import savonitar.click2win.database.PlayerService;
import savonitar.click2win.gameserver.MatchStatus;
import savonitar.click2win.gameserver.events.PlayerClickedEvent;
import savonitar.click2win.gameserver.events.ServerGameEvent;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/gamesession")
public class GameSessionController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameSessionProcessor gameSessionProcessor;

    @Autowired
    private Map<PlayerGameSession, MatchStatus> sessionToMatchStatus;

    @Autowired
    private Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent;

    @Autowired
    private EventSequenceGenerator eventSequenceGenerator;

    @PostMapping("/start")
    public String startSession(@RequestParam String player) {
        final PlayerGameSession playerGameSession = new PlayerGameSession(UUID.randomUUID().toString());
        String playerName = player == null ? "default" : player;
        log.info("Start new session: {}", playerGameSession);
        sessionToMatchStatus.put(playerGameSession, MatchStatus.IN_PROGRESS);
        eventSequenceGenerator.serverEventSequenceGenerator(playerGameSession, gameSessionProcessor)
                .doOnNext(serverGameEvent -> {
                    sessionToLastTargetGameEvent.put(playerGameSession, serverGameEvent);
                    log.info("New event placed: {}, for: {}", serverGameEvent, playerGameSession);
                })
                .doOnComplete(() -> {
                    log.info("Session completed for: {}", playerGameSession);
                    try {
                        Player newPlayer = new Player();
                        ServerGameEvent matchResults = gameSessionProcessor.calculateMatchResults(playerGameSession);
                        newPlayer.setPlayerName(playerName);
                        newPlayer.setRating(matchResults.getScore());
                        playerService.savePlayer(newPlayer);
                    } catch (Exception ex) {
                        log.error("Can't save player", ex);
                    }
                    sessionToMatchStatus.put(playerGameSession, MatchStatus.COMPLETED);
                })
                .subscribe();
        return playerGameSession.sessionId();
    }

    @GetMapping("/next")
    public ServerGameEvent nextTarget(@RequestParam String session) {
        log.info("NextTarget required for: {}", session);
        final PlayerGameSession playerGameSession = new PlayerGameSession(session);
        final MatchStatus matchStatus = sessionToMatchStatus.get(playerGameSession);
        if (matchStatus == null) {
            ServerGameEvent lastGameEvent = sessionToLastTargetGameEvent.get(playerGameSession);
            log.info("Game Completed, return last event: {}, for: {}", lastGameEvent, session);
            return ServerGameEvent.builder()
                    .score(lastGameEvent != null ? lastGameEvent.getScore() : 0)
                    .end(true)
                    .build();
        }
        if (matchStatus == MatchStatus.IN_PROGRESS) {
            ServerGameEvent serverGameEvent = sessionToLastTargetGameEvent.get(playerGameSession);
            log.info("Return serverGameEvent: {}, for: {}", serverGameEvent, session);
            return serverGameEvent;
        } else {
            ServerGameEvent matchResults = gameSessionProcessor.calculateMatchResults(playerGameSession);
            sessionToMatchStatus.remove(playerGameSession);
            log.info("Return matchResults: {}, for: {}", matchResults, session);
            return matchResults;
        }
    }

    @PostMapping("/click")
    public void playerClick(@RequestBody PlayerClickedEvent playerClickedEvent, @RequestParam String session) {
        log.info("PlayerClicked: {}, session: {}", playerClickedEvent, session);
        PlayerGameSession playerGameSession = new PlayerGameSession(session);
        gameSessionProcessor.processClientEvent(playerGameSession, playerClickedEvent);
    }
}