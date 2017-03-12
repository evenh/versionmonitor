package net.evenh.versionmonitor.application.subscriptions;

import java.util.List;
import java.util.Optional;
import net.evenh.versionmonitor.domain.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.domain.subscriptions.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
  private final SubscriptionRepository repository;

  @Autowired
  public SubscriptionService(SubscriptionRepository repository) {
    this.repository = repository;
  }


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
