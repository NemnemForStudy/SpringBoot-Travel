package travel.travel_Spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TravelSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelSpringApplication.class, args);
	}

}
