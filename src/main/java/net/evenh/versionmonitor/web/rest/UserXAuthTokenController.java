package net.evenh.versionmonitor.web.rest;

import com.codahale.metrics.annotation.Timed;

import net.evenh.versionmonitor.security.xauth.Token;
import net.evenh.versionmonitor.security.xauth.TokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserXAuthTokenController {
  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService userDetailsService;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  @Timed
  public Token authorize(@RequestParam String username, @RequestParam String password) {

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    Authentication authentication = this.authenticationManager.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetails details = this.userDetailsService.loadUserByUsername(username);
    return tokenProvider.createToken(details);
  }
}
