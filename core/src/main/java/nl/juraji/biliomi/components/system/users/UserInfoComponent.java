package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.components.shared.BadWordsService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 2-5-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class UserInfoComponent extends Component {

  @Inject
  private PointsService pointsService;

  @Inject
  private BadWordsService badWordsService;

  @Inject
  private TimeFormatter timeFormatter;

  /**
   * Find out in which user group you currently are
   * Usage: !mygroup
   */
  @CommandRoute(command = "mygroup")
  public boolean groupCommand(User user, Arguments arguments) {
    chat.whisper(user, i18n.get("ChatCommand.myGroup.message")
        .add("groupname", user.getUserGroup().getName()));
    return true;
  }

  /**
   * get your current title
   * Usage: !mytitle
   */
  @CommandRoute(command = "mytitle")
  public boolean myTitleCommand(User user, Arguments arguments) {
    if (arguments.isEmpty()) {
      if (StringUtils.isEmpty(user.getTitle())) {
        chat.whisper(user, i18n.get("ChatCommand.myTitle.noTitle"));
      } else {
        chat.say(i18n.get("ChatCommand.myTitle.message")
            .add("titledusername", user::getNameAndTitle));
      }
    }
    return true;
  }

  /**
   * Change or remove your title
   * Usage: !changemytitle [new title...]
   */
  @CommandRoute(command = "changemytitle")
  public boolean changeTitleCommand(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, i18n.get("ChatCommand.changeMyTitle.usage"));
      return false;
    }

    String newTitle = arguments.toString();
    if (badWordsService.containsBadWords(newTitle)) {
      chat.say(i18n.getInputContainsBadWords());
      return false;
    }

    user.setTitle(newTitle);
    usersService.save(user);

    chat.say(i18n.get("ChatCommand.changeMyTitle.changed")
        .add("username", user::getDisplayName)
        .add("titledusername", user::getNameAndTitle));
    return true;
  }

  /**
   * Remove your title
   * Warning: does not confirm your choice
   * Usage: !removemytitle
   */
  @CommandRoute(command = "removemytitle")
  public boolean removeTitleCommand(User user, Arguments arguments) {
    if (user.getTitle() == null) {
      return false;
    }

    user.setTitle(null);
    usersService.save(user);

    chat.say(i18n.get("ChatCommand.removeMyTitle.removed")
        .add("username", user::getDisplayName));
    return true;
  }

  /**
   * Find your current points balance
   * Is automatically aliassed to the points plural name, in the settings, whenever the points names are changed
   * Usage: !mypoints or !my[plural points name]
   */
  @CommandRoute(command = "mypoints")
  public boolean myPointsCommand(User user, Arguments arguments) {
    chat.say(i18n.get("ChatCommand.myPoints.message")
        .add("username", user::getDisplayName)
        .add("points", () -> pointsService.asString(user.getPoints()))
        .add("time", () -> timeFormatter.timeQuantity(user.getRecordedTime(), TimeUnit.HOURS)));
    return true;
  }

  /**
   * Find out for how long you have been in the channel
   * Biliomi records user presence by IRC JOIN and PART events
   * !Usage: !mytime
   */
  @CommandRoute(command = "mytime")
  public boolean myTimeCommand(User user, Arguments arguments) {
    chat.say(i18n.get("ChatCommand.myTime.message")
        .add("username", user::getDisplayName)
        .add("points", () -> pointsService.asString(user.getPoints()))
        .add("time", () -> timeFormatter.timeQuantity(user.getRecordedTime(), TimeUnit.HOURS)));
    return true;
  }
}