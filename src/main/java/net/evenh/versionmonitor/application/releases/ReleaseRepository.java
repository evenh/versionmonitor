package net.evenh.versionmonitor.application.releases;

import net.evenh.versionmonitor.domain.Release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for persisting various software project releases.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long> {
}
