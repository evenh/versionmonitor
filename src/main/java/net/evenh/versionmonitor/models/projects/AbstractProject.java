package net.evenh.versionmonitor.models.projects;

import net.evenh.versionmonitor.models.Project;
import net.evenh.versionmonitor.models.Release;

import java.util.List;
import java.util.Optional;

/**
 * A default project which shall be extended.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
public abstract class AbstractProject implements Project {
  private String name;
  private Optional<String> description = Optional.empty();
  private String identifier;
  private List<Release> releases;

  public AbstractProject(){
  }

  public AbstractProject(String identifier) {
    this.identifier = identifier;
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
    return description;
  }

  public void setDescription(Optional<String> description) {
    this.description = description;
  }

  public void setDescription(String description) {
    setDescription(Optional.ofNullable(description));
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
    return this.getClass().getSimpleName() + "{" + "name='" + name + '\''
            + ", description='" + description.orElse(null) + '\''
            + ", identifier='" + identifier + '\''
            + ", releases=" + releases
            + '}';
  }
}
