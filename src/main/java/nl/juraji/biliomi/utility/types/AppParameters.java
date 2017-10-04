package nl.juraji.biliomi.utility.types;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Joiner;
import nl.juraji.biliomi.BiliomiContainer;

import java.io.File;

/**
 * Created by Juraji on 12-5-2017.
 * Biliomi v3
 */
@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class AppParameters {

  @Parameter(names = {"--configdir", "-c"}, description = "Path to the component configuration directory")
  private String configurationDir = "./config";

  @Parameter(names = {"--langdir", "-l"}, description = "Base directory containing language files")
  private String languageBaseDir = "./l10n";

  @Parameter(names = {"--workingdir", "-w"}, description = "The base directory for component files (e.g. exports, etc.)")
  private String workingDir = "./data";

  @Parameter(names = {"--casteroauth"}, description = "When supplied this token is used, instead of the OAuth flow during installation")
  private String casterOAuth = null;

  @Parameter(names = {"--botoauth"}, description = "When supplied this token is used, instead of the OAuth flow during installation")
  private String botOAuth = null;

  @Parameter(names = "--resetauth", description = "Reset authentication for Twitch and ALL other integrations")
  private boolean resetAuth = false;

  @Parameter(names = {"--debug"}, description = "Start Biliomi in debug mode, more information is shown in the console")
  private boolean debugMode = false;

  public AppParameters(String[] args) {
    try {
      JCommander.newBuilder()
          .programName("java -jar biliomi-x.x.x.jar")
          .addObject(this)
          .build()
          .parse(args);
    } catch (ParameterException e) {
      // Invalid parameters were passed, print the usage and exit immediately (prevents Biliomi from bootstrapping)
      e.usage();
      BiliomiContainer.getContainer().shutdownInError();
    }
  }

  public File getConfigurationDir() {
    return new File(configurationDir);
  }

  public File getLanguageBaseDir() {
    return new File(languageBaseDir);
  }

  public File getWorkingDir(String... path) {
    File file = new File(workingDir, Joiner.on("/").join(path));

    //noinspection ResultOfMethodCallIgnored
    file.mkdirs();

    return file;
  }

  public String getCasterOAuth() {
    return casterOAuth;
  }

  public String getBotOAuth() {
    return botOAuth;
  }

  public boolean isResetAuth() {
    return resetAuth;
  }

  public boolean isDebugMode() {
    return debugMode;
  }
}
