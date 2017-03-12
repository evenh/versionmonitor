package net.evenh.versionmonitor.api.controllers;

import com.fasterxml.jackson.annotation.JsonView;

import net.evenh.versionmonitor.api.commands.AddProjectCommand;
import net.evenh.versionmonitor.api.exceptions.DuplicateProjectException;
import net.evenh.versionmonitor.api.exceptions.NoProjectsExistsException;
import net.evenh.versionmonitor.api.exceptions.ProjectNotFoundException;
import net.evenh.versionmonitor.api.exceptions.SubscriptionAlreadyLinkedToProjectException;
import net.evenh.versionmonitor.api.exceptions.SubscriptionNotFoundException;
import net.evenh.versionmonitor.api.exceptions.UnknownProjectTypeException;
import net.evenh.versionmonitor.domain.hosts.HostRegistry;
import net.evenh.versionmonitor.domain.hosts.HostService;
import net.evenh.versionmonitor.domain.projects.Project;
import net.evenh.versionmonitor.domain.projects.ProjectService;
import net.evenh.versionmonitor.domain.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.application.subscriptions.SubscriptionService;
import net.evenh.versionmonitor.infrastructure.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

/**
 * Handles CRUD operations for various software projects.
 *
 * @author Even Holthe
 * @since 2016-01-10
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
  private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

  @Autowired
  private ProjectService projectService;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  HostRegistry registry;

  /**
   * Get all existing projects.
   *
   * @return A list of existing projects.
   */
  @JsonView(View.Summary.class)
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity getAllProjects() {
    List<Project> projects = projectService.findAll();

    if (projects.isEmpty()) {
      throw new NoProjectsExistsException();
    }

    return ResponseEntity.ok(projects);
  }

  /**
   * Add a new project.
   *
   * @param command A <code>AddProjectCommand</code>.
   * @return A project upon success, otherwise a JSON response describing failure.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity add(@Valid @RequestBody AddProjectCommand command) {
    // Loop through hosts and process request
    for (String hostname : registry.getHosts()) {
      if (hostname.equalsIgnoreCase(command.getHost())) {

        // Check for duplicates
        if (projectService.doesExist(command.getIdentifier())) {
          logger.info("Project '{}' does already exist in the database", command.getIdentifier());
          throw new DuplicateProjectException();
        }

        HostService hostService = registry.getHostService(hostname).get();
        Optional<? extends Project> project = hostService.getProject(command.getIdentifier());

        if (project.isPresent()) {
          Project saved = projectService.persist(project.get());
          logger.info("Successfully added project: {}", saved);
          return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }

      }
    }

    throw new UnknownProjectTypeException();
  }

  /**
   * Gets a single project by primary key.
   *
   * @param id The primary key of an <code>Project</code>.
   * @return The project found by primary key on success, a JSON error response otherwise.
   */
  @JsonView(View.Detail.class)
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity getOne(@PathVariable Long id) {
    Optional<Project> project = projectService.findOne(id);

    if (!project.isPresent()) {
      throw new ProjectNotFoundException();
    }

    return ResponseEntity.ok(project);
  }

  /**
   * Links a <code>AbstractSubscription</code> to a given projet.
   * @param id The id of the project.
   * @param subscriptionId The subscription id to link.
   */
  @JsonView(View.Summary.class)
  @RequestMapping(value = "/{id}/subscribe/{subscriptionId}", method = RequestMethod.POST)
  public ResponseEntity addSubscriber(@PathVariable Long id, @PathVariable Long subscriptionId) {
    Optional<AbstractSubscription> subscriptionMaybe = subscriptionService.findOne(subscriptionId);
    Optional<Project> projectMaybe = projectService.findOne(id);

    if (!projectMaybe.isPresent()) {
      throw new ProjectNotFoundException();
    }

    if (!subscriptionMaybe.isPresent()) {
      throw new SubscriptionNotFoundException();
    }

    Project project = projectMaybe.get();

    if (!project.addSubscription(subscriptionMaybe.get())) {
      throw new SubscriptionAlreadyLinkedToProjectException();
    }

    Project savedProject = projectService.persist(project);

    logger.info("Successfully added subscription for project {}: {}", savedProject, subscriptionMaybe);

    return ResponseEntity.ok(savedProject);
  }

  /**
   * Unlinks a <code>AbstractSubscription</code> from a given projet.
   * @param id The id of the project.
   * @param subscriptionId The subscription id to unlink.
   */
  @JsonView(View.Summary.class)
  @RequestMapping(value = "/{id}/unsubscribe/{subscriptionId}", method = RequestMethod.POST)
  public ResponseEntity removeSubscriber(@PathVariable Long id, @PathVariable Long subscriptionId) {
    Optional<AbstractSubscription> subscriptionMaybe = subscriptionService.findOne(subscriptionId);
    Optional<Project> projectMaybe = projectService.findOne(id);

    if (!projectMaybe.isPresent()) {
      throw new ProjectNotFoundException();
    }

    if (!subscriptionMaybe.isPresent()) {
      throw new SubscriptionNotFoundException();
    }


    Project project = projectMaybe.get();

    if (!project.removeSubscription(subscriptionMaybe.get())) {
      throw new SubscriptionNotFoundException();
    }

    Project savedProject = projectService.persist(project);

    logger.info("Successfully removed subscription for project {}: {}", savedProject, subscriptionMaybe);

    return ResponseEntity.ok(savedProject);
  }

  /**
   * Deletes a project by primary key.
   *
   * @param id The primary key of an <code>Project</code>.
   * @return HTTP 204 on success, error message otherwise.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteOne(@PathVariable Long id) {
    Optional<Project> project = projectService.findOne(id);

    if (!project.isPresent()) {
      throw new ProjectNotFoundException();
    }

    projectService.delete(project.get());

    return ResponseEntity.noContent().build();
  }
}
