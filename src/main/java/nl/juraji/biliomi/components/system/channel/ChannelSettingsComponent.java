package nl.juraji.biliomi.components.system.channel;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchStream;
import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class ChannelSettingsComponent extends Component {
  public static final String CHANNEL_TITLE_TEMPLATE_KEY = "ChannelTitle";
  private static final String CHANNEL_TITLE_TEMPLATE_INPUT_REPL = "{{input}}";

  @Inject
  private ChannelService channelService;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private TemplateDao templateDao;

  /**
   * Check wether the caster or another channel is streaming or not
   * Usage: !online or !online [channelname]
   */
  @CommandRoute(command = "online")
  public boolean onlineCommand(User user, Arguments arguments) {
    TwitchStream stream;
    User targetUser;

    if (arguments.size() == 0) {
      // On no arguments post current channel status
      targetUser = usersService.getCaster();
      stream = channelService.getStream();
    } else {
      // Add the target channel as user, so we get a Twitch id and get their channelstatus
      targetUser = usersService.getUser(arguments.get(0), true);
      stream = channelService.getStream(targetUser.getTwitchUserId());
    }

    if (stream != null) {
      chat.say(l10n.get("ChatCommand.online.channelOnline")
          .add("channelname", targetUser::getDisplayName)
          .add("gamename", stream::getGame));
    } else {
      chat.say(l10n.get("Common.channels.channelOffline")
          .add("channelname", targetUser::getDisplayName));
    }
    return true;
  }

  /**
   * Get the uptime of the current channel (if the channel is online)
   * Usage: !uptime
   */
  @CommandRoute(command = "uptime")
  public boolean uptimeCommand(User user, Arguments arguments) {
    User caster = usersService.getCaster();
    TwitchStream stream = channelService.getStream();

    if (stream != null) {
      DateTime streamStart = new DateTime(stream.getCreatedAt());
      chat.say(l10n.get("ChatCommand.uptime.status")
          .add("channelname", caster::getDisplayName)
          .add("uptime", timeFormatter.timeQuantitySince(streamStart)));
    } else {
      chat.say(l10n.get("Common.channels.channelOffline")
          .add("channelname", caster::getDisplayName));
    }
    return true;
  }

  /**
   * Get the current game
   * Usage: !game
   */
  @CommandRoute(command = "game")
  public boolean gameCommand(User user, Arguments arguments) {
    User caster = usersService.getCaster();
    TwitchStream stream = channelService.getStream();

    if (stream != null) {
      chat.say(l10n.get("ChatCommand.game.status")
          .add("channelname", caster::getDisplayName)
          .add("gamename", stream::getGame));
    } else {
      chat.say(l10n.get("Common.channels.channelOffline")
          .add("channelname", caster::getDisplayName));
    }
    return true;
  }

  /**
   * Update channel information
   * Usage !channel [game|status] [more...]
   */
  @CommandRoute(command = "channel", systemCommand = true)
  public boolean channelCommand(User user, Arguments arguments) {
    return captureSubCommands("channel", l10n.supply("ChatCommand.channel.usage"), user, arguments);
  }

  /**
   * Update channel game
   * Usage !channel game [game name...]
   */
  @SubCommandRoute(parentCommand = "channel", command = "game")
  public boolean channelCommandGame(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.channel.game.usage"));
      return false;
    }

    Game updateGameResult = channelService.updateGame(arguments.toString());

    if (updateGameResult != null) {
      chat.whisper(user, l10n.get("ChatCommand.channel.game.updated")
          .add("gamename", updateGameResult::getName));
      return true;
    } else {
      chat.whisper(user, l10n.get("Common.channels.updatedfailed"));
      return false;
    }
  }

  /**
   * Update channel status
   * Optionally applies template CHANNEL_TITLE_TEMPLATE_KEY with key "{{input}}" replaced by user input
   * Usage !channel status [new status...]
   */
  @SubCommandRoute(parentCommand = "channel", command = "status")
  public boolean channelCommandStatus(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.channel.status.usage"));
      return false;
    }

    Template template = templateDao.getByKey(CHANNEL_TITLE_TEMPLATE_KEY);
    String newStatus;

    if (template == null || template.getTemplate() == null) {
      // No template available
      newStatus = arguments.toString();
    } else {
      // Template available, apply it to the input
      newStatus = Templater.template(template.getTemplate())
          .add(CHANNEL_TITLE_TEMPLATE_INPUT_REPL, arguments.toString())
          .apply();
    }

    boolean success = channelService.updateStatus(newStatus);
    if (success) {
      chat.whisper(user, l10n.get("ChatCommand.channel.status.updated")
          .add("status", newStatus));
      return true;
    } else {
      chat.whisper(user, l10n.get("Common.channels.updatedfailed"));
      return false;
    }
  }

  /**
   * Set a template to apply when the channel status is updated
   * Use the key "{{input}}" which will be replaced with the arguments
   * Usage: !channel titletemplate [template...] or !channel titletemplate off
   */
  @SubCommandRoute(parentCommand = "channel", command = "titletemplate")
  public boolean channelCommandTitleTemplate(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.channel.titletemplate.usage"));
      return false;
    }

    Template template = templateDao.getByKey(CHANNEL_TITLE_TEMPLATE_KEY);
    String newTemplate = arguments.toString();
    OnOff off = EnumUtils.toEnum(newTemplate, OnOff.class);

    if (template == null) {
      throw new IllegalStateException("Channel title template does not exist");
    }

    if (EnumUtils.equals(OnOff.OFF, off)) {
      // User wishes to delete the existing template
      template.setTemplate(null);
      chat.whisper(user, l10n.get("ChatCommand.channel.titletemplate.deleted"));
    } else {
      // User wishes to set or edit the current template
      template.setTemplate(arguments.toString());
      templateDao.save(template);

      chat.whisper(user, l10n.get("ChatCommand.channel.titletemplate.updated")
          .add("template", template::getTemplate));
    }

    return true;
  }
}
