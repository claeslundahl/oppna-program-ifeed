package se.vgregion.ifeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IfeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfeedApplication.class, args);
    }

}
