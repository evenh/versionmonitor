package net.evenh.versionmonitor.application.subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {
  @Autowired
  private SubscriptionRepository repository;


  /**
   * Finds all subscriptions persisted in the database.
   */
  public List<AbstractSubscription> findAll() {
    return repository.findAll();
  }

  /**
   * Finds a subscription by id.
   */
  public Optional<AbstractSubscription> findOne(Long id) {
    return Optional.ofNullable(repository.findOne(id));
  }

  /**
   * Persists a subscription.
   */
  public AbstractSubscription save(AbstractSubscription subscription) {
    return repository.saveAndFlush(subscription);
  }

  /**
   * Deletes a subscription.
   */
  public void delete(AbstractSubscription subscription) {
    repository.delete(subscription);
  }
}
