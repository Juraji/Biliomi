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
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.factories.archives.TarArchiveUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 1)
public class FindUpdatesSetupTask implements SetupTask {
  private static final String DOWNLOAD_CONTENT_TYPE = "application/x-gzip";

  @Inject
  @AppDataValue("github.biliomi.repository.owner")
  private String ghRepoOwner;

  @Inject
  @AppDataValue("github.biliomi.repository.name")
  private String ghRepoName;

  @Inject
  @AppDataValue("biliomi.autoupdate.directories")
  private String installerDirectories;

  @Inject
  @UserSetting("biliomi.core.checkForUpdates")
  private String isCheckForUpdates;

  @Inject
  private Logger logger;

  @Inject
  private VersionInfo versionInfo;

  @Inject
  private GithubApi githubApi;

  @Inject
  private ConsoleApi console;

  private final File downloadDir;
  private final File installDir;

  public FindUpdatesSetupTask() {
    downloadDir = BiliomiContainer.getParameters().getWorkingDir("Updates");
    installDir = new File("./");
  }

  @Override
  public void boot() {
    // Run updater on boot
    this.update();
  }

  @Override
  public void install() {
    // Do nothing
  }

  @Override
  public void update() {
    if (!"true".equals(isCheckForUpdates)) {
      return;
    }

    try {
      Response<GithubRelease> response = githubApi.getLatestRelease(ghRepoOwner, ghRepoName);
      if (response.isOK()) {
        GithubRelease githubRelease = response.getData();

        if (!githubRelease.isDraft() && !githubRelease.isPrerelease() && isNonSemVer(versionInfo.getVersion(), githubRelease.getTagName())) {
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
    console.println();
    console.println("A new version of Biliomi is available: " + versionInfo.getVersion() + " -> " + githubRelease.getTagName() + ".");
    console.println("More information about this release can be found at " + githubRelease.getUrl() + ".");
    console.print("I can perform this update for you, would you like me to go ahead and install the latest version? [y/n]: ");
    if (!console.awaitYesNo()) {
      // User does not wish to update, continue boot
      return;
    }

    console.println();
    console.println("Downloading Biliomi " + githubRelease.getTagName() + "...");
    File archiveFile = downloadReleaseTar(githubRelease);

    console.println("Removing old files...");
    removeOldInstall();

    console.println("Unpacking new release...");
    TarArchiveUtils.extract(archiveFile);

    console.println("Finalizing installation...");
    FileUtils.copyDirectory(new File(archiveFile.getParent(), "Biliomi v3"), installDir);
    FileUtils.deleteDirectory(downloadDir);

    console.println();
    console.println("Biliomi has been updated successfully");
    console.println();

    String coreConfigFile = BiliomiContainer.getParameters().getConfigurationDir().getAbsolutePath() + "/core.yml";
    console.println("The following actions are up to you:");
    console.println("- The l10n directory has been replaced, if you were using a custom language I recommend you download the latest and replace the l10n directory.");
    console.println("- Check if there are any new settings you need to copy over from the default-config.");
    console.println("- Set the update mode to UPDATE in " + coreConfigFile + " in order to run database updates.");
    console.println();

    console.println("Restart Biliomi whenever you are ready...");
    console.println();
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
    List<File> directories = Arrays.stream(installerDirectories.split(","))
        .map(s -> new File(installDir, s))
        .collect(Collectors.toList());

    filesToDelete.addAll(directories);

    EStream.from(filesToDelete)
        .filter(File::exists)
        .forEach(FileUtils::forceDelete);
  }

  private boolean isNonSemVer(String currentVersionTag, String newVersionTag) {
    if (currentVersionTag == null || newVersionTag == null) {
      return false;
    }

    // Snapshot releases should always be treated as an older version
    if (currentVersionTag.contains("SNAPSHOT")) {
      return true;
    }

    // Turns both versions in to arrays like {MAJOR, MINOR, MICRO}
    String[] cvs = currentVersionTag.replaceAll("v([0-9.]+).*", "$1").split("\\.");
    String[] nvs = newVersionTag.replaceAll("v([0-9.]+).*", "$1").split("\\.");

    // Starts at the second element, since major versions are never updateable
    for (int i = 1; i < 3; i++) {
      if (Integer.valueOf(cvs[i]) >= Integer.valueOf(nvs[i])) {
        return false;
      }
    }

    return true;
  }
}
