package savonitar.click2win.gameserver.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;
import savonitar.click2win.core.GameSessionProcessor;
import savonitar.click2win.core.PlayerGameSession;
import savonitar.click2win.core.gameplay.EventSequenceGenerator;
import savonitar.click2win.database.PlayerRepository;
import savonitar.click2win.database.PlayerService;
import savonitar.click2win.gameserver.MatchStatus;
import savonitar.click2win.gameserver.events.ServerGameEvent;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class CommonConfiguration {

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String postgresPass = System.getenv("postgres_pass");
        String postgresUser = System.getenv("postgres_user");
        String postgresUrl = System.getenv("postgres_url");
        String postgresPort = System.getenv("postgres_port");
        final String pgConnectionString = "jdbc:postgresql://"
                + postgresUrl
                + ":" + postgresPort
                + "/players";
        log.info("pgConnectionString: {}", pgConnectionString);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(pgConnectionString);
        dataSource.setUsername(postgresUser);
        dataSource.setPassword(postgresPass);
        return dataSource;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public RestTemplate restTemplate(StringHttpMessageConverter stringHttpMessageConverter,
                                     MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        return new RestTemplate(List.of(stringHttpMessageConverter, mappingJackson2HttpMessageConverter));
    }

    @Bean
    public Map<PlayerGameSession, MatchStatus> sessionToMatchStatus() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public PlayerService playerService(PlayerRepository playerRepository) {
        return new PlayerService(playerRepository);
    }

    @Bean
    public Map<PlayerGameSession, AtomicInteger> sessionToPlayerHits() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public GameSessionProcessor gameSessionProcessor(Map<PlayerGameSession, ServerGameEvent> sessionToLastTargetGameEvent,
                                                     Map<PlayerGameSession, AtomicInteger> sessionToPlayerHits) {
        return new GameSessionProcessor(sessionToLastTargetGameEvent, sessionToPlayerHits);
    }

    @Bean
    public EventSequenceGenerator eventSequenceGenerator(int gameSessionDurationMs, int eventGenerationIntervalMs) {
        return new EventSequenceGenerator(gameSessionDurationMs, eventGenerationIntervalMs);
    }

    @Bean
    public int gameSessionDurationMs(@Value("${game.session.duration:30000}") int gameSessionDurationMs) {
        return gameSessionDurationMs;
    }

    @Bean
    public int eventGenerationIntervalMs(@Value("${game.event.generation:1000}") int eventGenerationIntervalMs) {
        return eventGenerationIntervalMs;
    }
}