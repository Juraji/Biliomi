package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.CallbackResources;
import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.USCore;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.USDatabase;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.USTwitch;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.integrations.USIntegrationConsumer;
import nl.juraji.biliomi.utility.calculate.Numbers;
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
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.function.Consumer;

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
  private ConsoleApi consoleApi;

  @Inject
  private UserSettings userSettings;

  private final File configDir;
  private final File installDir;
  private final File coreYamlFile;
  private final BiliomiContainer container;

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

    try {
      logger.info("This is the first time you've started Biliomi, hi!");

      if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        logger.info("You will need to install Biliomi on a PC with a desktop environment, " +
            "since you will need a browser to set up OAuth authorizations");
      }

      logger.info("Biliomi will now setup and ask you a few questions, so don't go anywhere!");
      logger.info("Note that this setup will only run once! To run this again, delete the config directory and restart Biliomi");

      copyDefaultConfigDir();
      setupCoreUserSettings();
      setupDatabaseUserSettings();

      logger.info("For security reasons Biliomi is not shipped with any OAuth applicaiton keys");
      logger.info("This is why you need to create the applications on the appropriate platforms and supply the information here");
      logger.info("Note: The callback url will ALWAYS be: " + CallbackResources.REDIRECT_URI);
      setupTwitchUserSettings();
      setupSteamLabsConsumerUserSettings();
      setupTwitterConsumerUserSettings();
      setupSpotifyConsumerUserSettings();

      saveSettings();
      logger.info("Your settings have been saved. Restart Biliomi to apply your settings");
      logger.info("Would you ever wish to edit these settings open up " + coreYamlFile.getAbsolutePath() + " in your favorite text editor");
      container.shutdownNow(0);
    } catch (Exception e) {
      try {
        logger.error("Installation failed, reverting changes...", e);
        if (configDir.exists()) {
          FileUtils.deleteDirectory(configDir);
        }
      } catch (IOException e1) {
        logger.info("Failed reverting changes", e);
      } finally {
        container.shutdownInError();
      }
    }
  }

  @Override
  public String getDisplayName() {
    return "First Time Setup";
  }

  private void saveSettings() throws InvocationTargetException, IllegalAccessException, IOException {
    // Component settings are saved in separate files, the components tag should be null in core.yml
    userSettings.getBiliomi().setComponents(null);

    Yaml yamlInstance = new Yaml(new Constructor(UserSettings.class));
    String yamlString = yamlInstance.dumpAs(userSettings, new Tag("biliomi"), DumperOptions.FlowStyle.BLOCK);

    FileUtils.writeStringToFile(coreYamlFile, yamlString, "UTF-8", false);
  }

  private void copyDefaultConfigDir() throws IOException {
    File defaultConfigDir = new File(installDir, "default-config");
    logger.info("Copying " + defaultConfigDir.getAbsolutePath() + " to " + configDir.getAbsolutePath() + "...");
    FileUtils.copyDirectory(defaultConfigDir, configDir);
    logger.info("Default settings copied to " + configDir.getPath());
  }

  private void setupCoreUserSettings() throws Exception {
    USCore usCore = userSettings.getBiliomi().getCore();
    String input;

    logger.info("Would you like Biliomi to automatically check for updates on startup? [y/n]:");
    usCore.setCheckForUpdates(consoleApi.awaitYesNo());

    logger.info("I need your ISO 3166 country code");
    logger.info("You can find out the correct setting for you on https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2");
    logger.info("Enter your country code and press [enter]:");
    input = consoleApi.awaitInput(true);
    usCore.setCountryCode(input);

    userSettings.getBiliomi().setCore(usCore);
  }

  private void setupDatabaseUserSettings() throws Exception {
    USDatabase usDatabase = userSettings.getBiliomi().getDatabase();
    String input;

    logger.info("Biliomi is able to either connect to a MySQL database or use a local database");
    logger.info("Note that it is strongly recommended to use a MySQL databse, since the local database will be very slow");
    logger.info("Do you want to use the local database? [y/n]:");
    if (consoleApi.awaitYesNo()) {
      usDatabase.setUseH2Database(true);
    } else {
      usDatabase.setUseH2Database(false);

      logger.info("In order to connect to a MySQL database Biliomi needs some specifics");
      logger.info("Enter the database host address, e.g. localhost or myserverprovider.com:");
      input = consoleApi.awaitInput(true);
      usDatabase.setHost(input);

      logger.info("Enter the database port, or press [enter] to use the default: [3306]");
      input = consoleApi.awaitInput();
      if (StringUtils.isEmpty(input)) {
        usDatabase.setPort(3306);
      } else {
        usDatabase.setPort(Numbers.asNumber(input).toInteger());
      }

      logger.info("Enter the database name:");
      input = consoleApi.awaitInput(true);
      usDatabase.setDatabase(input);

      logger.info("Enter the username for Biliomi to use for the database:");
      input = consoleApi.awaitInput(true);
      usDatabase.setUsername(input);

      logger.info("Enter the password for Biliomi to use for the database, this is not hidden, make sure no one's looking:");
      input = consoleApi.awaitInput(true);
      usDatabase.setPassword(input);

      logger.info("Does Biliomi need to use SSL in order to connect to the database? [y/n]");
      usDatabase.setUsessl(consoleApi.awaitYesNo());
    }
  }

  private void setupTwitchUserSettings() throws Exception {
    USTwitch usTwitch = userSettings.getBiliomi().getTwitch();
    String input;

    logger.info("Create an application for Biliomi on Twitch");
    logger.info("Hit [enter] to open up https://www.twitch.tv/kraken/oauth2/clients/new and use the callback uri stated above");
    consoleApi.awaitInput();
    Desktop.getDesktop().browse(new URI("https://www.twitch.tv/kraken/oauth2/clients/new"));

    logger.info("When your done enter the Client ID presented to you:");
    input = consoleApi.awaitInput(true);
    usTwitch.setClientId(input);

    logger.info("Biliomi needs its own Twitch account in order to be able to talk in your chat, please create one of you haven't already");
    logger.info("Tho not nescessary, it's wise to also make Biliomi's Twitch account an editor on your channel");
    logger.info("Enter the Twitch account name of Biliomi is/will be in lowercase:");
    input = consoleApi.awaitInput(true);
    usTwitch.getLogin().setBotUsername(input);

    logger.info("Enter your own Twitch account name in lowercase:");
    input = consoleApi.awaitInput(true);
    usTwitch.getLogin().setChannelUsername(input);
  }

  private void setupSteamLabsConsumerUserSettings() throws Exception {
    USIntegrationConsumer streamLabs = userSettings.getBiliomi().getIntegrations().getStreamLabs();
    setupIntegration("Stream Labs", "https://streamlabs.com/dashboard/#/apps/register",
        streamLabs::setConsumerKey, streamLabs::setConsumerSecret);
  }

  private void setupTwitterConsumerUserSettings() throws Exception {
    USIntegrationConsumer twitter = userSettings.getBiliomi().getIntegrations().getTwitter();
    setupIntegration("Twitter", "https://apps.twitter.com/",
        twitter::setConsumerKey, twitter::setConsumerSecret);
  }

  private void setupSpotifyConsumerUserSettings() throws Exception {
    USIntegrationConsumer spotify = userSettings.getBiliomi().getIntegrations().getSpotify();
    setupIntegration("Spotify", "https://developer.spotify.com/my-applications",
        spotify::setConsumerKey, spotify::setConsumerSecret);
  }

  private void setupIntegration(String name, String appCreationUrl, Consumer<String> keySetter, Consumer<String> secretSetter) throws Exception {
    String input;

    logger.info("Do you want to setup the " + name + " integration? [y/n]");
    if (consoleApi.awaitYesNo()) {
      logger.info("You will need to setup a " + name + " application on your account");
      logger.info("Hit [enter] to open up " + appCreationUrl + " and use the callback uri stated above");

      consoleApi.awaitInput();
      Desktop.getDesktop().browse(new URI(appCreationUrl));

      logger.info("When your done enter your Client ID:");
      input = consoleApi.awaitInput(true);
      keySetter.accept(input);

      logger.info("Now enter your Client Secret:");
      input = consoleApi.awaitInput(true);
      secretSetter.accept(input);
    }
  }
}
