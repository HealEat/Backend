package healeat.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
public class HealEatServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealEatServerApplication.class, args);
	}
}
