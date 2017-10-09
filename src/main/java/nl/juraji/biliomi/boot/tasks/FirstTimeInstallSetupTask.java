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
import nl.juraji.biliomi.utility.calculate.DeepMerge;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

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
  private final BiliomiContainer container;

  public FirstTimeInstallSetupTask() {
    AppParameters parameters = BiliomiContainer.getParameters();
    configDir = parameters.getConfigurationDir();
    installDir = parameters.getRootDir();
    container = BiliomiContainer.getContainer();
  }

  @Override
  public void install() {
    if (userSettings != null && configDir.exists()) {
      // Configuration dir exists, which means the user already copied it.
      return;
    }

    try {
      DeepMerge.initializePojo(userSettings);

      logger.info("This is the first time you've started Biliomi, hi!");
      logger.info("Biliomi will now setup and ask you a few questions, so don't go anywhere!");
      logger.info("Note that this setup will only run once! To run this again, delete the config directory and restart Biliomi");

      copyDefaultConfigDir();
      setupCoreUserSettings();
      setupDatabaseUserSettings();

      logger.info("For security reasons Biliomi is not shipped with any OAuth applicaiton keys");
      logger.info("This is why you need to create the applications on the appropriate platforms and supply the information here");
      setupTwitchUserSettings();

      logger.info("The following questions are optional, you can skip these on by one by pressing [enter] without any other input");
      setupSteamLabsConsumerUserSettings();
      setupTwitterConsumerUserSettings();
      setupSpotifyConsumerUserSettings();

      saveSettings();

      logger.info("Restart Biliomi to apply your settings");
      container.shutdownNow(0);
    } catch (Exception e) {
      try {
        logger.error("Installation failed, reverting changes...", e);
        deleteCopiedConfig();
      } catch (IOException e1) {
        logger.info("Failed reverting changes", e);
      } finally {
        container.shutdownInError();
      }
    }
  }

  private void saveSettings() throws InvocationTargetException, IllegalAccessException, IOException {
    // Component settings are saved in separate files, the components tag should be null in core.yml
    userSettings.getBiliomi().setComponents(null);

    Yaml yamlInstance = new Yaml(new Constructor(UserSettings.class));
    String yamlString = yamlInstance.dumpAs(userSettings, new Tag("biliomi"), DumperOptions.FlowStyle.BLOCK);

    File coreYamlFile = new File(configDir, "core.yml");
    FileUtils.writeStringToFile(coreYamlFile, yamlString, "UTF-8", false);
  }

  private void copyDefaultConfigDir() throws IOException {
    File defaultConfigDir = new File(installDir, "default-config");
    logger.info("Copying " + defaultConfigDir.getPath() + " to " + configDir.getPath() + "...");
    FileUtils.copyDirectory(defaultConfigDir, configDir);
    logger.info("Default settings copied to " + configDir.getPath());
  }

  private void deleteCopiedConfig() throws IOException {
    if (configDir.exists()) {
      FileUtils.deleteDirectory(configDir);
    }
  }

  private void setupCoreUserSettings() throws ExecutionException, InterruptedException {
    USCore usCore = userSettings.getBiliomi().getCore();
    String input;

    logger.info("Would you like Biliomi to automatically check for updates on startup? [Y/N]:");
    logger.info("Note: The callback url will ALWAYS be: " + CallbackResources.REDIRECT_URI);
    input = consoleApi.awaitInput(true);
    usCore.setCheckForUpdates("Y".equalsIgnoreCase(input));

    logger.info("I need your ISO 3166 country code");
    logger.info("You can find out the correct setting for you on https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2");
    logger.info("Enter your country code and press [enter]:");
    input = consoleApi.awaitInput(true);
    usCore.setCountryCode(input);

    userSettings.getBiliomi().setCore(usCore);
  }

  private void setupDatabaseUserSettings() throws ExecutionException, InterruptedException {
    USDatabase usDatabase = userSettings.getBiliomi().getDatabase();
    String input;

    logger.info("Biliomi is able to either connect to a MySQL database or use a local database");
    logger.info("Note that it is strongly recommended to use a MySQL databse, since the local database will be very slow");
    logger.info("Do you want to use the local database? [Y/N]:");
    input = consoleApi.awaitInput(true);
    if (input.equalsIgnoreCase("Y")) {
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

      logger.info("Does Biliomi need to use SSL in order to connect to the database? [Y/N]");
      input = consoleApi.awaitInput(true);
      usDatabase.setUsessl("Y".equalsIgnoreCase(input));
    }
  }

  private void setupTwitchUserSettings() throws ExecutionException, InterruptedException {
    USTwitch usTwitch = userSettings.getBiliomi().getTwitch();
    String input;

    logger.info("Create an application for Biliomi on Twitch");
    logger.info("You can do this by heading over to https://www.twitch.tv/kraken/oauth2/clients/new and use the callback uri stated above");
    logger.info("When your done enter the Client ID presented to you:");
    input = consoleApi.awaitInput(true);
    usTwitch.setClientId(input);

    logger.info("Now I need to know what the Twitch account name of Biliomi is/will be:");
    input = consoleApi.awaitInput(true);
    usTwitch.getLogin().setBotUsername(input);

    logger.info("Enter your own Twitch account name:");
    input = consoleApi.awaitInput(true);
    usTwitch.getLogin().setChannelUsername(input);
  }

  private void setupSteamLabsConsumerUserSettings() {
    // Todo: Implement me
  }

  private void setupTwitterConsumerUserSettings() {
    // Todo: Implement me
  }

  private void setupSpotifyConsumerUserSettings() {
    // Todo: Implement me
  }
}
