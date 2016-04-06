package net.evenh.versionmonitor.web.rest;

import com.codahale.metrics.annotation.Timed;

import net.evenh.versionmonitor.security.jwt.JWTConfigurer;
import net.evenh.versionmonitor.security.jwt.TokenProvider;
import net.evenh.versionmonitor.web.rest.dto.LoginDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserJWTController {

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  @Timed
  public ResponseEntity<?> authorize(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {

    UsernamePasswordAuthenticationToken authenticationToken =
      new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

    try {
      Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      boolean rememberMe = (loginDTO.isRememberMe() == null) ? false : loginDTO.isRememberMe();
      String jwt = tokenProvider.createToken(authentication, rememberMe);
      response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
      return ResponseEntity.ok(new JWTToken(jwt));
    } catch (AuthenticationException exception) {
      return new ResponseEntity<>(exception.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
    }
  }
}
