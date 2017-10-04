package nl.juraji.biliomi.io.api.twitter.v1;

import nl.juraji.biliomi.io.api.twitter.v1.model.TweetListener;
import nl.juraji.biliomi.io.api.twitter.v1.model.TweetStreamFuture;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Set;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
public class TwitterApiImpl implements TwitterApi {

  @Inject
  @UserSetting("biliomi.integrations.twitter.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.twitter.consumerSecret")
  private String consumerSecret;

  @Inject
  private AuthTokenDao authTokenDao;

  private AccessToken accessToken;
  private Twitter twitter;

  @PostConstruct
  private void initTwitterApiImpl() {
    AuthToken authToken = authTokenDao.get(TokenGroup.INTEGRATIONS, "twitter");

    if (authToken.getToken() != null) {
      accessToken = new AccessToken(authToken.getToken(), authToken.getSecret());

      twitter = new TwitterFactory().getInstance();
      twitter.setOAuthConsumer(consumerKey, consumerSecret);
      twitter.setOAuthAccessToken(accessToken);
    }
  }

  @Override
  public void postTweet(String message) throws Exception {
    assertServiceAvailable();
    twitter.updateStatus(message);
  }

  @Override
  public TweetStreamFuture listenForTweets(TweetListener tweetListener, Set<String> keywords) throws Exception {
    assertServiceAvailable();
    // Create a new instance for every listener, so the caller can stop this specific instance if needed
    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
    twitterStream.setOAuthAccessToken(accessToken);
    twitterStream.addListener(tweetListener);
    twitterStream.filter(keywords.toArray(new String[]{}));

    return new TweetStreamFuture(twitterStream);
  }

  private void assertServiceAvailable() throws UnavailableException {
    if (accessToken == null || twitter == null) {
      throw new UnavailableException();
    }
  }
}
