package net.evenh.versionmonitor.application.hosts.npm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NpmReleaseRepresentation {
  private String version;
  private Date released;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Date getReleased() {
    return released;
  }

  public void setReleased(Date released) {
    this.released = released;
  }

  @Override
  public String toString() {
    return "npm-release{" +
      "version='" + version + '\'' +
      ", released='" + released + '\'' +
      '}';
  }
}
