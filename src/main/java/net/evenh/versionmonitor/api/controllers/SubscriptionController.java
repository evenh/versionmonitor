package net.evenh.versionmonitor.api.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import net.evenh.versionmonitor.api.commands.AddSubscriptionCommand;
import net.evenh.versionmonitor.api.exceptions.NoSubscriptionsExistsException;
import net.evenh.versionmonitor.api.exceptions.SubscriptionNotFoundException;
import net.evenh.versionmonitor.api.exceptions.UnknownSubscriptionServiceException;
import net.evenh.versionmonitor.application.subscriptions.SubscriptionService;
import net.evenh.versionmonitor.application.subscriptions.types.SlackSubscription;
import net.evenh.versionmonitor.domain.subscriptions.Subscription;
import net.evenh.versionmonitor.infrastructure.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
  private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);
  @Autowired
  private SubscriptionService subscriptionService;

  /**
   * Gets all subscriptions.
   */
  @JsonView(View.Summary.class)
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity getAll() {
    List<Subscription> subscriptions = subscriptionService.findAll();

    if (subscriptions.isEmpty()) {
      throw new NoSubscriptionsExistsException();
    }

    return ResponseEntity.ok(subscriptions);
  }

  /**
   * Gets a single subscription.
   */
  @JsonView(View.Detail.class)
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity getOne(@PathVariable Long id) {
    Optional<Subscription> subscription = subscriptionService.findOne(id);

    if (!subscription.isPresent()) {
      throw new SubscriptionNotFoundException();
    }

    return ResponseEntity.ok(subscription);
  }

  /**
   * Create a subscription.
   */
  // TODO: duplicate check
  @JsonView(View.Detail.class)
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity create(@RequestBody @Valid AddSubscriptionCommand command) {
    if (command.getService().equalsIgnoreCase("slack")) {
      final Subscription saved = subscriptionService.save(new SlackSubscription(command));
      log.info("Successfully saved subscription: {}", saved);

      return ResponseEntity.ok(saved);
    }

    throw new UnknownSubscriptionServiceException();
  }

  /**
   * Delete a subscription.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteOne(@PathVariable Long id) {
    Optional<Subscription> subscription = subscriptionService.findOne(id);

    if (!subscription.isPresent()) {
      throw new SubscriptionNotFoundException();
    }

    subscriptionService.delete(subscription.get());

    return ResponseEntity.noContent().build();
  }
}
