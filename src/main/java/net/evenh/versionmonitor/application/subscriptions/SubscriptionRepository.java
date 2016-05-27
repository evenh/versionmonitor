package net.evenh.versionmonitor.application.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by evh on 27.05.2016.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<AbstractSubscription, Long> {
}
