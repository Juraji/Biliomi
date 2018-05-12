package nl.juraji.biliomi.components.integrations.twitter.api.v1;

import nl.juraji.biliomi.components.integrations.twitter.api.v1.model.TweetListener;
import nl.juraji.biliomi.components.integrations.twitter.api.v1.model.TweetStreamFuture;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;

import java.util.Set;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
public interface TwitterApi {

    /**
     * Post an update to the authorized Twitter account
     *
     * @param message The message to post
     * @throws UnavailableException When the Twitter OAuth token has not been installed
     * @throws Exception            When an error occurres during communication with Twitter
     */
    void postTweet(String message) throws Exception;

    /**
     * Set a listener for tweets
     *
     * @param tweetListener A listener to intercept tweets
     * @param keywords      Specific keywords to listen to
     */
    TweetStreamFuture listenForTweets(TweetListener tweetListener, Set<String> keywords) throws Exception;
}
