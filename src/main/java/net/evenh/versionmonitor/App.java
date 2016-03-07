package net.evenh.versionmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);

  /**
   * Runs the Spring Boot application.
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    logger.info("Version monitor running - ready for work");
  }
}
