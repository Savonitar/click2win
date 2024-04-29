package savonitar.click2win.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("savonitar.click2win.*")
@EnableJpaRepositories("savonitar.click2win.database")
public class HTTPGameServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HTTPGameServerApplication.class, args);
    }
}