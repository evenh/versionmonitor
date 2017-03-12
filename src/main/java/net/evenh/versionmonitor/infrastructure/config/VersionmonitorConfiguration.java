package net.evenh.versionmonitor.infrastructure.config;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Properties specific to Versionmonitor.
 *
 * <p> Properties are configured in the application.properties file. </p>
 */
@Component
@ConfigurationProperties(prefix = "versionmonitor", ignoreInvalidFields = true)
public class VersionmonitorConfiguration {
  private final Async async = new Async();
  private final Http http = new Http();
  private final Cache cache = new Cache();
  private final CorsConfiguration cors = new CorsConfiguration();
  private final Slack slack = new Slack();
  private final Github github = new Github();
  private final Jobchecker jobchecker = new Jobchecker();

  public Async getAsync() {
    return async;
  }

  public Http getHttp() {
    return http;
  }

  public Cache getCache() {
    return cache;
  }

  public CorsConfiguration getCors() {
    return cors;
  }

  public Slack getSlack() {
    return slack;
  }

  public Github getGithub() {
    return github;
  }

  public Jobchecker getJobchecker() {
    return jobchecker;
  }

  public static class Async {

    private int corePoolSize = 2;

    private int maxPoolSize = 50;

    private int queueCapacity = 10000;

    public int getCorePoolSize() {
      return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
      return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
    }
  }

  public static class Http {

    private final Cache cache = new Cache();

    public Cache getCache() {
      return cache;
    }

    public static class Cache {

      private int timeToLiveInDays = 1461;

      private int cachesize = 10;

      public int getTimeToLiveInDays() {
        return timeToLiveInDays;
      }

      public void setTimeToLiveInDays(int timeToLiveInDays) {
        this.timeToLiveInDays = timeToLiveInDays;
      }

      public int getCachesize() {
        return cachesize;
      }

      public void setCachesize(int cachesize) {
        this.cachesize = cachesize;
      }
    }
  }

  public static class Cache {

    private int timeToLiveSeconds = 3600;

    public int getTimeToLiveSeconds() {
      return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
      this.timeToLiveSeconds = timeToLiveSeconds;
    }
  }

  public static class Slack {
    private String botname = "VersionMonitor";

    public String getBotname() {
      return botname;
    }

    public void setBotname(String botname) {
      this.botname = botname;
    }
  }

  public static class Github {
    private Integer ratelimitBuffer = 20;
    @NotNull
    private String oauthToken;

    public Integer getRatelimitBuffer() {
      return ratelimitBuffer;
    }

    public void setRatelimitBuffer(Integer ratelimitBuffer) {
      this.ratelimitBuffer = ratelimitBuffer;
    }

    public String getOauthToken() {
      return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
      this.oauthToken = oauthToken;
    }
  }

  public static class Jobchecker {
    private String cron;

    public String getCron() {
      return cron;
    }

    public void setCron(String cron) {
      this.cron = cron;
    }
  }
}
