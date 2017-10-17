package nl.juraji.biliomi.components.integrations.twitter.api.v1.model;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
public interface TweetListener extends StatusListener {

  @Override
  void onStatus(Status status);

  @Override
  void onStallWarning(StallWarning warning);

  @Override
  default void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    // Not required
  }

  @Override
  default void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    // Not required
  }

  @Override
  default void onScrubGeo(long userId, long upToStatusId) {
    // Not required
  }
}
