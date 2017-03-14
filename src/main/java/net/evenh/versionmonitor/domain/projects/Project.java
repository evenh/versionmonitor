package net.evenh.versionmonitor.domain.projects;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
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
import net.evenh.versionmonitor.domain.releases.Release;
import net.evenh.versionmonitor.domain.subscriptions.Subscription;
import net.evenh.versionmonitor.infrastructure.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The project class specifies a default software project found on a host.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Project {
  private static final Logger log = LoggerFactory.getLogger(Project.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.Summary.class)
  private Long id;

  /**
   * Gets the name of a software project.
   */
  @NotNull
  @JsonView(View.Summary.class)
  private String name;

  /**
   * A description of the software project.
   *
   * <p><i>Note that all hosts may not have this information.</i>
   */
  @JsonView(View.Summary.class)
  private String description;

  /**
   * A unique identifier to identify this project with the host.
   *
   * <p>Can be a URL or anything a String can hold.
   */
  @NotNull
  @JsonView(View.Summary.class)
  private String identifier;

  /**
   * Releases for this software project.
   */
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id")
  @JsonView(View.Detail.class)
  private List<Release> releases;

  /**
   * List of notification subscribers for this project.
   */
  @ManyToMany(
      cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH},
      fetch = FetchType.EAGER
      )
  @JsonView(View.Detail.class)
  private Set<Subscription> subscriptions;

  public Project() {
  }

  public Project(String identifier) {
    this.identifier = identifier;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(Optional<String> description) {
    this.description = description.get();
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public List<Release> getReleases() {
    return releases;
  }

  public void setReleases(List<Release> releases) {
    this.releases = releases;
  }

  public void addRelease(Release release) {
    releases.add(release);
  }

  public void setSubscriptions(Set<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  /**
   * Add a {@link Subscription} to this project.
   */
  public boolean addSubscription(Subscription subscription) {
    if (subscriptions.contains(subscription)) {
      log.debug("Subscription does already exist - won't add: {}", subscription);
      return false;
    } else {
      subscriptions.add(subscription);
    }

    return true;
  }

  /**
   * Remove a {@link Subscription} from this project.
   */
  public boolean removeSubscription(Subscription subscription) {
    if (!subscriptions.contains(subscription)) {
      log.debug("Subscription does not exist - won't remove: {}", subscription);
      return false;
    } else {
      subscriptions.remove(subscription);
    }

    return true;
  }

  public Set<Subscription> getSubscriptions() {
    return subscriptions;
  }

  /**
   * Returns the URL of this project on a specific host.
   */
  public abstract String getProjectUrl();

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" + "id='" + id + '\''
      + ", name='" + name + '\''
      + ", description='" + description + '\''
      + ", identifier='" + identifier + '\''
      + '}';
  }
}
