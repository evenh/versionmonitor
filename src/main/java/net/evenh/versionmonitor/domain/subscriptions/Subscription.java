package net.evenh.versionmonitor.domain.subscriptions;

import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import net.evenh.versionmonitor.infrastructure.View;
import org.hibernate.validator.constraints.NotEmpty;

@Entity(name = "subscription")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Subscription {
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

  public Long getId() {
    return id;
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

  public abstract String getServiceName();
}
