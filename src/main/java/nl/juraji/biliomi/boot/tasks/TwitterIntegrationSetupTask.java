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
import java.util.concurrent.ExecutionException;

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
  private ConsoleApi consoleApi;

  @Inject
  private AuthTokenDao authTokenDao;

  @Override
  public void install() {
    if (StringUtils.isEmpty(consumerKey) || StringUtils.isEmpty(consumerSecret)) {
      logger.info("No OAuth information set for Twitter, skipping set up");
      return;
    }

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
      } catch (InterruptedException | ExecutionException e) {
        logger.error("Failed setting up Twitter integration", e);
      }
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup Twitter integration";
  }

  private void installTwitterToken(AuthToken token) throws TwitterException, ExecutionException, InterruptedException {
    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(consumerKey, consumerSecret);

    RequestToken requestToken = twitter.getOAuthRequestToken();
    logger.info("Open the following URL and grant access to your account:");
    logger.info(requestToken.getAuthorizationURL());
    logger.info("Enter the pin presented to you and hit [enter], or simply hit [enter] to skip Twitter integration:");
    String pinInput = consoleApi.awaitInput();

    if (StringUtils.isNotEmpty(pinInput)) {
      AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pinInput);
      token.setToken(accessToken.getToken());
      token.setSecret(accessToken.getTokenSecret());
      token.setUserId(String.valueOf(accessToken.getUserId()));
      authTokenDao.save(token);
      logger.info("Successfully set up Twitter integration");
    } else {
      logger.info("Canceled Twitter integration setup");
    }
  }
}
