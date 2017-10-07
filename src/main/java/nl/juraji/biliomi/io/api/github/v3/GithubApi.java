package nl.juraji.biliomi.io.api.github.v3;

import nl.juraji.biliomi.io.api.github.v3.model.GithubRelease;
import nl.juraji.biliomi.io.web.Response;

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
}
