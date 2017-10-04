package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

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
      } catch (ExecutionException | InterruptedException e) {
        logger.error("Failed setting up Steam integration", e);
      }
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup Steam integration";
  }

  private void installSteamToken() throws ExecutionException, InterruptedException {
    logger.info("Open the following URL, log in to the Steam website and fill out the form to retrieve your API key");
    logger.info(getApiKeyUrl);
    logger.info("Enter your Steam API key and hit [enter], or simply hit [enter] to skip Steam integration:");
    String apiKey = consoleApi.waitForInput();

    if (StringUtils.isEmpty(apiKey)) {
      logger.info("Canceled Steam integration setup");
      return;
    }

    userToken.setToken(apiKey);

    logger.info("Note: You can use " + getUserIdUrl + " to find out your user id");
    logger.info("Enter your Steam user id and hit [enter], or hit [enter] now to skipt Steam integration:");
    String userId = consoleApi.waitForInput();

    if (StringUtils.isEmpty(userId)) {
      logger.info("Canceled Steam integration setup");
      return;
    }

    userToken.setUserId(userId);
    authTokenDao.save(userToken);
    logger.info("Successfully set up Steam integration");
  }
}
