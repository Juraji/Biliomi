package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import org.joda.time.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 3-5-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class UserGroupManagementComponent extends Component {
  private static final Pattern NUMBER_AT_START_PATTERN = Pattern.compile("^[0-9].*");

  @Inject
  private UserGroupService userGroupService;

  @Inject
  private TimeFormatter timeFormatter;

  /**
   * Manage usergroups
   * Lists groups when no arguments are supplied, any arguments are passed to the subcommandrouter
   * Usage: !groups [help|add|rename|remove] [groupname]
   */
  @CommandRoute(command = "groups", systemCommand = true)
  public boolean groupsCommand(User user, Arguments arguments) {

    // If no arguments are supplied whisper a list of all known groups to user
    if (arguments.isEmpty()) {
      List<String> groupList = userGroupService.getList().stream()
          .sorted(Comparator.comparingInt(UserGroup::getWeight))
          .map(userGroup -> userGroup.getName() + " (" + userGroup.getWeight() + ")")
          .collect(Collectors.toList());

      chat.whisper(user, i18n.get("ChatCommand.groups.list")
          .add("grouplist", groupList));
      return true;
    }

    return captureSubCommands("groups", i18n.supply("ChatCommand.groups.usage"), user, arguments);
  }

  /**
   * Whisper the user some help on how groups work
   * Usage: !groups help
   */
  @SubCommandRoute(parentCommand = "groups", command = "help")
  public boolean helpCommand(User user, Arguments arguments) {
    chat.whisper(user, i18n.get("ChatCommand.groups.help"));
    return true;
  }

  /**
   * Add a new group
   * Usage: !groups add [groupname] [integer weight]
   */
  @SubCommandRoute(parentCommand = "groups", command = "add")
  public boolean groupsCommandAdd(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.groups.add.usage"));
      return false;
    }

    Integer newGroupWeight = Numbers.asNumber(arguments.get(1)).toInteger();
    String newGroupName = arguments.getSafe(0);
    if (newGroupWeight == null || NUMBER_AT_START_PATTERN.matcher(newGroupName).matches()) {
      chat.whisper(user, i18n.get("ChatCommand.groups.help"));
      return false;
    }

    if (userGroupService.groupExists(newGroupWeight)) {
      chat.whisper(user, i18n.get("Common.groups.duplicateGroupWeight")
          .add("weight", newGroupWeight));
      return false;
    }

    if (userGroupService.groupExists(newGroupName)) {
      chat.whisper(user, i18n.get("Common.groups.duplicateGroupName")
          .add("groupname", newGroupName));
      return false;
    }

    UserGroup newUserGroup = userGroupService.createNewGroup(newGroupName, newGroupWeight);
    chat.whisper(user, i18n.get("ChatCommand.groups.add.created")
        .add("groupname", newUserGroup::getName)
        .add("weight", newUserGroup::getWeight));
    return true;
  }

  /**
   * Rename a group
   * Note: Non-number input is always treated as new group name, number input is always treated as new group weight
   * Usage: !groups edit [groupname] [new groupname] or !groups edit [groupname] [new integer weight]
   */
  @SubCommandRoute(parentCommand = "groups", command = "edit")
  public boolean groupsCommandEdit(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.groups.edit.usage"));
      return false;
    }

    UserGroup userGroup = userGroupService.getByName(arguments.get(0));
    if (userGroup == null) {
      chat.whisper(user, i18n.getGroupNonExistent(arguments.get(0)));
      return false;
    }

    if (userGroup.isDefaultGroup()) {
      chat.whisper(user, i18n.get("Common.groups.defaultGroupNotEditable"));
      return false;
    }

    String userInput = arguments.getSafe(1);

    if (Numbers.asNumber(userInput).isNaN()) {
      // Input is not a number, so must be a new name
      return editUserGroupName(user, userGroup, userInput);
    } else {
      return editUserGroupWeight(user, userGroup, userInput);
    }
  }

  /**
   * Remove a group
   * Note: Any users assigned to the removed group are moved to the default group
   * Usage: !groups remove [groupname]
   */
  @SubCommandRoute(parentCommand = "groups", command = "remove")
  public boolean groupsCommandRemove(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, i18n.get("ChatCommand.groups.remove.usage"));
      return false;
    }

    UserGroup userGroup = userGroupService.getByName(arguments.get(0));
    if (userGroup == null) {
      chat.whisper(user, i18n.getGroupNonExistent(arguments.get(0)));
      return false;
    }

    if (userGroup.isDefaultGroup()) {
      chat.whisper(user, i18n.get("Common.groups.defaultGroupNotEditable"));
      return false;
    }

    // Move any user assigned to group to the default group
    List<User> usersByGroup = usersService.getUsersByGroup(userGroup);
    if (!usersByGroup.isEmpty()) {
      UserGroup defaultGroup = userGroupService.getDefaultGroup();

      usersByGroup.forEach(user1 -> user.setUserGroup(defaultGroup));
      usersService.save(usersByGroup);

      chat.whisper(user, i18n.get("ChatCommand.groups.remove.movedUsersToDefault")
          .add("count", usersByGroup::size)
          .add("defaultgroupname", defaultGroup::getName));
    }

    userGroupService.delete(userGroup);
    chat.whisper(user, i18n.get("ChatCommand.groups.remove.removed")
        .add("groupname", userGroup::getName));
    return true;
  }

  /**
   * Manage Time based groups
   * One is able to assign a certain amount of hours to a group.
   * When The TimeTrackingTimerService runs updates it checks each user and assigns
   * the apprioprate group when predicated.
   * Set an amount of hours or 0 to disable timebased assignment for this group.
   * Usage !groups timebased [groupname] [amount of hours in chat or 0]
   */
  @SubCommandRoute(parentCommand = "groups", command = "timebased")
  public boolean groupsCommandTimeBased(User user, Arguments arguments) {
    Integer newHours = Numbers.asNumber(arguments.get(1)).toInteger();

    if (!arguments.assertSize(2) || newHours == null) {
      chat.whisper(user, i18n.get("ChatCommand.groups.timebased.usage"));
      return false;
    }

    UserGroup targetGroup = userGroupService.getByName(arguments.get(0));
    if (targetGroup == null) {
      chat.whisper(user, i18n.getGroupNonExistent(arguments.get(0)));
      return false;
    }

    if (targetGroup.isDefaultGroup()) {
      chat.whisper(user, i18n.get("Common.groups.defaultGroupNotEditable"));
      return false;
    }

    if (newHours <= 0) {
      targetGroup.setLevelUpHours(null);
      chat.whisper(user, i18n.get("ChatCommand.groups.timebased.edited.unset")
          .add("groupname", targetGroup::getName));
    } else {
      targetGroup.setLevelUpHours(newHours);
      chat.whisper(user, i18n.get("ChatCommand.groups.timebased.edited")
          .add("groupname", targetGroup::getName)
          .add("time", timeFormatter.timeQuantity(Duration.standardHours(newHours), TimeUnit.HOURS)));
    }

    userGroupService.save(targetGroup);
    return true;
  }

  private boolean editUserGroupName(User user, UserGroup targetUserGroup, String newName) {
    if (NUMBER_AT_START_PATTERN.matcher(newName).matches()) {
      chat.whisper(user, i18n.get("ChatCommand.groups.help"));
      return false;
    }

    if (targetUserGroup.getName().equalsIgnoreCase(newName) || userGroupService.groupExists(newName)) {
      chat.whisper(user, i18n.get("Common.groups.duplicateGroupName")
          .add("groupname", newName));
      return false;
    }

    String oldGroupName = targetUserGroup.getName();
    targetUserGroup.setName(newName);
    userGroupService.save(targetUserGroup);

    chat.whisper(user, i18n.get("ChatCommand.groups.edit.changedName")
        .add("oldgroupname", oldGroupName)
        .add("newgroupname", targetUserGroup::getName));

    return true;
  }

  private boolean editUserGroupWeight(User user, UserGroup targetUserGroup, String newWeightString) {
    // Input is a number is must be a new weight
    Integer newGroupWeight = Numbers.asNumber(newWeightString).toInteger();

    if (newGroupWeight == null || MathUtils.isNotInRange(newGroupWeight, UserGroupService.MIN_WEIGHT, UserGroupService.MAX_WEIGHT)) {
      chat.whisper(user, i18n.get("ChatCommand.groups.help"));
      return false;
    }

    if (targetUserGroup.getWeight() == newGroupWeight || userGroupService.groupExists(newGroupWeight)) {
      chat.whisper(user, i18n.get("Common.groups.duplicateGroupWeight")
          .add("weight", newWeightString));
      return false;
    }

    int oldWeight = targetUserGroup.getWeight();
    targetUserGroup.setWeight(newGroupWeight);
    userGroupService.save(targetUserGroup);

    chat.whisper(user, i18n.get("ChatCommand.groups.edit.changedWeight")
        .add("groupname", targetUserGroup::getName)
        .add("oldweight", oldWeight)
        .add("newweight", targetUserGroup::getWeight));

    return true;
  }

  /**
   * Assign a user to a specific group
   * Note: Users can only be in one group at a time
   * Usage: !setusergroup [username] [target groupname]
   */
  @CommandRoute(command = "setusergroup", systemCommand = true, modCanActivate = true)
  public boolean setUserGroupCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.setusergroup.usage"));
      return false;
    }

    User targetUser = usersService.getUser(arguments.get(0));
    if (targetUser == null) {
      chat.whisper(user, i18n.getUserNonExistent(arguments.get(0)));
      return false;
    }

    UserGroup targetUserGroup = userGroupService.getByName(arguments.get(1));
    if (targetUserGroup == null) {
      chat.whisper(user, i18n.getGroupNonExistent(arguments.get(1)));
      return false;
    }

    UserGroup currentUserGroup = targetUser.getUserGroup();
    targetUser.setUserGroup(targetUserGroup);
    usersService.save(targetUser);

    chat.whisper(user, i18n.get("ChatCommand.setusergroup.changed")
        .add("username", targetUser::getDisplayName)
        .add("oldgroupname", currentUserGroup::getName)
        .add("newgroupname", targetUserGroup::getName));
    return true;
  }
}
