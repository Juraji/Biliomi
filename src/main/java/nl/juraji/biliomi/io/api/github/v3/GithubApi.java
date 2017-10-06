package nl.juraji.biliomi.io.api.github.v3;

import nl.juraji.biliomi.io.api.github.v3.model.GithubRelease;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.utility.factories.ModelUtils;

import java.util.Comparator;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
public interface GithubApi {

  /**
   * Retrieve the latest release from a repository
   *
   * @see <a href="https://developer.github.com/v3/repos/releases/#get-the-latest-release">Get the latest release</a>
   */
  Response<GithubRelease> getLatestRelease(String owner, String repository) throws Exception;

  /**
   * Compare two release tags.
   * Note: Expects release tags to be formatted like: vX.X.X
   *
   * @param currentVersionTag The current release tag
   * @param newVersionTag     The target release tag
   * @return True if the target tag is a higher version than ther current tag, else False
   */
  default boolean isNewRelease(String currentVersionTag, String newVersionTag) {
    if (newVersionTag == null) {
      return false;
    }

    String latest = ModelUtils.listOf(currentVersionTag, newVersionTag).stream()
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(currentVersionTag);

    return !latest.equals(currentVersionTag);
  }
}
