package nl.juraji.biliomi.components.chat.topcommand;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.commands.CommandService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.PointsSettings;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class TopCommandComponent extends Component {
  private static final int RESULT_SIZE = 5;

  @Inject
  private PointsService pointsService;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private CommandService commandService;

  @Inject
  @BotName
  private String botName;

  @Inject
  @ChannelName
  private String channelName;

  @Override
  public void init() {
    // If the points names were changed we need to make sure toppoints is aliassed accordingly
    checkTopPointsAlias(settingsService.getSettings(PointsSettings.class));
    settingsService.getSettings(PointsSettings.class, this::checkTopPointsAlias);
  }

  /**
   * Get the top RESULT_SIZE of users with the most time
   * Excludes caster and bot
   * Usage: !toptime
   */
  @CommandRoute(command = "toptime")
  public boolean topTimeCommand(User user, Arguments arguments) {
    List<User> topUsers = usersService.getTopUsersByField("recordedTime", RESULT_SIZE, channelName, botName);

    Map<String, String> map = EStream.from(topUsers)
        .mapToBiEStream(User::getDisplayName, User::getRecordedTime)
        .mapValue(time -> timeFormatter.timeQuantity(time))
        .toOrderedMap();

    chat.say(i18n.get("ChatCommand.topTime.message")
        .add("list", map));
    return true;
  }

  /**
   * Get the top RESULT_SIZE of users with the most points
   * Excludes caster and bot
   * Usage: !toppoints
   */
  @CommandRoute(command = "toppoints")
  public boolean topPointsCommand(User user, Arguments arguments) {
    List<User> topUsers = usersService.getTopUsersByField("points", RESULT_SIZE, channelName, botName);

    Map<String, String> map = EStream.from(topUsers)
        .mapToBiEStream(User::getDisplayName, User::getPoints)
        .mapValue(points -> pointsService.asString(points))
        .toOrderedMap();

    chat.say(i18n.get("ChatCommand.topPoints.message")
        .add("pointsname", pointsService::pointsName)
        .add("list", map));
    return true;
  }

  /**
   * When the points name changes the "toppoints" command should adapt.
   * This is solved by creating an alias "topPOINTSNAME" to "toppoints".
   * Previous aliasses are removed when the new alias is created.
   */
  private void checkTopPointsAlias(PointsSettings settings) {
    if (!settings.getPointsNamePlural().equals(PointsSettings.DEFAULT_POINTS_NAME)) {
      String requiredAlias = "top" + settings.getPointsNamePlural().toLowerCase();

      if (!commandService.commandExists(requiredAlias)) {
        Command topPointsCommand = commandService.getCommand("toppoints");
        commandService.removeAllAliasses(topPointsCommand);
        commandService.registerAlias(requiredAlias, topPointsCommand);
      }
    }
  }
}
