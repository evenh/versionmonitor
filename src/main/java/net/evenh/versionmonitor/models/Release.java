package net.evenh.versionmonitor.models;

import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHTag;

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
  private Long id;

  @NotNull
  private String version;

  @NotNull
  private String url;

  @Column(nullable = true)
  private Boolean prerelease;

  private Release() {
    this.prerelease = null;
  }

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

  public Boolean isPrerelease() {
    return prerelease;
  }

  private void setPrerelease(Boolean prerelease) {
    this.prerelease = prerelease;
  }

  @Override
  public String toString() {
    return "Release{" + "id='" + id + '\''
            + ", version='" + version + '\''
            + ", prerelease='" + prerelease + '\''
            + ", url='" + url + '\''
            + '}';
  }


  /**
   * Constructs a Release object using the Builder pattern.
   */
  public static class ReleaseBuilder {
    private String version;
    private String url;
    private Boolean prerelease;

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

    /**
     * Populates all necessary fields using a GitHub tag.
     *
     * @param tag A <code>GHTag</code> object
     */
    public ReleaseBuilder fromGitHub(GHTag tag, String identifier) {
      this.version = tag.getName();
      this.url = "https://github.com/" + identifier + "/releases/tag/" + this.version;
      this.prerelease = null;

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
      release.setPrerelease(prerelease);

      return release;
    }
  }
}
