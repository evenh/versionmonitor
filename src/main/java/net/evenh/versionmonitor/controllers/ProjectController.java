package net.evenh.versionmonitor.controllers;

import net.evenh.versionmonitor.commands.AddProjectCommand;
import net.evenh.versionmonitor.composites.ErrorMessageComposite;
import net.evenh.versionmonitor.models.ErrorCode;
import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.repositories.ProjectRepository;

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

import java.io.FileNotFoundException;
import java.util.List;

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
  private ProjectRepository repository;

  private ErrorCode error;

  /**
   * Get all existing projects.
   *
   * @return A list of existing projects.
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity getAllProjects() {
    List<AbstractProject> projects = repository.findAll();

    if (projects.isEmpty()) {
      ErrorCode error = ErrorCode.NO_PROJECTS;
      return new ResponseEntity<>(ErrorMessageComposite.of(error), error.getHttpStatus());
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

    if (command.getHost().equals("github")) {
      // Check for duplicates
      if (repository.findByIdentifier(command.getIdentifier()).isPresent()) {
        logger.info("Project '{}' does already exist in the database", command.getIdentifier());
        return errorOf(ErrorCode.DUPLICATE_PROJECT);
      }

      try {
        AbstractProject saved = repository.saveAndFlush(new GitHubProject(command.getIdentifier()));
        logger.info("Successfully added project: {}", saved);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
      } catch (FileNotFoundException nfe) {
        return errorOf(ErrorCode.HOST_UNKNOWN_PROJECT);
      } catch (Exception e) {
        logger.warn("Got exception while adding new project", e);
        return errorOf(ErrorCode.ERROR_CREATING_PROJECT);
      }
    }

    return errorOf(ErrorCode.UNKNOWN_PROJECT_TYPE);
  }

  /**
   * Gets a single project by primary key.
   *
   * @param id The primary key of an <code>AbstractProject</code>.
   * @return The project found by primary key on success, a JSON error response otherwise.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity getOne(@PathVariable Long id) {
    AbstractProject project = repository.findOne(id);

    if (project == null) {
      return errorOf(ErrorCode.PROJECT_NOT_FOUND);
    }

    return ResponseEntity.ok(project);
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
      return errorOf(ErrorCode.PROJECT_NOT_FOUND);
    }

    repository.delete(project);

    return ResponseEntity.noContent().build();
  }

  /**
   * Creates a <code>ResponseEntity</code> for a given <code>ErrorCode</code>.
   *
   * @param error A <code>ErrorCode</code>.
   * @return A populated <code>ResponseEntity</code> containing an
   *        <code>ErrorMessageComposite</code>.
   */
  private ResponseEntity errorOf(ErrorCode error) {
    return new ResponseEntity<>(ErrorMessageComposite.of(error), error.getHttpStatus());
  }
}
