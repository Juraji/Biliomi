package nl.juraji.biliomi.components.chat.subscribers;

import nl.juraji.biliomi.components.shared.TemplateSetup;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.SubscriberPlanType;
import nl.juraji.biliomi.model.subscribers.SubscriberWatchSettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class SubscriberWatchComponent extends Component {
    public static final String SUB_NOTICE_TEMPLATE = "SubscriberSubNotice";
    public static final String RESUB_NOTICE_TEMPLATE = "SubscriberResubNotice";

    @Inject
    private PointsService pointsService;

    @Inject
    private SubscriberWatchUpdateService subscriberWatchUpdateService;

    @Inject
    private SubscriberWatchEventService subscriberWatchEventService;

    @Inject
    private SettingsService settingsService;

    @Inject
    private TemplateDao templateDao;

    @Override
    public void init() {
        subscriberWatchEventService.init();
        subscriberWatchUpdateService.start();
    }

    /**
     * The main command for setting followerwatch settings
     * Only contains subcommand, so all calls are pushed to captureSubCommands
     * Usage: !subscriberwatch [reward|setsubnotice] [value]
     */
    @CommandRoute(command = "subscriberwatch", systemCommand = true)
    public boolean followerWatchCommand(User user, Arguments arguments) {
        return captureSubCommands("subscriberwatch", i18n.supply("ChatCommand.subscriberWatch.usage"), user, arguments);
    }

    /**
     * Set the reward in points when a user (re)subscribes.
     * <p>
     * Usage: !subscriberwatch reward [amount of points]
     */
    @SubCommandRoute(parentCommand = "subscriberwatch", command = "reward")
    public boolean followerWatchCommandReward(User user, Arguments arguments) {
        SubscriberPlanType tier = EnumUtils.toEnum(arguments.popSafe(), SubscriberPlanType.class);
        Long newReward = NumberConverter.asNumber(arguments.popSafe()).toLong();

        if (tier == null || newReward == null || newReward < 0) {
            chat.whisper(user, i18n.get("ChatCommand.subscriberWatch.reward.usage"));
            return false;
        }

        SubscriberWatchSettings settings = settingsService.getSettings(SubscriberWatchSettings.class);
        String tierL10nKey = null;
        switch (tier) {
            case TIER1:
                settings.setRewardTier1(newReward);
                tierL10nKey = "ChatCommand.subscriberWatch.reward.saved.tier.1";
                break;
            case TIER2:
                settings.setRewardTier2(newReward);
                tierL10nKey = "ChatCommand.subscriberWatch.reward.saved.tier.2";
                break;
            case TIER3:
                settings.setRewardTier3(newReward);
                tierL10nKey = "ChatCommand.subscriberWatch.reward.saved.tier.3";
                break;
        }

        settingsService.save(settings);
        chat.whisper(user, i18n.get("ChatCommand.subscriberWatch.reward.saved")
                .add("tier", i18n.getString(tierL10nKey))
                .add("points", pointsService.asString(newReward)));

        return true;
    }

    /**
     * Set the notice to post in the chat when a new subscriber registers
     * Usage: !subscriberwatch setsubnotice [message... or OFF to disable]
     */
    @SubCommandRoute(parentCommand = "subscriberwatch", command = "setsubnotice")
    public boolean subscriberWatchsetsubnoticeTemplateCommand(User user, Arguments arguments) {
        return new TemplateSetup(templateDao, chat)
                .withCommandUsageMessage(i18n.getString("ChatCommand.subscriberWatch.setsubnotice.usage"))
                .withTemplateDisabledMessage(i18n.getString("ChatCommand.followerWatch.setsubnotice.disabled"))
                .withTemplatedSavedMessage(i18n.getString("ChatCommand.followerWatch.setsubnotice.saved"))
                .apply(user, arguments.toString(), SUB_NOTICE_TEMPLATE);
    }

    /**
     * Set the notice to post in the chat when a subscriber resubscribes
     * Usage: !subscriberwatch setresubnotice [message... or OFF to disable]
     */
    @SubCommandRoute(parentCommand = "subscriberwatch", command = "setresubnotice")
    public boolean subscriberWatchsetresubnoticeTemplateCommand(User user, Arguments arguments) {
        return new TemplateSetup(templateDao, chat)
                .withCommandUsageMessage(i18n.getString("ChatCommand.subscriberWatch.setresubnotice.usage"))
                .withTemplateDisabledMessage(i18n.getString("ChatCommand.followerWatch.setresubnotice.disabled"))
                .withTemplatedSavedMessage(i18n.getString("ChatCommand.followerWatch.setresubnotice.saved"))
                .apply(user, arguments.toString(), RESUB_NOTICE_TEMPLATE);
    }
}
