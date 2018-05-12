package nl.juraji.biliomi.components.system.points;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.PointsSettings;
import nl.juraji.biliomi.utility.types.components.TimerService;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class PointsPayoutTimerService extends TimerService {

    @Inject
    private UsersService usersService;

    @Inject
    private ChannelService channelService;

    @Inject
    private ChatService chatService;

    @Inject
    private SettingsService settingsService;

    private PointsSettings settings;

    @PostConstruct
    private void initPointsPayoutTimerService() {
        settings = settingsService.getSettings(PointsSettings.class, e -> {
            settings = e;
            restart();
        });
    }

    @Override
    public void start() {
        if (channelService.isStreamOnline()) {
            boolean onlineEnabled = settings.isTrackOnline();
            long amount = settings.getOnlinePayoutAmount();
            long payoutInterval = settings.getOnlinePayoutInterval();

            if (onlineEnabled) {
                super.start();
                scheduleAtFixedRate(() -> doPayouts(amount), 0, payoutInterval, TimeUnit.MILLISECONDS);
            }
        } else {
            boolean offlineEnabled = settings.isTrackOffline();
            long amount = settings.getOfflinePayoutAmount();
            long payoutInterval = settings.getOfflinePayoutInterval();

            if (offlineEnabled) {
                super.start();
                scheduleAtFixedRate(() -> doPayouts(amount), 0, payoutInterval, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void doPayouts(long amount) {
        List<String> chatters = chatService.getViewers();

        List<User> users = chatters.stream()
                .map(usersService::getUser)
                .filter(Objects::nonNull)
                .filter(user -> user.isCaster() || user.isFollower() || user.isSubscriber())
                .collect(Collectors.toList());

        logger.info("Running points payouts for {} users, with {} {} per user...",
                users.size(), amount, settings.getPointsNamePlural());

        users.forEach(user -> user.setPoints(user.getPoints() + amount));
        usersService.save(users);
    }
}
