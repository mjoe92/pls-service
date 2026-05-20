package de.vw.paso.pls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableReactiveMongoRepositories(basePackages = "de.vw.paso.pls.repository")
public class PlsApplication {

  public static void main(final String[] args) {
    SpringApplication.run(PlsApplication.class, args);
  }
}
