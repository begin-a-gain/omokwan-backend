package begin_a_gain.omokwang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OmokwangApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmokwangApplication.class, args);
    }
}
