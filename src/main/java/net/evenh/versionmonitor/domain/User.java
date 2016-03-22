package net.evenh.versionmonitor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.validator.constraints.Email;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A user.
 */
@Entity
@Table(name = "vm_user")
public class User extends AbstractAuditingEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Pattern(regexp = "^[a-z0-9]*$|(anonymousUser)")
  @Size(min = 1, max = 50)
  @Column(length = 50, unique = true, nullable = false)
  private String login;

  @JsonIgnore
  @NotNull
  @Size(min = 60, max = 60)
  @Column(name = "password_hash", length = 60)
  private String password;

  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @Email
  @Size(max = 100)
  @Column(length = 100, unique = true)
  private String email;

  @Column(nullable = false)
  private boolean activated = false;

  @Size(max = 20)
  @Column(name = "activation_key", length = 20)
  @JsonIgnore
  private String activationKey;

  @Size(max = 20)
  @Column(name = "reset_key", length = 20)
  private String resetKey;

  @Column(name = "reset_date", nullable = true)
  private ZonedDateTime resetDate = null;

  @JsonIgnore
  @ManyToMany
  @JoinTable(
    name = "vm_user_authority",
    joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
  private Set<Authority> authorities = new HashSet<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean getActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public String getResetKey() {
    return resetKey;
  }

  public void setResetKey(String resetKey) {
    this.resetKey = resetKey;
  }

  public ZonedDateTime getResetDate() {
    return resetDate;
  }

  public void setResetDate(ZonedDateTime resetDate) {
    this.resetDate = resetDate;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    User user = (User) obj;

    return login.equals(user.login);
  }

  @Override
  public int hashCode() {
    return login.hashCode();
  }

  @Override
  public String toString() {
    return "User{"
      + "login='" + login + '\''
      + ", firstName='" + firstName + '\''
      + ", lastName='" + lastName + '\''
      + ", email='" + email + '\''
      + ", activated='" + activated + '\''
      + ", activationKey='" + activationKey + '\''
      + "}";
  }
}
