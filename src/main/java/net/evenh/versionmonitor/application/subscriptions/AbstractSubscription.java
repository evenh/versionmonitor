package net.evenh.versionmonitor.application.subscriptions;

import com.fasterxml.jackson.annotation.JsonView;

import net.evenh.versionmonitor.api.commands.AddSubscriptionCommand;
import net.evenh.versionmonitor.domain.View;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;

@Entity(name = "subscriptions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractSubscription implements Subscription {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonView(View.Summary.class)
  private Long id;

  @NotNull
  @NotEmpty
  @JsonView(View.Summary.class)
  private String name;

  @NotNull
  @NotEmpty
  @JsonView(View.Summary.class)
  private String identifier;

  public AbstractSubscription() {
  }

  public AbstractSubscription(AddSubscriptionCommand command) {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
}
