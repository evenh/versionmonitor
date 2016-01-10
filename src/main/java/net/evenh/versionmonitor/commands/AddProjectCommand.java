package net.evenh.versionmonitor.commands;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * Add new project command.
 *
 * @author Even Holthe
 * @since 2016-01-10
 */
public class AddProjectCommand implements Serializable {
  @NotNull
  private String host;
  @NotNull
  private String identifier;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String toString() {
    return "AddProjectCommand{"
            + "host='" + host + '\''
            + ", identifier='" + identifier + '\''
            + '}';
  }
}
