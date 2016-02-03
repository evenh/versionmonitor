package net.evenh.versionmonitor.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A subscription of new release notifications.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Entity(name = "subscriptions")
public class Subscription {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String identifier;

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

  @Override
  public String toString() {
    return "Subscription{"
            + "id='" + id + '\''
            + "name='" + name + '\''
            + ", identifier='" + identifier + '\''
            + '}';
  }
}
