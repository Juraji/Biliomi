package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.api.streamlabs.oauth.StreamLabsOAuthDirector;
import nl.juraji.biliomi.io.api.streamlabs.oauth.StreamLabsOAuthScope;
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
public class StreamLabsIntegrationSetupTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  private ConsoleApi console;

  @Inject
  private WebClient webClient;

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  @UserSetting("biliomi.integrations.streamLabs.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.streamLabs.consumerSecret")
  private String consumerSecret;

  @Override
  public void install() {
    if (StringUtils.isEmpty(consumerKey) || StringUtils.isEmpty(consumerSecret)) {
      logger.info("No OAuth information set for Stream Labs, skipping set up");
      return;
    }

    try {
      AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "streamlabs");

      if (StringUtils.isEmpty(token.getToken())) {
        installAccessToken(token);
      }
    } catch (Exception e) {
      logger.error("Failed setting up Stream Labs integration", e);
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup Stream Labs integration";
  }

  private void installAccessToken(AuthToken token) throws Exception {
    console.println();
    console.println("Biliomi can link to your Stream Labs account and watch various stats like donations, hosts etc.");
    console.print("Would you like this? [y/n]: ");

    if (!console.awaitYesNo()) {
      logger.info("Skipping Stream Labs integration setup");
      return;
    }

    StreamLabsOAuthDirector director = new StreamLabsOAuthDirector(consumerKey, consumerSecret, webClient);
    String authenticationUrl = director.getAuthenticationUri(
        StreamLabsOAuthScope.DONATIONS_READ,
        StreamLabsOAuthScope.SOCKET_TOKEN);

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
      throw new Exception("You need to be able to open a web browser in order to authenticate with Stream Labs.", e);
    }
  }
}
