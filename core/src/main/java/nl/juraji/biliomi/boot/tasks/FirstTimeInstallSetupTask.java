package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.config.core.YamlCoreSettings;
import nl.juraji.biliomi.config.core.biliomi.USCore;
import nl.juraji.biliomi.config.core.biliomi.USDatabase;
import nl.juraji.biliomi.config.core.biliomi.USTwitch;
import nl.juraji.biliomi.config.core.biliomi.database.USMySQL;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.web.oauth.OAuthFlow;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.types.AppParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 0)
public class FirstTimeInstallSetupTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  private ConsoleApi console;

  @Inject
  private YamlCoreSettings yamlCoreSettings;

  private final File configDir;
  private final File installDir;
  private final File coreYamlFile;
  private final BiliomiContainer container;
  private Thread setupCancelHook;

  public FirstTimeInstallSetupTask() {
    AppParameters parameters = BiliomiContainer.getParameters();
    configDir = parameters.getConfigurationDir();
    installDir = parameters.getRootDir();
    coreYamlFile = new File(configDir, "core.yml");
    container = BiliomiContainer.getContainer();
  }

  @Override
  public void install() {
    if (configDir.exists()) {
      // Configuration dir exists, which means the user already copied it.
      return;
    }

    registerSetupCancelHook();

    try {
      console.println();
      console.println("This is the first time you've started Biliomi, hi!.");
      console.println();

      if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        console.println("You will need to install Biliomi on a PC with a desktop environment, " +
            "since you will need a browser to set up OAuth authorizations.");
        container.shutdownNow(0);
      }

      console.println("Biliomi will now setup and ask you a few questions, so don't go anywhere!");
      console.println("Note that this setup will only run once! To run this again, delete the config directory and restart Biliomi.");
      console.println();

      copyDefaultConfigDir();
      setupCoreUserSettings();
      setupDatabaseUserSettings();
      setupTwitchUserSettings();
      setupTwitchWebhookSettings();

      saveSettings();
      console.println("Your settings have been saved. Restart Biliomi to apply your settings.");
      console.println();
      console.println("Would you ever wish to edit these settings open up " + coreYamlFile.getAbsolutePath() + " in your favorite text editor.");

      File integrationsYamlFile = new File(configDir, "integrations.yml");
      console.println("If you want to use integrations like Stream Labs or Twitter open up " + integrationsYamlFile.getAbsolutePath() +
          " in your favorite text editor and follow the instructions there.");
      console.println();

      removeSetupCancelHook();
      container.shutdownNow(0);
    } catch (Exception e) {
      container.shutdownInError();
    }
  }

  @Override
  public String getDisplayName() {
    return "First Time Setup";
  }

  private void saveSettings() throws IOException {
    // Component and integration settings are saved in separate files, these tags should be null in core.yml

    Yaml yamlInstance = new Yaml(new Constructor(YamlCoreSettings.class));
    String yamlString = yamlInstance.dumpAs(yamlCoreSettings, new Tag("nl/juraji/biliomi"), DumperOptions.FlowStyle.BLOCK);

    FileUtils.writeStringToFile(coreYamlFile, yamlString, "UTF-8", false);
  }

  private void copyDefaultConfigDir() throws IOException {
    File defaultConfigDir = new File(installDir, "default-config");
    console.println("Copying " + defaultConfigDir.getAbsolutePath() + " to " + configDir.getAbsolutePath() + "...");
    FileUtils.copyDirectory(defaultConfigDir, configDir);
    console.println("Default settings copied to " + configDir.getPath() + ".");
    console.println();
  }

  private void setupCoreUserSettings() throws Exception {
    USCore usCore = yamlCoreSettings.getBiliomi().getCore();
    String input;

    console.print("Would you like Biliomi to automatically check for updates on startup? [y/n]: ");
    usCore.setCheckForUpdates(console.awaitYesNo());
    console.println();

    console.println("I need your ISO 3166 country code.");
    console.println("You can find out the correct setting for you on https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2.");
    console.print("Enter your country code and press [enter]: ");
    input = console.awaitInput(true);
    usCore.setCountryCode(input);
    console.println();
  }

  private void setupDatabaseUserSettings() throws Exception {
    USDatabase usDatabase = yamlCoreSettings.getBiliomi().getDatabase();
    String input;

    console.println("Biliomi is able to either connect to a MySQL database or use a local database.");
    console.println("Note that it is strongly recommended to use a MySQL databse, since the local database will be very slow.");
    console.print("Do you want to use the local database? [y/n]: ");
    if (console.awaitYesNo()) {
      usDatabase.setUseH2Database(true);
      console.println();
    } else {
      usDatabase.setUseH2Database(false);
      USMySQL usMySQL = usDatabase.getMySQL();
      console.println();

      console.println("In order to connect to a MySQL database Biliomi needs some specifics.");
      console.print("Enter the database host address, e.g. localhost or myserverprovider.com: ");
      usMySQL.setHost(console.awaitInput(true));
      console.println();

      console.print("Enter the database port, or press [enter] to use the default [3306]: ");
      input = console.awaitInput();
      if (StringUtils.isEmpty(input)) {
        usMySQL.setPort(3306);
      } else {
        usMySQL.setPort(NumberConverter.asNumber(input).toInteger());
      }
      console.println();

      console.print("Enter the database name: ");
      usMySQL.setDatabase(console.awaitInput(true));
      console.println();

      console.print("Enter the username for Biliomi to use for the database: ");
      input = console.awaitInput(true);
      usMySQL.setUsername(input);

      console.print("Enter the password for Biliomi to use for the database, this is not hidden, make sure no one's looking: ");
      input = console.awaitInput(true);
      usMySQL.setPassword(input);

      console.print("Does Biliomi need to use SSL in order to connect to the database? [y/n]: ");
      usMySQL.setUsessl(console.awaitYesNo());
      console.println();
    }
  }

  private void setupTwitchUserSettings() throws Exception {
    USTwitch usTwitch = yamlCoreSettings.getBiliomi().getTwitch();
    String input;

    console.println("Create an application for Biliomi on Twitch.");
    console.println("For security reasons Biliomi is not shipped with any OAuth application keys.");
    console.println("This is why you need to create an application on Twitch and supply the information here.");
    console.println("Note: The oauth callback url will be: " + OAuthFlow.REDIRECT_URI);
    console.print("Hit [enter] to open up https://glass.twitch.tv/console/apps and use the callback uri stated above.");
    console.awaitInput();
    Desktop.getDesktop().browse(new URI("https://glass.twitch.tv/console/apps"));
    console.println();

    console.print("When your done enter the Client ID presented to you: ");
    input = console.awaitInput(true);
    usTwitch.setClientId(input);
    console.println();

    console.print("Now generate a new client secret and enter it here: ");
    input = console.awaitInput(true);
    usTwitch.setClientSecret(input);
    console.println();

    console.println("Biliomi needs its own Twitch account in order to be able to talk in your chat, please create one of you haven't already.");
    console.println("Tho not nescessary, it's wise to also make Biliomi's Twitch account an editor on your channel.");
    console.print("Enter the Twitch account name of Biliomi in lowercase: ");
    input = console.awaitInput(true);
    usTwitch.getLogin().setBotUsername(input);
    console.println();

    console.print("Enter your own Twitch account name in lowercase: ");
    input = console.awaitInput(true);
    usTwitch.getLogin().setChannelUsername(input);
    console.println();
  }

  private void setupTwitchWebhookSettings() throws Exception {
    USTwitch usTwitch = yamlCoreSettings.getBiliomi().getTwitch();
    String callbackUrl;
    String input;

    console.println("Biliomi uses Twitch's webhooks in order to receive notifications about followers, subscribers, channel status and more.");
    console.println("In order for this to work proper you will have to setup port forwarding in your router (if applicable) and find out what your public (internet) IP-address is.");
    console.println("You can find out what your public IP-address is using https://wtfismyip.com.");
    console.print("Would you like me to open this website for you? [y/n]:");
    boolean yes = console.awaitYesNo();
    if (yes) {
      Desktop.getDesktop().browse(new URI("https://wtfismyip.com"));
    }
    console.println();

    console.println("Please enter your public IP-address: ");
    input = console.awaitInput(true);
    callbackUrl = "http://" + input;
    console.println();

    console.println("Please enter a free port number [30001]: ");
    input = console.awaitInput();
    if (StringUtils.isEmpty(input)) {
      callbackUrl += ":30001";
    } else {
      callbackUrl += ":" + input;
    }

    usTwitch.setWebhookCallbackUrl(callbackUrl);
    console.println();

    console.println("Please do not forget to forward this port to the machine running Biliomi in your router.");
    console.println("Refer to your router's manual to find out how to do this.");
    console.println("You can always open up config/core.yml and edit the address and port.");
    console.println();
  }

  private void registerSetupCancelHook() {
    setupCancelHook = new Thread(() -> {
      try {
        console.println(); // In case a question is on the current line
        logger.error("Installation canceled, reverting changes...");
        if (configDir.exists()) {
          FileUtils.deleteDirectory(configDir);
        }
      } catch (IOException e) {
        logger.info("Failed reverting changes", e);
      }
    });

    Runtime.getRuntime().addShutdownHook(setupCancelHook);
  }

  private void removeSetupCancelHook() {
    Runtime.getRuntime().removeShutdownHook(setupCancelHook);
  }
}
