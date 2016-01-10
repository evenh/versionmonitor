package net.evenh.versionmonitor.controllers;

import net.evenh.versionmonitor.commands.AddProjectCommand;
import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.repositories.ProjectRepository;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ProjectController.class);

  @Autowired
  private ProjectRepository repository;

  /**
   * Get all existing projects.
   *
   * @return A list of existing projects.
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<AbstractProject> getAllProjects() {
    return repository.findAll();
  }

  /**
   * Add a new project.
   *
   * @param command A <code>AddProjectCommand</code>.
   * @param result  Automatically populated validation results.
   * @return A <code>ResponseEntity</code> that describes success or failure.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity add(@RequestBody @Valid AddProjectCommand command, BindingResult result) {
    if (result.hasErrors()) {
      return new ResponseEntity(result.getAllErrors(), HttpStatus.BAD_REQUEST);
    }

    if (command.getHost().equals("github")) {
      AbstractProject project = new GitHubProject(command.getIdentifier());
      return new ResponseEntity(repository.saveAndFlush(project), HttpStatus.CREATED);
    }

    return new ResponseEntity("Not processed. I only know github projects for now.", HttpStatus.OK);
  }
}
