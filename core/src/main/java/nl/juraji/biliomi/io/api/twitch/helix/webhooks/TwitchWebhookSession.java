package nl.juraji.biliomi.io.api.twitch.helix.webhooks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.api.TwitchHelixWebhookApi;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.FollowsNotificationHandler;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.StreamsNotificationHandler;
import nl.juraji.biliomi.utility.estreams.einterface.ERunnable;
import nl.juraji.biliomi.utility.types.components.TimerService;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class TwitchWebhookSession extends TimerService {
  private static final String FOLLOWS_ENDPOINT = "/follows";
  private static final String STREAMS_ENDPOINT = "/streams";
  private static final int LEASE_DURATION_HRS = 24;

  @Inject
  private WebhookReceiver receiver;

  @Inject
  private TwitchHelixWebhookApi twitchHelixWebhookApi;

  @Inject
  private ChannelService channelService;

  @Inject
  private UsersService usersService;

  @Override
  public void start() {
    String channelId = String.valueOf(channelService.getChannelId());
    int subscriptionRefreshInterval = LEASE_DURATION_HRS - 1;
    long leaseDurationSeconds = TimeUnit.SECONDS.convert(LEASE_DURATION_HRS, TimeUnit.HOURS);

    // Set secret on receiver, start receiver and refresh timer
    receiver.start();

    // disable scheduling refreshing subscriptions if the disable flag was supplied
    boolean disableWebhookSubscriptions = BiliomiContainer.getParameters().isDisableWebhookSubscriptions();
    if (disableWebhookSubscriptions) {
      logger.info("Will not renew subscriptions (--disablewebhooksubscriptions was supplied)");
    } else {
      super.start();
    }

    // Subscribe to streams
    StreamsNotificationHandler streamsNotificationHandler = new StreamsNotificationHandler();
    receiver.registerNotificationHandler(STREAMS_ENDPOINT, streamsNotificationHandler);
    scheduleAtFixedRate(
        subscribeCallWrapper(() -> this.twitchHelixWebhookApi.subscribeToStreamsWebhookTopic(STREAMS_ENDPOINT, leaseDurationSeconds, channelId)),
        0, subscriptionRefreshInterval, TimeUnit.HOURS
    );

    // Subscribe to follows
    FollowsNotificationHandler followsNotificationHandler = new FollowsNotificationHandler(usersService);
    receiver.registerNotificationHandler(FOLLOWS_ENDPOINT, followsNotificationHandler);
    scheduleAtFixedRate(
        subscribeCallWrapper(() -> this.twitchHelixWebhookApi.subscribeToFollowersWebhookTopic(FOLLOWS_ENDPOINT, leaseDurationSeconds, channelId)),
        0, subscriptionRefreshInterval, TimeUnit.HOURS
    );

    logger.info("Twitch webhooks initialized");
  }

  @PreDestroy
  @Override
  public void stop() {
    receiver.stop();
    super.stop();
  }

  private Runnable subscribeCallWrapper(ERunnable<Exception> runnable) {
    return () -> {
      try {
        runnable.run();
      } catch (Exception e) {
        logger.error("Failed refreshing webhook subscription, will retry later", e);
      }
    };
  }
}
