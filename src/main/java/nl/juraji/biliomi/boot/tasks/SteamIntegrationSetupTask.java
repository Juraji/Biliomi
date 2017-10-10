package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.awt.*;
import java.net.URI;

/**
 * Created by Juraji on 15-9-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 10)
public class SteamIntegrationSetupTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  private ConsoleApi consoleApi;

  @Inject
  @AppDataValue("steam.api.uris.getapikey")
  private String getApiKeyUrl;

  @Inject
  @AppDataValue("steam.api.uris.getuserid")
  private String getUserIdUrl;

  @Inject
  private AuthTokenDao authTokenDao;
  private AuthToken userToken;

  @Override
  public void install() {
    userToken = authTokenDao.get(TokenGroup.INTEGRATIONS, "steam");

    if (userToken.getToken() == null) {
      try {
        installSteamToken();
      } catch (Exception e) {
        logger.error("Failed setting up Steam integration", e);
      }
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup Steam integration";
  }

  private void installSteamToken() throws Exception {
    logger.info("Would you like to set up Steam integration now? [Y/N]");
    if (!consoleApi.awaitYesNo()) {
      logger.info("Canceled Steam integration setup");
      return;
    }

    logger.info("You will need a Steam Api key");
    logger.info("Hit [enter] to open up " + getApiKeyUrl + " fill out the form");
    Desktop.getDesktop().browse(new URI(getApiKeyUrl));

    logger.info("Enter your Steam API key and hit [enter]:");
    String apiKey = consoleApi.awaitInput(true);
    userToken.setToken(apiKey);

    logger.info("Note: You can use " + getUserIdUrl + " to find out your user id");
    logger.info("Enter your Steam user id and hit [enter]:");
    String userId = consoleApi.awaitInput(true);
    userToken.setUserId(userId);

    authTokenDao.save(userToken);
    logger.info("Successfully set up Steam integration");
  }
}
