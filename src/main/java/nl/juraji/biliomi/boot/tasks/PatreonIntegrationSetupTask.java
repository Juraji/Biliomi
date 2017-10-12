package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.api.patreon.oauth.PatreonOAuthFlowDirector;
import nl.juraji.biliomi.io.api.patreon.oauth.PatreonOAuthScope;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 10)
public class PatreonIntegrationSetupTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  private ConsoleApi console;

  @Inject
  private WebClient webClient;

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  @UserSetting("biliomi.integrations.patreon.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.patreon.consumerSecret")
  private String consumerSecret;

  @Override
  public void install() {
    if (StringUtils.isEmpty(consumerKey) || StringUtils.isEmpty(consumerSecret)) {
      logger.info("No OAuth information set for Stream Labs, skipping set up");
      return;
    }

    try {
      AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "patreon");

      if (StringUtils.isEmpty(token.getToken())) {
        installAccessToken(token);
      }
    } catch (Exception e) {
      logger.error("Failed setting up Patreon integration", e);
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup PArteon integration";
  }

  private void installAccessToken(AuthToken token) throws Exception {
    console.println();
    console.println("Biliomi can link to your Patreon in order to be able to respond to new pledges.");
    console.print("Would you like this? [y/n]: ");

    if (!console.awaitYesNo()) {
      logger.info("Skipping Patreon integration setup");
      return;
    }

    PatreonOAuthFlowDirector director = new PatreonOAuthFlowDirector(consumerKey, consumerSecret, webClient);
    String authenticationUrl = director.getAuthenticationUri(
        PatreonOAuthScope.USERS,
        PatreonOAuthScope.MY_CAMPAIGN,
        PatreonOAuthScope.PLEDGES_TO_ME);

    try {
      Desktop.getDesktop().browse(new URI(authenticationUrl));
      boolean authSuccess = director.awaitAccessToken();

      //noinspection Duplicates
      if (authSuccess) {
        token.setToken(director.getAccessToken());
        token.setRefreshToken(director.getRefreshToken());
        token.setIssuedAt(DateTime.now());
        token.setTimeToLive(director.getTimeToLive());
      } else {
        throw new Exception(director.getAuthenticationError());
      }

      authTokenDao.save(token);
    } catch (IOException e) {
      throw new Exception("You need to be able to open a web browser in order to authenticate with Patreon.", e);
    }
  }
}
