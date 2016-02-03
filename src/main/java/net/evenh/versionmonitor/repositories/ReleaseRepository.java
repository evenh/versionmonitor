package net.evenh.versionmonitor.repositories;

import net.evenh.versionmonitor.models.Release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * A repository for persisting various software project releases.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Component
public interface ReleaseRepository extends JpaRepository<Release, Long> {
}
