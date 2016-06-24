package net.evenh.versionmonitor.domain.projects.npm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NpmProjectRepresentation {
  @JsonProperty("_id")
  private String name;
  private String description;

  @JsonProperty("versions")
  private Map<String, NpmReleaseRepresentation> releases;

  private Map<String, Date> time;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, NpmReleaseRepresentation> getReleases() {
    return releases;
  }

  public void setReleases(Map<String, NpmReleaseRepresentation> releases) {
    this.releases = releases;
  }

  public Map<String, Date> getTime() {
    return time;
  }

  public void setTime(Map<String, Date> time) {
    this.time = time;
  }

  @Override
  public String toString() {
    return "NpmProjectRepresentation{" +
      "name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", releases=" + releases +
      ", time=" + time +
      '}';
  }
}
