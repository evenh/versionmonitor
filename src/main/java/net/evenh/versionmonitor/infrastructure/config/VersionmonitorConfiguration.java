package net.evenh.versionmonitor.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;

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
  private final Mail mail = new Mail();
  private final Security security = new Security();
  private final Metrics metrics = new Metrics();
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

  public Mail getMail() {
    return mail;
  }

  public Security getSecurity() {
    return security;
  }

  public Metrics getMetrics() {
    return metrics;
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

      public int getTimeToLiveInDays() {
        return timeToLiveInDays;
      }

      public void setTimeToLiveInDays(int timeToLiveInDays) {
        this.timeToLiveInDays = timeToLiveInDays;
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

  public static class Mail {

    private String from = "versionmonitor@localhost";

    public String getFrom() {
      return from;
    }

    public void setFrom(String from) {
      this.from = from;
    }
  }

  public static class Security {

    private final Authentication authentication = new Authentication();

    public Authentication getAuthentication() {
      return authentication;
    }

    public static class Authentication {

      private final Jwt jwt = new Jwt();

      public Jwt getJwt() {
        return jwt;
      }

      public static class Jwt {

        private String secret;

        private long tokenValidityInSeconds = 1800;
        private long tokenValidityInSecondsForRememberMe = 2592000;

        public String getSecret() {
          return secret;
        }

        public void setSecret(String secret) {
          this.secret = secret;
        }

        public long getTokenValidityInSeconds() {
          return tokenValidityInSeconds;
        }

        public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
          this.tokenValidityInSeconds = tokenValidityInSeconds;
        }

        public long getTokenValidityInSecondsForRememberMe() {
          return tokenValidityInSecondsForRememberMe;
        }

        public void setTokenValidityInSecondsForRememberMe(long tokenValidityInSecondsForRememberMe) {
          this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
        }
      }
    }
  }

  public static class Metrics {

    private final Jmx jmx = new Jmx();

    private final Spark spark = new Spark();

    private final Graphite graphite = new Graphite();

    private final Logs logs = new Logs();

    public Jmx getJmx() {
      return jmx;
    }

    public Spark getSpark() {
      return spark;
    }

    public Graphite getGraphite() {
      return graphite;
    }

    public Logs getLogs() {
      return logs;
    }


    public static class Jmx {

      private boolean enabled = true;

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }
    }

    public static class Spark {

      private boolean enabled = false;

      private String host = "localhost";

      private int port = 9999;

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }

      public String getHost() {
        return host;
      }

      public void setHost(String host) {
        this.host = host;
      }

      public int getPort() {
        return port;
      }

      public void setPort(int port) {
        this.port = port;
      }
    }

    public static class Graphite {

      private boolean enabled = false;

      private String host = "localhost";

      private int port = 2003;

      private String prefix = "versionmonitor";

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }

      public String getHost() {
        return host;
      }

      public void setHost(String host) {
        this.host = host;
      }

      public int getPort() {
        return port;
      }

      public void setPort(int port) {
        this.port = port;
      }

      public String getPrefix() {
        return prefix;
      }

      public void setPrefix(String prefix) {
        this.prefix = prefix;
      }
    }

    public static class Logs {

      private boolean enabled = false;

      private long reportFrequency = 60;

      public long getReportFrequency() {
        return reportFrequency;
      }

      public void setReportFrequency(int reportFrequency) {
        this.reportFrequency = reportFrequency;
      }

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }
    }
  }

  public static class Slack {
    private String botname = "VersionMonitor";
    private String webhookUrl;

    public String getBotname() {
      return botname;
    }

    public void setBotname(String botname) {
      this.botname = botname;
    }

    public String getWebhookUrl() {
      return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
      this.webhookUrl = webhookUrl;
    }
  }

  public static class Github {
    private Integer cachesize = 10;
    private Integer ratelimitBuffer = 20;
    @NotNull
    private String oauthToken;

    public Integer getCachesize() {
      return cachesize;
    }

    public void setCachesize(Integer cachesize) {
      this.cachesize = cachesize;
    }

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
