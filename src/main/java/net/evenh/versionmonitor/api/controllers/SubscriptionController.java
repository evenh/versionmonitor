package net.evenh.versionmonitor.api.controllers;

import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.annotation.JsonView;

import net.evenh.versionmonitor.api.commands.AddSubscriptionCommand;
import net.evenh.versionmonitor.application.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.application.subscriptions.SubscriptionRepository;
import net.evenh.versionmonitor.domain.View;
import net.evenh.versionmonitor.domain.subscriptions.SlackSubscription;

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

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
  private final static Logger log = LoggerFactory.getLogger(SubscriptionController.class);
  @Autowired
  SubscriptionRepository subscriptionRepository;

  @JsonView(View.Summary.class)
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity getAll() {
    List<AbstractSubscription> subscriptions = subscriptionRepository.findAll();

    if (subscriptions.isEmpty()) {
      return responseMessage("No subscriptions exists", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(subscriptions);
  }

  @JsonView(View.Detail.class)
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity getOne(@PathVariable Long id) {
    AbstractSubscription subscription = subscriptionRepository.findOne(id);

    if (subscription == null) {
      return responseMessage("Subscription not found", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(subscription);
  }

  // TODO: duplicate check
  @JsonView(View.Detail.class)
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity create(@RequestBody @Valid AddSubscriptionCommand command) {
    if (command.getService().equalsIgnoreCase("slack")) {
      final SlackSubscription saved = subscriptionRepository.save(new SlackSubscription(command));
      log.info("Successfully saved subscription: {}", saved);

      return ResponseEntity.ok(saved);
    }

    return responseMessage("Unknown service", HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteOne(@PathVariable Long id) {
    AbstractSubscription subscription = subscriptionRepository.findOne(id);

    if (subscription == null) {
      return responseMessage("Subscription not found", HttpStatus.NOT_FOUND);
    }

    subscriptionRepository.delete(subscription);

    return ResponseEntity.noContent().build();
  }

  // TODO: Not do it like this :-)
  private ResponseEntity responseMessage(String message, HttpStatus status) {
    return new ResponseEntity(ImmutableMap.of("timestamp", new Date(), "message", message), status);
  }
}
