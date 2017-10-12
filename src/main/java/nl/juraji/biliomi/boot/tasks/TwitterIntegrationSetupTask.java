package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.awt.*;
import java.net.URI;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
@Default
@SetupTaskPriority(priority = 10)
public class TwitterIntegrationSetupTask implements SetupTask {

  @Inject
  @UserSetting("biliomi.integrations.twitter.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.twitter.consumerSecret")
  private String consumerSecret;

  @Inject
  private Logger logger;

  @Inject
  private ConsoleApi console;

  @Inject
  private AuthTokenDao authTokenDao;

  @Override
  public void install() {
    if (StringUtils.isEmpty(consumerKey) || StringUtils.isEmpty(consumerSecret)) {
      logger.info("No consumer information set for Twitter");
      return;
    }

    AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "twitter");

    if (StringUtils.isEmpty(token.getToken())) {
      try {
        installTwitterToken(token);
      } catch (TwitterException e) {
        logger.error("An exception occurred while authorizing Twitter", e);
      } catch (Exception e) {
        logger.error("Failed setting up Twitter integration", e);
      }
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup Twitter integration";
  }

  private void installTwitterToken(AuthToken token) throws Exception {
    console.println();
    console.print("Would you like to set up Twitter integration now? [y/n]: ");
    if (!console.awaitYesNo()) {
      logger.info("Canceled Twitter integration setup");
      return;
    }

    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(consumerKey, consumerSecret);

    RequestToken requestToken = twitter.getOAuthRequestToken();

    console.println();
    console.print("Hit [enter] to open up " + requestToken.getAuthorizationURL() + " and authorize on Twitter.");
    Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));

    console.print("Enter the pin presented to you and hit [enter]: ");
    String pinInput = console.awaitInput(true);

    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pinInput);
    token.setToken(accessToken.getToken());
    token.setSecret(accessToken.getTokenSecret());
    token.setUserId(String.valueOf(accessToken.getUserId()));
    authTokenDao.save(token);

    console.println();
    console.println("Successfully set up Twitter integration");
    console.println();
  }
}
