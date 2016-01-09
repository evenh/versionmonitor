package net.evenh.versionmonitor.models;

import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHTag;

import java.util.Optional;

/**
 * Represents a release.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
public class Release {
  private String version;
  private String url;
  private Optional<Boolean> prerelease;

  private Release() {
    this.prerelease = Optional.empty();
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

  public Optional<Boolean> isPrerelease() {
    return prerelease;
  }

  private void setPrerelease(Optional<Boolean> prerelease) {
    this.prerelease = prerelease;
  }

  public void setPrerelease(boolean isPrerelease) {
    setPrerelease(Optional.of(isPrerelease));
  }

  @Override
  public String toString() {
    String preRelease = (prerelease.isPresent() ? prerelease.get().toString() : "N/A");

    return "Release{" + "version='" + version + '\''
            + ", prerelease='" + preRelease + '\''
            + ", url='" + url + '\''
            + '}';
  }


  /**
   * Constructs a Release object using the Builder pattern.
   */
  public static class ReleaseBuilder {
    private String version;
    private String url;
    private Optional<Boolean> prerelease;

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

    public ReleaseBuilder withPrerelease(Optional<Boolean> prerelease) {
      this.prerelease = prerelease;
      return this;
    }

    public ReleaseBuilder withPrerelease(boolean prerelease) {
      this.prerelease = Optional.ofNullable(prerelease);
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
      this.prerelease = Optional.empty();

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
      this.prerelease = Optional.of(release.isPrerelease());

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
