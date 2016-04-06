package net.evenh.versionmonitor.security.jwt;

import net.evenh.versionmonitor.config.VersionmonitorConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class TokenProvider {
  private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

  private static final String AUTHORITIES_KEY = "auth";
  private String secretKey;
  private long tokenValidityInSeconds;
  private long tokenValidityInSecondsForRememberMe;

  @Autowired
  private VersionmonitorConfiguration config;

  @PostConstruct
  public void init() {
    this.secretKey = config.getSecurity().getAuthentication().getJwt().getSecret();
    this.tokenValidityInSeconds =
      1000 * config.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
    this.tokenValidityInSecondsForRememberMe =
      1000 * config.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
  }

  public String createToken(Authentication authentication, Boolean rememberMe) {
    String authorities = authentication.getAuthorities().stream()
      .map(authority -> authority.getAuthority())
      .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity = new Date(now);
    if (rememberMe) {
      validity = new Date(now + this.tokenValidityInSecondsForRememberMe);
    } else {
      validity = new Date(now + this.tokenValidityInSeconds);
    }

    return Jwts.builder()
      .setSubject(authentication.getName())
      .claim(AUTHORITIES_KEY, authorities)
      .signWith(SignatureAlgorithm.HS512, secretKey)
      .setExpiration(validity)
      .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parser()
      .setSigningKey(secretKey)
      .parseClaimsJws(token)
      .getBody();

    String principal = claims.getSubject();

    Collection<? extends GrantedAuthority> authorities =
      Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")).stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      log.info("Invalid JWT signature: " + e.getMessage());
      return false;
    }
  }
}
