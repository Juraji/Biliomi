package nl.juraji.biliomi.components.chat.raffle;

import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.system.commands.CommandService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouterRegistry;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class RaffleComponent extends Component {

  private final Method raffleKeywordCommandRunnerMethod;

  @Inject
  private CommandService commandService;

  @Inject
  private CommandRouterRegistry commandRouterRegistry;

  @Inject
  private UserGroupService userGroupService;

  @Inject
  private PointsService pointsService;

  @Inject
  private RaffleService raffleService;

  public RaffleComponent() throws NoSuchMethodException {
    // Keep a final reference to the keyword command runner method, to be able to register custom commands in the commandService
    raffleKeywordCommandRunnerMethod = this.getClass().getDeclaredMethod("raffleKeywordCommandRunner", User.class, Arguments.class);
  }

  /**
   * Manage raffles
   * Usage: !raffle [start|end|repick] [more...]
   */
  @CommandRoute(command = "raffle", systemCommand = true)
  public boolean raffleCommand(User user, Arguments arguments) {
    return captureSubCommands("raffle", l10n.supply("ChatCommand.raffle.usage"), user, arguments);
  }

  /**
   * Start a new raffle
   * Usage: !raffle start [keyword] [cost (0 or more)] [followers only (on|off)]
   */
  @SubCommandRoute(parentCommand = "raffle", command = "start")
  public boolean raffleCommandStart(User user, Arguments arguments) {
    if (!raffleService.isUnstarted() && !raffleService.isEnded()) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.start.raffleStillRunning"));
      return false;
    }

    String keyword = arguments.pop();
    Long cost = Numbers.asNumber(arguments.pop()).toLong();
    OnOff followersOnly = EnumUtils.toEnum(arguments.pop(), OnOff.class);

    if (StringUtils.isEmpty(keyword) || cost == null || cost < 0 || followersOnly == null) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.start.usage"));
      return false;
    }

    keyword = keyword.toLowerCase();
    if (commandService.commandExists(keyword)) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.start.keywordIsCommand")
          .add("keyword", keyword));
      return false;
    }

    raffleService.startNewRaffle(keyword, cost, OnOff.ON.equals(followersOnly));

    Command command = new Command();
    command.setCommand(raffleService.getKeyword());
    command.setUserGroup(userGroupService.getDefaultGroup());
    commandService.save(command);

    commandRouterRegistry.put(keyword, this, raffleKeywordCommandRunnerMethod);

    chat.say(l10n.get("ChatCommand.raffle.start.startedAnnouncement")
        .add("username", user::getDisplayName)
        .add("keyword", keyword)
        .add("points", () -> pointsService.asString(cost))
        .add("forwho", l10n.getIfElse(raffleService.isFollowersOnly(),
            "ChatCommand.raffle.start.startedAnnouncement.followersOnly",
            "ChatCommand.raffle.start.startedAnnouncement.anyone")));
    return true;
  }

  /**
   * End a running raffle
   * Usage: !raffle end
   */
  @SubCommandRoute(parentCommand = "raffle", command = "end")
  public boolean raffleCommandEnd(User user, Arguments arguments) {
    if (raffleService.isUnstarted()) {
      chat.whisper(user, l10n.get("Common.raffles.unstarted"));
      return false;
    }

    if (raffleService.isEnded()) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.end.alreadyEndedBefore"));
      return false;
    }

    User pickedUser = raffleService.endRaffle();

    Command command = commandService.getCommand(raffleService.getKeyword());
    commandService.delete(command);
    commandRouterRegistry.remove(raffleService.getKeyword());

    if (pickedUser == null) {
      chat.say(l10n.get("ChatCommand.raffle.end.ended.noUsers"));
    } else {
      chat.say(l10n.get("ChatCommand.raffle.end.ended.withUser")
          .add("username", pickedUser::getDisplayName));
    }
    return true;
  }

  /**
   * Repick the winner of the last raffle
   * Usage: !raffle repick
   */
  @SubCommandRoute(parentCommand = "raffle", command = "repick")
  public boolean raffleCommandRepick(User user, Arguments arguments) {
    if (raffleService.isUnstarted()) {
      chat.whisper(user, l10n.get("Common.raffles.unstarted"));
      return false;
    }

    if (!raffleService.isEnded()) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.repick.raffleStillRunning"));
      return false;
    }

    User repick = raffleService.repick();

    if (repick == null) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.repick.noUsers"));
      repick = raffleService.getLastPickedUser();
      if (repick != null) {
        chat.whisper(user, l10n.get("Common.raffles.lastPickedUser")
            .add("username", repick::getDisplayName));
      }
    } else {
      chat.say(l10n.get("ChatCommand.raffle.repick.withUser")
          .add("username", repick::getDisplayName));
    }
    return true;
  }

  /**
   * Use the raffleKeywordCommandRunner to run raffle keyword handliong
   * The keyword of the currently running raffle points to this method
   */
  @SuppressWarnings("unused") // Is used by reflection in init()
  public boolean raffleKeywordCommandRunner(User user, Arguments arguments) {
    if (raffleService.isNonFollowerCannotJoin(user)) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.onKeyword.raffleIsFollowerOnly"));
      return false;
    }

    if (raffleService.isUserAlreadyJoined(user)) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.onKeyword.alreadyJoined"));
      return false;
    }

    if (raffleService.isUserNotHasEnoughPoints(user)) {
      chat.whisper(user, l10n.get("ChatCommand.raffle.onKeyword.notEnoughPoints")
          .add("points", () -> pointsService.asString(raffleService.getJoinCost()))
          .add("balance", () -> pointsService.asString(user.getPoints())));
    }

    if (raffleService.getJoinCost() > 0) {
      pointsService.take(user, raffleService.getJoinCost());
    }

    int count = raffleService.addUser(user);
    chat.say(l10n.get("ChatCommand.raffle.onKeyword.userJoined")
        .add("username", user::getNameAndTitle)
        .add("count", count));
    return true;
  }
}
