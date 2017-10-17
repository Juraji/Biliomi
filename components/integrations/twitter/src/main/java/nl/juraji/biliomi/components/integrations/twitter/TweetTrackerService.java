package nl.juraji.biliomi.components.integrations.twitter;

import nl.juraji.biliomi.components.integrations.twitter.api.v1.TwitterApi;
import nl.juraji.biliomi.components.integrations.twitter.api.v1.model.TweetListener;
import nl.juraji.biliomi.components.integrations.twitter.api.v1.model.TweetStreamFuture;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.model.integrations.twitter.TwitterSettings;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.types.Restartable;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.logging.log4j.Logger;
import twitter4j.StallWarning;
import twitter4j.Status;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
@Default
public class TweetTrackerService implements Restartable {

  @Inject
  private Logger logger;

  @Inject
  private TwitterApi twitterApi;

  @Inject
  private SettingsService settingsService;

  @Inject
  private TemplateDao templateDao;

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  private ChatService chat;
  private TweetStreamFuture tweetStreamFuture;

  @Override
  public void start() {
    stop(); // Run stop to be sure to have stopped any current listening stream
    TwitterSettings settings = settingsService.getSettings(TwitterSettings.class);
    if (!settings.getTrackedKeywords().isEmpty()) {
      try {
        AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "twitter");
        Template template = templateDao.getByKey(TwitterComponent.TWITTER_TWEET_FOUND_TEMPLATE_ID);
        Long filteredUserId = Numbers.asNumber(token.getUserId()).withDefault(0).toLong();

        assert template != null; // Template cannot be null as it is initialized on install/update
        TweetListener listener = createTweetListener(template.getTemplate(), filteredUserId);

        tweetStreamFuture = twitterApi.listenForTweets(listener, settings.getTrackedKeywords());
        logger.info(Templater.template("Listening on Twitter for tweets containing the following words: {{words}}")
            .add("words", settings.getTrackedKeywords())
            .apply());
      } catch (Exception e) {
        logger.error("Failed starting TweetTrackerService", e);
      }
    }
  }

  @Override
  public void stop() {
    if (tweetStreamFuture != null) {
      tweetStreamFuture.stopStream();
      tweetStreamFuture = null;
    }
  }


  public String getStatus() {
    if (tweetStreamFuture != null && tweetStreamFuture.isTracking()) {
      TwitterSettings settings = settingsService.getSettings(TwitterSettings.class);
      return Templater.template("Tracking tweets containing: {{words}}")
          .add("words", settings::getTrackedKeywords)
          .apply();
    }

    return "Tracking is disabled";
  }

  /**
   * Create a new TweetListener that will post the set Template
   * in the chat.
   *
   * @param filteredUserId      A Twitter user id to filter out of incoming tweets
   * @param chatMessageTemplate The template to post in the chat when a new tweet is matched
   * @return A new instance of TweetListener
   */
  private TweetListener createTweetListener(String chatMessageTemplate, long filteredUserId) {
    return new TweetListener() {

      @Override
      public void onStatus(Status status) {
        if (filteredUserId != status.getUser().getId()) {
          chat.say(Templater.template(chatMessageTemplate)
              .add("username", status.getUser()::getScreenName));
        }
      }

      @Override
      public void onStallWarning(StallWarning warning) {
        logger.warn("Biliomi can't keep up with the stream of tweets, make sure your tracking" +
            " words are not too common or Biliomi will get disconnected and stop listening for tweets!");
      }

      @Override
      public void onException(Exception e) {
        logger.error("And error occurred while listening to tweets", e);
      }
    };
  }
}
