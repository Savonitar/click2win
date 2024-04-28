package savonitar.click2win.gameserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import savonitar.click2win.core.gameplay.EventSequenceGenerator;
import savonitar.click2win.gameserver.MatchStatus;
import savonitar.click2win.gameserver.protobuf.PlayerClickedEvent;
import savonitar.click2win.gameserver.protobuf.ServerGameEvent;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/gamesession")
public class GameSessionController {

    @Autowired
    private GameSessionProcessor gameSessionProcessor;

    @Autowired
    private Map<PlayerGameSession, MatchStatus> sessionToMatchStatus;

    @Autowired
    private Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent;

    @Autowired
    private EventSequenceGenerator eventSequenceGenerator;

    @PostMapping(value = "/start")
    public String startSession() {
        final PlayerGameSession playerGameSession = new PlayerGameSession(UUID.randomUUID().toString());
        log.info("Start new session: {}", playerGameSession);
        sessionToMatchStatus.put(playerGameSession, MatchStatus.IN_PROGRESS);
        eventSequenceGenerator.serverEventSequenceGenerator(playerGameSession, gameSessionProcessor)
                .doOnNext(serverGameEvent -> sessionToLastTargetGameEvent.put(playerGameSession, serverGameEvent))
                .doOnComplete(() -> {
                    log.info("Session completed for: {}", playerGameSession);
                    sessionToMatchStatus.put(playerGameSession, MatchStatus.COMPLETED);
                })
                .subscribe();
        return playerGameSession.sessionId();
    }

    @GetMapping(value = "/next", produces = {"application/x-protobuf"})
    public ServerGameEvent nextTarget(@RequestParam String session) {
        log.info("NextTarget required for: {}", session);
        final PlayerGameSession playerGameSession = new PlayerGameSession(session);
        final MatchStatus matchStatus = sessionToMatchStatus.get(playerGameSession);
        if (matchStatus == null) {
            ServerGameEvent lastGameEvent = sessionToLastTargetGameEvent.get(playerGameSession);
            log.info("Game Completed, return last event: {}, for: {}", lastGameEvent, session);
            return ServerGameEvent.newBuilder()
                    .setScore(lastGameEvent != null ? lastGameEvent.getScore() : 0)
                    .setEnd(true)
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

    @PostMapping(value = "/click", consumes = {"application/x-protobuf"})
    public void playerClick(@RequestBody PlayerClickedEvent playerClickedEvent, @RequestParam String session) {
        log.info("PlayerClicked: {}, session: {}", playerClickedEvent, session);
        PlayerGameSession playerGameSession = new PlayerGameSession(session);
        gameSessionProcessor.processClientEvent(playerGameSession, playerClickedEvent);
    }
}