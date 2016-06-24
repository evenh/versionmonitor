package net.evenh.versionmonitor.domain.releases;

import com.fasterxml.jackson.annotation.JsonView;

import net.evenh.versionmonitor.domain.View;

import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHTag;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Represents a release.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Entity
@Table(name = "releases")
public class Release {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonView(View.Summary.class)
  private Long id;

  @NotNull
  @JsonView(View.Summary.class)
  private String version;

  @NotNull
  @JsonView(View.Detail.class)
  private String url;

  @Column(nullable = false)
  @JsonView(View.Detail.class)
  private Date releasedAt;

  public Long getId() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  private void setVersion(String version) {
    this.version = version;
  }

  public String getUrl() {
    return url;
  }

  private void setUrl(String url) {
    this.url = url;
  }

  public void setReleasedAt(Date releasedAt) {
    this.releasedAt = releasedAt;
  }

  /**
   * Gets the timestamp of when this project was inserted into the database.
   *
   * @return The insertion date in the database as a ISO 8601 string.
   */
  public String getReleasedAt() {
    return ZonedDateTime
            .ofInstant(releasedAt.toInstant(), ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_INSTANT);
  }

  @Override
  public String toString() {
    return "Release{" + "id='" + id + '\''
            + ", version='" + version + '\''
            + ", url='" + url + '\''
            + ", releasedAt='" + getReleasedAt() + '\''
            + '}';
  }


  /**
   * Constructs a Release object using the Builder pattern.
   */
  public static class ReleaseBuilder {
    private String version;
    private String url;
    private Boolean prerelease;
    private Date createdAt;

    private ReleaseBuilder() {
    }

    public static ReleaseBuilder builder() {
      return new ReleaseBuilder();
    }

    public ReleaseBuilder withVersion(String version) {
      this.version = version;
      return this;
    }

    public ReleaseBuilder withUrl(String url) {
      this.url = url;
      return this;
    }

    public ReleaseBuilder withPrerelease(Boolean prerelease) {
      this.prerelease = prerelease;
      return this;
    }

    public ReleaseBuilder withCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    /**
     * Populates all necessary fields using a GitHub tag.
     *
     * @param tag A <code>GHTag</code> object
     */
    public ReleaseBuilder fromGitHub(GHTag tag, String identifier) {
      this.version = tag.getName();
      this.url = "https://github.com/" + identifier + "/releases/tag/" + this.version;
      this.prerelease = null;
      try {
        this.createdAt = tag.getCommit().getCommitShortInfo().getCommitter().getDate();
      } catch (IOException e) {
        this.createdAt = new Date(0);
      }

      return this;
    }

    /**
     * Populates all necessary fields using a GitHub release.
     *
     * @param release A <code>GHRelease</code> object
     */
    public ReleaseBuilder fromGitHub(GHRelease release) {
      this.version = release.getTagName();
      this.url = release.getHtmlUrl().toString();
      this.prerelease = release.isPrerelease();
      try {
        this.createdAt = release.getCreatedAt();
      } catch (IOException e) {
        this.createdAt = new Date(0);
      }

      return this;
    }

    /**
     * Constructs the <code>Release</code> object based on information provided to the builder.
     *
     * @return A constructed <code>Release</code> object.
     */
    public Release build() {
      Release release = new Release();
      release.setVersion(version);
      release.setUrl(url);
      release.setReleasedAt(createdAt);

      return release;
    }
  }
}
