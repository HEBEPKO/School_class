package by.neverko.schoolclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SchoolClassApplication {
   public static void main(String[] args) {
       SpringApplication.run(SchoolClassApplication.class);
   }
}