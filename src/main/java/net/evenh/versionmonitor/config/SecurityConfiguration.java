package net.evenh.versionmonitor.config;

import net.evenh.versionmonitor.security.AuthoritiesConstants;
import net.evenh.versionmonitor.security.Http401UnauthorizedEntryPoint;
import net.evenh.versionmonitor.security.xauth.TokenProvider;
import net.evenh.versionmonitor.security.xauth.XAuthTokenConfigurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Autowired
  private Http401UnauthorizedEntryPoint authenticationEntryPoint;

  @Autowired
  private UserDetailsService userDetailService;

  @Autowired
  private TokenProvider tokenProvider;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(userDetailService)
      .passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
      .antMatchers("/scripts/**/*.{js,html}")
      .antMatchers("/bower_components/**")
      .antMatchers("/i18n/**")
      .antMatchers("/assets/**")
      .antMatchers("/swagger-ui/index.html")
      .antMatchers("/test/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .exceptionHandling()
      .authenticationEntryPoint(authenticationEntryPoint)
      .and()
      .csrf()
      .disable()
      .headers()
      .frameOptions()
      .disable()
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers("/api/register").permitAll()
      .antMatchers("/api/activate").permitAll()
      .antMatchers("/api/authenticate").permitAll()
      .antMatchers("/api/account/reset_password/init").permitAll()
      .antMatchers("/api/account/reset_password/finish").permitAll()
      .antMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/api/audits/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/api/**").authenticated()
      .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/configprops/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/mappings/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/liquibase/**").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/configuration/security").permitAll()
      .antMatchers("/configuration/ui").permitAll()
      .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN)
      .antMatchers("/protected/**").authenticated()
      .and()
      .apply(securityConfigurerAdapter());

  }

  private XAuthTokenConfigurer securityConfigurerAdapter() {
    return new XAuthTokenConfigurer(userDetailService, tokenProvider);
  }

  @Bean
  public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
    return new SecurityEvaluationContextExtension();
  }
}
