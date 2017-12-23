package nl.juraji.biliomi.components.chat.followers;

import nl.juraji.biliomi.components.shared.TemplateSetup;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.FollowerWatchSettings;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class FollowerWatchComponent extends Component {

  @Inject
  private PointsService pointsService;

  @Inject
  private TemplateDao templateDao;

  @Inject
  private FollowerWatchUpdateService followerWatchTimer;

  @Inject
  private FollowerWatchEventsService followerWatchEventsService;

  private FollowerWatchSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(FollowerWatchSettings.class, e -> settings = e);
    followerWatchEventsService.init();
    followerWatchTimer.start();
  }

  /**
   * The main command for setting followerwatch settings
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Usage: !followerwatch [reward|setfollowernotice] [value]
   */
  @CommandRoute(command = "followerwatch", systemCommand = true)
  public boolean followerWatchCommand(User user, Arguments arguments) {
    return captureSubCommands("followerwatch", i18n.supply("ChatCommand.followerWatch.usage"), user, arguments);
  }

  /**
   * Set the reward in points when a user follows.
   * Note: Payouts only happen on first-time-follows, any subsequent follow is skipped
   * Usage: !followerwatch reward [amount of points]
   */
  @SubCommandRoute(parentCommand = "followerwatch", command = "reward")
  public boolean followerWatchCommandReward(User user, Arguments arguments) {
    Long newReward = NumberConverter.asNumber(arguments.popSafe()).toLong();

    if (newReward == null || newReward < 0) {
      chat.whisper(user, i18n.get("ChatCommand.followerWatch.reward.usage"));
      return false;
    }

    settings.setReward(newReward);
    settingsService.save(settings);
    chat.whisper(user, i18n.get("ChatCommand.followerWatch.reward.saved")
        .add("points", pointsService.asString(newReward)));

    return true;
  }

  /**
   * Set the notice to post in the chat when a new follower registers
   * Usage: !followerwatch setfollowernotice [message... or OFF to disable]
   */
  @SubCommandRoute(parentCommand = "followerwatch", command = "setfollowernotice")
  public boolean followerWatchsetfollowernoticeCommand(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(i18n.getString("ChatCommand.followerWatch.setfollowernotice.usage"))
        .withTemplateDisabledMessage(i18n.getString("ChatCommand.followerWatch.setfollowernotice.disabled"))
        .withTemplatedSavedMessage(i18n.getString("ChatCommand.followerWatch.setfollowernotice.saved"))
        .apply(user, arguments.toString(), FollowerWatchConstants.INCOMING_FOLLOW_NOTICE);
  }
}
