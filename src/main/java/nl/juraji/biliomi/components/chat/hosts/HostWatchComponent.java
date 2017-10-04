package nl.juraji.biliomi.components.chat.hosts;

import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.HostWatchSettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 17-5-2017.
 * Biliomi v3
 */
@Singleton
@NormalComponent
public class HostWatchComponent extends Component {
  private static final String HOST_CMD = ".host ";
  public static final String INCOMING_HOST_NOTICE_TEMPLATE = "IncomingHostNotice";

  @Inject
  private HostWatchEventsService hostWatchEventsService;

  @Inject
  private PointsService pointsService;

  @Inject
  private HostRecordService hostRecordService;

  @Inject
  private TemplateDao templateDao;

  private HostWatchSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(HostWatchSettings.class, s -> settings = s);
    hostWatchEventsService.init();
  }

  /**
   * Host another channel
   * Usage: !host [channelname]
   */
  @CommandRoute(command = "host", systemCommand = true)
  public boolean hostCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.hostWatch.host.usage"));
      return false;
    }

    String targetUsername = arguments.toString();
    User targetUser = usersService.getUser(targetUsername, true);

    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(targetUsername));
      return false;
    }

    hostRecordService.recordOutgoingHost(targetUser);
    chat.say(HOST_CMD + targetUser.getUsername());
    // Do not update the user. The Twitch chat already does this
    return true;
  }

  /**
   * The main command for setting followerwatch settings
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Usage: !hostwatch [reward|] [value]
   */
  @CommandRoute(command = "hostwatch", systemCommand = true)
  public boolean hostwatchCommand(User user, Arguments arguments) {
    return captureSubCommands("hostwatch", l10n.supply("ChatCommand.hostWatch.usage"), user, arguments);
  }

  /**
   * Set the reward in points when a user follows.
   * Usage: !hostwatch reward [amount of points]
   */
  @SubCommandRoute(parentCommand = "hostwatch", command = "reward")
  public boolean hostwatchCommandReward(User user, Arguments arguments) {
    Long newReward = Numbers.asNumber(arguments.popSafe()).toLong();

    if (newReward == null || newReward < 0) {
      chat.whisper(user, l10n.get("ChatCommand.hostWatch.reward.usage"));
      return false;
    }

    settings.setReward(newReward);
    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.hostWatch.reward.saved")
        .add("points", pointsService.asString(newReward)));

    return true;
  }

  /**
   * Set the notice to post in the chat when another channel initiates a host
   * Usage: !hostwatch sethostnotice [message... or OFF to disable]
   */
  @SubCommandRoute(parentCommand = "hostwatch", command = "sethostnotice")
  public boolean hostWatchsethostnoticeCommand(User user, Arguments arguments) {
    String newTemplate = arguments.toString();

    if (StringUtils.isEmpty(newTemplate)) {
      chat.whisper(user, l10n.get("ChatCommand.hostWatch.sethostnotice.usage"));
      return false;
    }

    Template template = templateDao.getByKey(INCOMING_HOST_NOTICE_TEMPLATE);
    OnOff onOff = EnumUtils.toEnum(newTemplate, OnOff.class);

    assert template != null; // Template cannot be null since it's set during install/update
    if (OnOff.OFF.equals(onOff)) {
      template.setTemplate("");
      templateDao.save(template);
      chat.whisper(user, l10n.get("ChatCommand.hostWatch.sethostnotice.disabled"));
    } else {
      template.setTemplate(newTemplate);
      templateDao.save(template);
      chat.whisper(user, l10n.get("ChatCommand.hostWatch.sethostnotice.saved"));
    }

    return true;
  }
}
