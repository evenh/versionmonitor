package net.evenh.versionmonitor.models.projects;

import net.evenh.versionmonitor.models.Project;
import net.evenh.versionmonitor.models.Release;

import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
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
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private Long id;

  @NotNull
  private String name;

  @Column(nullable = true)
  private String description;

  @NotNull
  private String identifier;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "project_id")
  private List<Release> releases;

  public AbstractProject(){
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
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
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
  public String toString() {
    return this.getClass().getSimpleName() + "{" + "id='" + id + '\''
            + ", name='" + name + '\''
            + ", description='" + description + '\''
            + ", identifier='" + identifier + '\''
            + ", releases=" + releases
            + '}';
  }
}
