package net.evenh.versionmonitor;

import net.evenh.versionmonitor.config.Constants;
import net.evenh.versionmonitor.config.VersionmonitorConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;


@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({VersionmonitorConfiguration.class})
public class App {
  private static final Logger log = LoggerFactory.getLogger(App.class);

  @Autowired
  private Environment env;

  /**
   * Initializes versionmonitor.
   * <p>
   * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
   * <p>
   * You can find more information on how profiles work with JHipster
   * on <a href="http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
   */
  @PostConstruct
  public void initApplication() {
    if (env.getActiveProfiles().length == 0) {
      log.warn("No Spring profile configured, running with default configuration");
    } else {
      log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
      Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
      if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
        log.error("You have misconfigured your application! " +
          "It should not run with both the 'dev' and 'prod' profiles at the same time.");
      }
    }
  }

  /**
   * Main method, used to run the application.
   *
   * @param args the command line arguments
   * @throws UnknownHostException if the local host name could not be resolved into an address
   */
  public static void main(String[] args) throws UnknownHostException {
    SpringApplication app = new SpringApplication(App.class);
    SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
    addDefaultProfile(app, source);
    app.run(args).getEnvironment();

    log.info("Ready for action!");
  }

  /**
   * If no profile has been configured, set by default the "dev" profile.
   */
  private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
    if (!source.containsProperty("spring.profiles.active") &&
      !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {

      app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
    }
  }
}
