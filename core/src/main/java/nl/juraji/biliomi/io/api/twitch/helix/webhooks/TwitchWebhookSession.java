package nl.juraji.biliomi.io.api.twitch.helix.webhooks;

import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.io.api.twitch.helix.TwitchHelixApi;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.FollowsNotificationHandler;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.StreamsNotificationHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class TwitchWebhookSession {
  public static final String FOLLOWS_ENDPOINT = "/follows";
  public static final String STREAMS_ENDPOINT = "/streams";

  @Inject
  private WebhookReceiver receiver;

  @Inject
  private TwitchHelixApi twitchHelixApi;

  @Inject
  private ChannelService channelService;

  @PostConstruct
  private void initTwitchWebhookSession() throws Exception {
    receiver.start();

    // Subscribe to streams
    StreamsNotificationHandler streamsNotificationHandler = new StreamsNotificationHandler();
    receiver.registerNotificationHandler(STREAMS_ENDPOINT, streamsNotificationHandler);
    this.twitchHelixApi.subscribeToStreamsWebhookTopic(String.valueOf(channelService.getChannelId()));

    // Subscribe to follows
    FollowsNotificationHandler followsNotificationHandler = new FollowsNotificationHandler();
    receiver.registerNotificationHandler(FOLLOWS_ENDPOINT, followsNotificationHandler);
    this.twitchHelixApi.subscribeToFollowersWebhookTopic(String.valueOf(channelService.getChannelId()));
  }

  @PreDestroy
  private void destroyTwitchWebhookSession() {
    receiver.stop();
  }
}
