package nl.juraji.biliomi.components.chat.subscribers;

import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.SubscriberWatchSettings;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.SubscriberPlanType;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class SubscriberWatchComponent extends Component {

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
    return captureSubCommands("subscriberwatch", l10n.supply("ChatCommand.subscriberWatch.usage"), user, arguments);
  }

  /**
   * Set the reward in points when a user (re)subscribes.
   * 
   * Usage: !subscriberwatch reward [amount of points]
   */
  @SubCommandRoute(parentCommand = "subscriberwatch", command = "reward")
  public boolean followerWatchCommandReward(User user, Arguments arguments) {
    SubscriberPlanType tier = EnumUtils.toEnum(arguments.popSafe(), SubscriberPlanType.class);
    Long newReward = Numbers.asNumber(arguments.popSafe()).toLong();

    if (tier == null || newReward == null || newReward < 0) {
      chat.whisper(user, l10n.get("ChatCommand.subscriberWatch.reward.usage"));
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
    chat.whisper(user, l10n.get("ChatCommand.subscriberWatch.reward.saved")
        .add("tier", l10n.getString(tierL10nKey))
        .add("points", pointsService.asString(newReward)));

    return true;
  }

  /**
   * Set the notice to post in the chat when a new subscriber registers
   * Usage: !subscriberwatch setsubnotice [message... or OFF to disable]
   */
  @SubCommandRoute(parentCommand = "subscriberwatch", command = "setsubnotice")
  public boolean subscriberWatchsetsubnoticeTemplateCommand(User user, Arguments arguments) {
    String newTemplate = arguments.toString();

    if (StringUtils.isEmpty(newTemplate)) {
      chat.whisper(user, l10n.get("ChatCommand.subscriberWatch.setsubnotice.usage"));
      return false;
    }

    Template template = templateDao.getByKey(SubscriberWatchConstants.SUB_NOTICE_TEMPLATE);
    OnOff onOff = EnumUtils.toEnum(newTemplate, OnOff.class);

    assert template != null; // Template cannot be null since it's set during install/update
    if (OnOff.OFF.equals(onOff)) {
      template.setTemplate("");
      templateDao.save(template);
      chat.whisper(user, l10n.get("ChatCommand.followerWatch.setsubnotice.disabled"));
    } else {
      template.setTemplate(newTemplate);
      templateDao.save(template);
      chat.whisper(user, l10n.get("ChatCommand.followerWatch.setsubnotice.saved"));
    }

    return true;
  }

  /**
   * Set the notice to post in the chat when a subscriber resubscribes
   * Usage: !subscriberwatch setresubnotice [message... or OFF to disable]
   */
  @SubCommandRoute(parentCommand = "subscriberwatch", command = "setresubnotice")
  public boolean subscriberWatchsetresubnoticeTemplateCommand(User user, Arguments arguments) {
    String newTemplate = arguments.toString();

    if (StringUtils.isEmpty(newTemplate)) {
      chat.whisper(user, l10n.get("ChatCommand.subscriberWatch.setresubnotice.usage"));
      return false;
    }

    Template template = templateDao.getByKey(SubscriberWatchConstants.RESUB_NOTICE_TEMPLATE);
    OnOff onOff = EnumUtils.toEnum(newTemplate, OnOff.class);

    assert template != null; // Template cannot be null since it's set during install/update
    if (OnOff.OFF.equals(onOff)) {
      template.setTemplate("");
      templateDao.save(template);
      chat.whisper(user, l10n.get("ChatCommand.followerWatch.setresubnotice.disabled"));
    } else {
      template.setTemplate(newTemplate);
      templateDao.save(template);
      chat.whisper(user, l10n.get("ChatCommand.followerWatch.setresubnotice.saved"));
    }

    return true;
  }
}
