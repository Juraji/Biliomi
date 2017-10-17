package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.integrations.USIntegrationConsumer;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USIntegrations {
  private USIntegrationConsumer streamLabs;
  private USIntegrationConsumer twitter;
  private USIntegrationConsumer spotify;

  public USIntegrationConsumer getStreamLabs() {
    return streamLabs;
  }

  public void setStreamLabs(USIntegrationConsumer streamLabs) {
    this.streamLabs = streamLabs;
  }

  public USIntegrationConsumer getTwitter() {
    return twitter;
  }

  public void setTwitter(USIntegrationConsumer twitter) {
    this.twitter = twitter;
  }

  public USIntegrationConsumer getSpotify() {
    return spotify;
  }

  public void setSpotify(USIntegrationConsumer spotify) {
    this.spotify = spotify;
  }
}
