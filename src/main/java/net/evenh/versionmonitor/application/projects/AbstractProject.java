package net.evenh.versionmonitor.application.projects;

import com.fasterxml.jackson.annotation.JsonView;

import net.evenh.versionmonitor.application.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.domain.View;
import net.evenh.versionmonitor.domain.releases.Release;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 * A default project which shall be extended.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractProject implements Project {
  private static final Logger log = LoggerFactory.getLogger(AbstractProject.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.Summary.class)
  private Long id;

  @NotNull
  @JsonView(View.Summary.class)
  private String name;

  @Column(nullable = true)
  @JsonView(View.Summary.class)
  private String description;

  @NotNull
  @JsonView(View.Summary.class)
  private String identifier;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id")
  @JsonView(View.Detail.class)
  private List<Release> releases;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
  @JsonView(View.Detail.class)
  private Set<AbstractSubscription> subscriptions;

  public AbstractProject() {
  }

  public AbstractProject(String identifier) {
    this.identifier = identifier;
  }

  public Long getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(Optional<String> description) {
    this.description = description.get();
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public List<Release> getReleases() {
    return releases;
  }

  public void setReleases(List<Release> releases) {
    this.releases = releases;
  }

  @Override
  public void addRelease(Release release) {
    releases.add(release);
  }

  public void setSubscriptions(Set<AbstractSubscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  @Override
  public boolean addSubscription(AbstractSubscription subscription) {
    if (subscriptions.contains(subscription)) {
      log.info("Subscription does already exist - won't add: {}", subscription);
      return false;
    } else {
      subscriptions.add(subscription);
    }

    return true;
  }

  @Override
  public boolean removeSubscription(AbstractSubscription subscription) {
    if (!subscriptions.contains(subscription)) {
      log.info("Subscription does not exist - won't remove: {}", subscription);
      return false;
    } else {
      subscriptions.remove(subscription);
    }

    return true;
  }

  @Override
  public Set<AbstractSubscription> getSubscriptions() {
    return subscriptions;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" + "id='" + id + '\''
      + ", name='" + name + '\''
      + ", description='" + description + '\''
      + ", identifier='" + identifier + '\''
      + '}';
  }
}
