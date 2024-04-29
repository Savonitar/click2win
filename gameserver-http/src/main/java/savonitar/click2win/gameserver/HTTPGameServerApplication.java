package savonitar.click2win.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class HTTPGameServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HTTPGameServerApplication.class, args);
    }
}