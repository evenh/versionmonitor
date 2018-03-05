package net.evenh.versionmonitor.application.subscriptions;

import java.util.List;
import java.util.Optional;
import net.evenh.versionmonitor.domain.subscriptions.Subscription;
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
  public List<Subscription> findAll() {
    return repository.findAll();
  }

  /**
   * Finds a subscription by id.
   */
  public Optional<Subscription> findOne(Long id) {
    return repository.findById(id);
  }

  /**
   * Persists a subscription.
   */
  public Subscription save(Subscription subscription) {
    return repository.saveAndFlush(subscription);
  }

  /**
   * Deletes a subscription.
   */
  public void delete(Subscription subscription) {
    repository.delete(subscription);
  }
}
