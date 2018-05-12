package nl.juraji.biliomi.io.api.twitch.helix.webhooks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.api.TwitchHelixWebhookApi;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.FollowsNotificationHandler;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.StreamsNotificationHandler;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
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

    @Inject
    @AppData("apis.twitch.helix.webhook.leaseDurationHours")
    private Long leaseDurationHours;

    @Inject
    @AppData("apis.twitch.helix.webhook.endpoint.follows")
    private String followsEndpoint;

    @Inject
    @AppData("apis.twitch.helix.webhook.endpoint.streams")
    private String streamsEndpoint;

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
        long leaseDurationSeconds = TimeUnit.SECONDS.convert(leaseDurationHours, TimeUnit.HOURS);

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
        receiver.registerNotificationHandler(streamsEndpoint, streamsNotificationHandler);
        scheduleSubscription(() -> this.twitchHelixWebhookApi.subscribeToStreamsWebhookTopic(streamsEndpoint, leaseDurationSeconds, channelId));

        // Subscribe to follows
        FollowsNotificationHandler followsNotificationHandler = new FollowsNotificationHandler(usersService);
        receiver.registerNotificationHandler(followsEndpoint, followsNotificationHandler);
        scheduleSubscription(() -> this.twitchHelixWebhookApi.subscribeToFollowersWebhookTopic(followsEndpoint, leaseDurationSeconds, channelId));

        logger.info("Twitch webhooks initialized");
    }

    @PreDestroy
    @Override
    public void stop() {
        receiver.stop();
        super.stop();
    }

    protected void scheduleSubscription(ERunnable<Exception> command) {
        long period = leaseDurationHours - 1;
        Runnable timerCommand = () -> {
            try {
                command.run();
            } catch (Exception e) {
                logger.error("Failed refreshing webhook subscription, will retry later", e);
            }
        };

        super.scheduleAtFixedRate(timerCommand, 0, period, TimeUnit.HOURS);
    }
}
