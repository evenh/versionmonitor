package net.evenh.versionmonitor.api.controllers;

import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.annotation.JsonView;

import net.evenh.versionmonitor.api.commands.AddProjectCommand;
import net.evenh.versionmonitor.application.hosts.HostRegistry;
import net.evenh.versionmonitor.application.hosts.HostService;
import net.evenh.versionmonitor.application.projects.AbstractProject;
import net.evenh.versionmonitor.application.projects.ProjectRepository;
import net.evenh.versionmonitor.application.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.application.subscriptions.SubscriptionRepository;
import net.evenh.versionmonitor.domain.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
  ProjectRepository repository;

  @Autowired
  SubscriptionRepository subscriptionRepository;

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
    List<AbstractProject> projects = repository.findAll();

    if (projects.isEmpty()) {
      return responseMessage("No projects exists", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(projects);
  }

  /**
   * Add a new project.
   *
   * @param command A <code>AddProjectCommand</code>.
   * @param result  Automatically populated validation results.
   * @return A project upon success, otherwise a JSON response describing failure.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity add(@RequestBody @Valid AddProjectCommand command, BindingResult result) {
    if (result.hasErrors()) {
      logger.debug("AddProjectCommand has validation errors", result.getAllErrors());
      return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
    }

    // Loop through hosts and process request
    for (String hostname : registry.getHosts()) {
      if (hostname.equalsIgnoreCase(command.getHost())) {

        // Check for duplicates
        if (repository.findByIdentifier(command.getIdentifier()).isPresent()) {
          logger.info("Project '{}' does already exist in the database", command.getIdentifier());
          return responseMessage("This project already exists", HttpStatus.CONFLICT);
        }

        HostService service = registry.getHostService(hostname).get();
        Optional<? extends AbstractProject> project = service.getProject(command.getIdentifier());

        if (project.isPresent()) {
          AbstractProject saved = repository.saveAndFlush(project.get());
          logger.info("Successfully added project: {}", saved);
          return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }

      }
    }

    return responseMessage("Unknown project type", HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * Gets a single project by primary key.
   *
   * @param id The primary key of an <code>AbstractProject</code>.
   * @return The project found by primary key on success, a JSON error response otherwise.
   */
  @JsonView(View.Detail.class)
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity getOne(@PathVariable Long id) {
    AbstractProject project = repository.findOne(id);

    if (project == null) {
      return responseMessage("Project not found", HttpStatus.NOT_FOUND);
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
    AbstractSubscription subscription = subscriptionRepository.findOne(subscriptionId);
    AbstractProject project = repository.findOne(id);

    if (project == null) {
      return responseMessage("Project not found", HttpStatus.NOT_FOUND);
    }

    if (subscription == null) {
      return responseMessage("Subscription not found", HttpStatus.NOT_FOUND);
    }

    boolean added = project.addSubscription(subscription);

    if (!added) {
      return responseMessage("This subscription is already linked to project " + id, HttpStatus.CONFLICT);
    }

    AbstractProject savedProject = repository.save(project);

    logger.info("Successfully added subscription for project {}: {}", savedProject, subscription);

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
    AbstractSubscription subscription = subscriptionRepository.findOne(subscriptionId);
    AbstractProject project = repository.findOne(id);

    if (project == null) {
      return responseMessage("Project not found", HttpStatus.NOT_FOUND);
    }

    if (subscription == null) {
      return responseMessage("Subscription not found", HttpStatus.NOT_FOUND);
    }

    boolean removed = project.removeSubscription(subscription);

    if (!removed) {
      return responseMessage("The subscription does not exist for the project with " + id, HttpStatus.NOT_FOUND);
    }



    AbstractProject savedProject = repository.save(project);

    logger.info("Successfully removed subscription for project {}: {}", savedProject, subscription);

    return ResponseEntity.ok(savedProject);
  }

  /**
   * Deletes a project by primary key.
   *
   * @param id The primary key of an <code>AbstractProject</code>.
   * @return HTTP 204 on success, error message otherwise.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteOne(@PathVariable Long id) {
    AbstractProject project = repository.findOne(id);

    if (project == null) {
      return responseMessage("Project not found", HttpStatus.NOT_FOUND);
    }

    repository.delete(project);

    return ResponseEntity.noContent().build();
  }

  private ResponseEntity responseMessage(String message, HttpStatus status) {
    return new ResponseEntity(ImmutableMap.of("timestamp", new Date(), "message", message), status);
  }
}
