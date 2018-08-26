package net.evenh.versionmonitor.api.commands;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

public class AddSubscriptionCommand {
  @NotNull
  @NotEmpty
  private String service;

  @NotNull
  @NotEmpty
  private String name;

  @NotNull
  @NotEmpty
  private String identifier;

  private String channel;

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
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

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  @Override
  public String toString() {
    return "AddSubscriptionCommand{"
      + "service='" + service + '\''
      + ", name='" + name + '\''
      + ", identifier='" + identifier + '\''
      + ", channel='" + channel + '\''
      + '}';
  }
}
