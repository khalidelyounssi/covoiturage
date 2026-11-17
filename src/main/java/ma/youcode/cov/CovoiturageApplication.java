package ma.youcode.cov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.Clock;
import java.time.ZoneId;

@SpringBootApplication
public class CovoiturageApplication {
	public static void main(String[] args) {
		SpringApplication.run(CovoiturageApplication.class, args);
	}

	@Bean
	Clock applicationClock() {
		return Clock.system(ZoneId.of("Africa/Casablanca"));
	}
}