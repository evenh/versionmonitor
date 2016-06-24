package net.evenh.versionmonitor.application.hosts.impl;

import net.evenh.versionmonitor.application.hosts.HostRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("npmHostService")
public class NpmHostService {
  private static final Logger logger = LoggerFactory.getLogger(GitHubHostService.class);

  @Autowired
  private HostRegistry registry;
}
