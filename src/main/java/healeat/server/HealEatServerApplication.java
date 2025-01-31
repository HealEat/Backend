package healeat.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing	// BaseEntity
@EnableFeignClients	// 외부 API 활용
public class HealEatServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealEatServerApplication.class, args);
	}
}
