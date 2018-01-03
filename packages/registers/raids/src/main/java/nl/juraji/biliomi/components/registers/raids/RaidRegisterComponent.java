package nl.juraji.biliomi.components.registers.raids;

import nl.juraji.biliomi.components.shared.TemplateSetup;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.registers.RaidRecord;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class RaidRegisterComponent extends Component {
  public static final String RAID_MESSAGE_TEMPLATE_ID = "RaidMessage";
  public static final String INCOMING_RAID_MESSAGE_TEMPLATE_ID = "IncomingRaidMessage";

  @Inject
  private RaidRecordService raidRecordService;

  @Inject
  private TemplateDao templateDao;

  /**
   * Throw an outgoing raid
   * Usage: !raid [channelname]
   */
  @CommandRoute(command = "raid", systemCommand = true, modCanActivate = true)
  public boolean raidCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, i18n.get("ChatCommand.raid.usage"));
      return false;
    }

    Template template = templateDao.getByKey(RAID_MESSAGE_TEMPLATE_ID);

    String channelName = arguments.get(0);
    User channel = usersService.getUser(channelName);
    if (channel == null) {
      chat.whisper(user, i18n.getUserNonExistent(channelName));
      return false;
    }

    RaidRecord raidRecord = raidRecordService.registerOutgoingRaid(channel);
    if (raidRecord == null) {
      chat.whisper(user, i18n.get("ChatCommand.raid.channelNotOnline")
          .add("channelname", channel::getDisplayName));
      return false;
    }

    assert template != null; // Template cannot be null since it's set during install/update
    chat.say(Templater.template(template.getTemplate())
        .add("channelname", channel::getUsername)
        .add("channeldisplayname", channel::getDisplayName));
    return true;
  }

  /**
   * Register an incoming raid
   * Usage: !raider [channelname]
   */
  @CommandRoute(command = "raider", systemCommand = true, modCanActivate = true)
  public boolean raiderCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, i18n.get("ChatCommand.raider.usage"));
      return false;
    }

    String channelName = arguments.get(0);
    User channel = usersService.getUser(channelName);
    if (channel == null) {
      chat.whisper(user, i18n.getUserNonExistent(channelName));
      return false;
    }

    RaidRecord raidRecord = raidRecordService.registerIncomingRaid(channel);
    if (raidRecord == null) {
      chat.whisper(user, i18n.get("ChatCommand.raider.errorRetrievingChannel"));
      return false;
    }

    long incomingRaidCount = raidRecordService.getIncomingRaidCount(channel);
    Template template = templateDao.getByKey(INCOMING_RAID_MESSAGE_TEMPLATE_ID);

    assert template != null; // Template cannot be null since it's set during install/update
    chat.say(Templater.template(template.getTemplate())
        .add("channelname", channel::getDisplayName)
        .add("game", raidRecord::getGameAtMoment)
        .add("ordinalrecordcount", MathUtils.getOrdinal(incomingRaidCount)));
    return true;
  }

  /**
   * The main command for setting up the raidregister
   * Usage: !raidregister [message|incomingmessage] [More...]
   */
  @CommandRoute(command = "raidregister", systemCommand = true)
  public boolean raidRegisterCommand(User user, Arguments arguments) {
    return captureSubCommands("raidregister", i18n.supply("ChatCommand.raidregister.usage"), user, arguments);
  }

  /**
   * Set the message template for throwing outgoing raids
   * Usage: !raidregister message [Raid message...]
   */
  @SubCommandRoute(parentCommand = "raidregister", command = "message")
  public boolean raidRegisterCommandMessage(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(i18n.getString("ChatCommand.raidregister.message.usage"))
        .withTemplatedSavedMessage(i18n.getString("ChatCommand.raidregister.message.saved"))
        .apply(user, arguments.toString(), RAID_MESSAGE_TEMPLATE_ID);
  }

  /**
   * Set the message template for cheering incoming raids
   * Usage: !raidregister incomingmessage [Raid message...]
   */
  @SubCommandRoute(parentCommand = "raidregister", command = "incomingmessage")
  public boolean raidRegisterIncomingMessageCommand(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(i18n.getString("ChatCommand.raidregister.incomingmessage.usage"))
        .withTemplatedSavedMessage(i18n.getString("ChatCommand.raidregister.incomingmessage.saved"))
        .apply(user, arguments.toString(), INCOMING_RAID_MESSAGE_TEMPLATE_ID);
  }
}
