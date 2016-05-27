package net.evenh.versionmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class App implements EnvironmentAware {
  private static final Logger log = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    log.info("Ready for action!");
  }

  @Override
  public void setEnvironment(Environment environment) {
    if (environment.getActiveProfiles().length == 0) {
      throw new RuntimeException("A valid Spring profile must be supplied!");
    }
  }
}
