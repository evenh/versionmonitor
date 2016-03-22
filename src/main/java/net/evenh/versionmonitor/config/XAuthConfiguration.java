package net.evenh.versionmonitor.config;

import net.evenh.versionmonitor.security.xauth.TokenProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures x-auth-token security.
 */
@Configuration
public class XAuthConfiguration {
  @Bean
  public TokenProvider tokenProvider(VersionmonitorConfiguration props) {
    String secret = props.getSecurity().getAuthentication().getXauth().getSecret();
    int validityInSeconds = props.getSecurity().getAuthentication().getXauth().getTokenValidityInSeconds();
    return new TokenProvider(secret, validityInSeconds);
  }
}
