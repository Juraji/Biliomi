package nl.juraji.biliomi.io.api.twitter.v1.model;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.TwitterStream;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
public class TweetStreamFuture {
  private final TwitterStream stream;
  private boolean isTracking = true;

  public TweetStreamFuture(TwitterStream stream) {
    this.stream = stream;

    stream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {
      @Override
      public void onConnect() {
        isTracking = true;
      }

      @Override
      public void onDisconnect() {
        isTracking = false;
      }

      @Override
      public void onCleanUp() {

      }
    });
  }

  public boolean isTracking() {
    return isTracking;
  }

  public void stopStream() {
    stream.shutdown();
  }
}
