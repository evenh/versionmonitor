package net.evenh.versionmonitor.security;

import net.evenh.versionmonitor.domain.User;
import net.evenh.versionmonitor.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Service("userDetailService")
public class UserDetailService implements UserDetailsService {
  private final Logger log = LoggerFactory.getLogger(UserDetailService.class);

  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(final String login) {
    log.debug("Authenticating username: {}", login);
    String lowercaseLogin = login.toLowerCase();
    Optional<User> userFromDatabase = userRepository.findOneByLogin(lowercaseLogin);
    return userFromDatabase.map(user -> {
      if (!user.getActivated()) {
        throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
      }
      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
        .collect(Collectors.toList());
      return new org.springframework.security.core.userdetails.User(lowercaseLogin,
        user.getPassword(),
        grantedAuthorities);
    }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found "
      + "in the database"));
  }
}
