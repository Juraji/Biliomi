package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.api.github.v3.GithubApi;
import nl.juraji.biliomi.io.api.github.v3.model.GithubRelease;
import nl.juraji.biliomi.io.api.github.v3.model.GithubReleaseAsset;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.factories.archives.TarArchiveUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 0) // First
public class FindUpdatesSetupTask implements SetupTask {
  private static final String DOWNLOAD_CONTENT_TYPE = "application/x-gzip";

  @Inject
  @AppDataValue("github.repository.owner")
  private String ghRepoOwner;

  @Inject
  @AppDataValue("github.repository.name")
  private String ghRepoName;

  @Inject
  private Logger logger;

  @Inject
  private VersionInfo versionInfo;

  @Inject
  private GithubApi githubApi;

  @Inject
  private ConsoleApi consoleApi;

  private final File downloadDir;
  private final File installDir;

  public FindUpdatesSetupTask() {
    downloadDir = BiliomiContainer.getParameters().getWorkingDir("Updates");
    installDir = new File("./");
  }

  @Override
  public void boot() {
    // Run updater on boot
    this.install();
  }

  @Override
  public void install() {
    try {
      Response<GithubRelease> response = githubApi.getLatestRelease(ghRepoOwner, ghRepoName);
      if (response.isOK()) {
        GithubRelease githubRelease = response.getData();

//        boolean isNewReleaseAvailable = githubApi.isNewRelease(versionInfo.getVersion(), githubRelease.getTagName());
        boolean isNewReleaseAvailable = githubApi.isNewRelease("v3.9.0", githubRelease.getTagName());
        if (isNewReleaseAvailable) {
          runUpdateInstaller(githubRelease);
        } else {
          logger.info("You are running the latest version of Biliomi, awesome!");
        }
      } else {
        throw new Exception(response.getRawData());
      }
    } catch (Exception e) {
      logger.error("Failed update", e);
    }
  }

  @Override
  public String getDisplayName() {
    return "Find updates on GitHub";
  }

  private void runUpdateInstaller(GithubRelease githubRelease) throws Exception {
    logger.info("A new version of Biliomi is available: " + versionInfo.getVersion() + " -> " + githubRelease.getTagName());
    logger.info("More information about this release can be found at " + githubRelease.getUrl());
    logger.info("I can perform this update for you, would you like me to go ahead and install the latest version?");
    logger.info("Type [Y] and [enter] to install the latest version, or hit [enter] to skip:");
    String input = consoleApi.waitForInput();

    if (!"Y".equals(input)) {
      // User does not wish to update, continue boot
      return;
    }

    logger.info("Downloading Biliomi " + githubRelease.getTagName() + "...");
    File archiveFile = downloadReleaseTar(githubRelease);

    logger.info("Removing old files...");
    removeOldInstall();

    logger.info("Unpacking new release...");
    TarArchiveUtils.extract(archiveFile);

    logger.info("Finalizing installation...");
    FileUtils.copyDirectory(new File(archiveFile.getParent(), "Biliomi v3"), installDir);
    FileUtils.deleteDirectory(downloadDir);

    logger.info("Biliomi has been updated successfully");
    logger.info("Note: The l10n directory has been replaced as well, if you were using a custom language I recommend you " +
        "download the latest and replace the l10n directory now");
    logger.info("Restart Biliomi whenever you are ready");
    BiliomiContainer.getContainer().shutdownNow(0);
  }

  private File downloadReleaseTar(GithubRelease githubRelease) throws Exception {
    GithubReleaseAsset asset = githubRelease.getAssets().stream()
        .filter(githubReleaseAsset -> DOWNLOAD_CONTENT_TYPE.equals(githubReleaseAsset.getContentType()))
        .findFirst()
        .orElse(null);

    if (asset == null) {
      throw new Exception("Could not find a TAR asset to download in the release");
    }

    // Create the download directory if it does not already exist.
    FileUtils.forceMkdir(downloadDir);

    URL url = new URL(asset.getDownloadUrl());
    File targetFile = new File(downloadDir, url.getFile());
    if (!targetFile.exists()) {
      FileUtils.copyURLToFile(url, targetFile);
    }
    return targetFile;
  }

  private void removeOldInstall() throws IOException {
    Collection<File> filesToDelete = FileUtils.listFiles(installDir, new String[]{"jar"}, false);

    filesToDelete.add(new File(installDir, "l10n"));
    filesToDelete.add(new File(installDir, "lib"));
    filesToDelete.add(new File(installDir, "default-config"));

    EStream.from(filesToDelete)
        .filter(File::exists)
        .forEach(FileUtils::forceDelete);
  }
}