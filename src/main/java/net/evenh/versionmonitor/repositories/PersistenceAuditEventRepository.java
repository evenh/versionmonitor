package net.evenh.versionmonitor.repositories;

import net.evenh.versionmonitor.domain.PersistentAuditEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the PersistentAuditEvent entity.
 */
@Repository
public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, Long> {
  List<PersistentAuditEvent> findByPrincipal(String principal);

  List<PersistentAuditEvent> findByPrincipalAndAuditEventDateAfter(String principal, LocalDateTime after);

  List<PersistentAuditEvent> findAllByAuditEventDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
