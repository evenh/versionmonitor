package net.evenh.versionmonitor;

import net.evenh.versionmonitor.models.Project;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.services.GitHubService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);

  @Bean
  public GitHubService gitHubService() {
    return GitHubService.getInstance();
  }

  /**
   * Runs the Spring Boot application.
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);

    logger.info("Version monitor running - ready for work");

    // Testing
    Project swift = new GitHubProject("spring-projects/spring-boot");
    logger.info("swift-info: {}", swift);
  }
}
