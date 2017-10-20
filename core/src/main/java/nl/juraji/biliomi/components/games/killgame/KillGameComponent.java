package nl.juraji.biliomi.components.games.killgame;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.UserKDRRecordStats;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class KillGameComponent extends Component {

  @Inject
  private KillRecordService killRecordService;

  @Inject
  private KillMessageService killMessageService;

  /**
   * The kill game, cuz we need it
   * Usage: !kill or !kill [username]
   */
  @CommandRoute(command = "kill")
  public boolean killCommand(User user, Arguments arguments) {
    if (arguments.assertMinSize(1)) {
      User target = null;

      // If there's just one argument check if it's a user
      if (arguments.assertSize(1)) {
        target = usersService.getUser(arguments.get(0), true);
      }

      if (target != null) {
        murder(user, target);
      } else {
        murder(user, arguments.toString());
      }
    } else {
      suicide(user);
    }

    return true;
  }

  @CommandRoute(command = "kdr")
  public boolean kdrCommand(User user, Arguments arguments) {
    UserKDRRecordStats kdr = killRecordService.getKDR(user);

    if (kdr == null) {
      chat.say(l10n.get("ChatCommand.kdr.noRecords")
          .add("username", user::getNameAndTitle));
    } else {
      chat.say(l10n.get("ChatCommand.kdr.stat")
          .add("username", user::getNameAndTitle)
          .add("kdr", kdr::getKdr)
          .add("comment", l10n.getIfElse(kdr.isMoreWins(), "ChatCommand.kdr.stat.high", "ChatCommand.kdr.stat.low"))
          .add("favoritetarget", () -> {
            if (kdr.getFavoriteTarget()!=null) {
              return kdr.getFavoriteTarget().getDisplayName();
            } else {
              return l10n.get("ChatCommand.kdr.stat.noFavTarget");
            }
          }));
    }

    return true;
  }

  /**
   * Run murder with non-user target.
   * Since the target is not a user no record will be saved
   *
   * @param killer The User running the command a.k.a. the killer
   * @param target The target as string
   */
  private void murder(User killer, String target) {
    chat.say(killMessageService.getMurderMessage(killer.getNameAndTitle(), target));
  }

  /**
   * Run murder with another User as target
   *
   * @param killer The User running the command a.k.a. the killer
   * @param target The target User a.k.a. the target
   */
  private void murder(User killer, User target) {
    killRecordService.recordKill(killer, target);
    chat.say(Templater.template(killMessageService.getMurderMessage(killer.getNameAndTitle(), target.getNameAndTitle())));
  }

  /**
   * No target was supplied, that means this is a suicide
   *
   * @param user The User running the command, he gettin rekt
   */
  private void suicide(User user) {
    killRecordService.recordSuicicide(user);
    chat.say(killMessageService.getSuicideMessage(user.getNameAndTitle()));
  }
}
