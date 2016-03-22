package net.evenh.versionmonitor.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
  private final Logger log = LoggerFactory.getLogger(AuthenticationProvider.class);

  private PasswordEncoder passwordEncoder;

  private UserDetailService userDetailService;

  public AuthenticationProvider(UserDetailService userDetailService, PasswordEncoder passwordEncoder) {
    this.userDetailService = userDetailService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    UsernamePasswordAuthenticationToken token =
      (UsernamePasswordAuthenticationToken) authentication;

    String login = token.getName();
    UserDetails user = userDetailService.loadUserByUsername(login);
    if (user == null) {
      throw new UsernameNotFoundException("User does not exists");
    }
    String password = user.getPassword();
    String tokenPassword = (String) token.getCredentials();
    if (!passwordEncoder.matches(tokenPassword, password)) {
      throw new BadCredentialsException("Invalid username/password");
    }
    return new UsernamePasswordAuthenticationToken(user, password,
      user.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken
      .class.equals(authentication);
  }
}
