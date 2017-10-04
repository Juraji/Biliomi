package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.model.core.UserGroupDao;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@Default
@SetupTaskPriority(priority = 1) // Second
public class DefaultGroupsAndUsersTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  @ChannelName
  private String channelName;

  @Inject
  @BotName
  private String botName;

  @Inject
  private UserGroupDao userGroupDao;

  @Inject
  private UsersService usersService;

  @Override
  public void install() {
    List<UserGroup> defaultGroups = createDefaultGroups();

    // Create users for caster and bot
    if (usersService.getUser(channelName) != null && usersService.getUser(botName) != null) {
      return;
    }

    User casterUser = usersService.getUser(channelName, true);
    User botUser = usersService.getUser(botName, true);

    casterUser.setCaster(true);
    casterUser.setUserGroup(defaultGroups.get(0));
    usersService.save(casterUser);

    botUser.setModerator(true);
    botUser.setUserGroup(defaultGroups.get(1));
    usersService.save(botUser);

    logger.info("Created user {} as Caster", casterUser.getDisplayName());
    logger.info("Created user {} as Moderator", botUser.getDisplayName());
  }

  @Override
  public void update() {
    // Do nothing
  }

  @Override
  public String getDisplayName() {
    return "Create default groups and users";
  }

  /**
   * Create the default set of groups:
   * 0: Caster
   * 1: Moderator
   * 2: Viewer (Set as default)
   */
  private List<UserGroup> createDefaultGroups() {
    List<UserGroup> groups = new ArrayList<>();

    UserGroup caster = new UserGroup();
    caster.setName("Caster");
    caster.setWeight(0);
    groups.add(caster);

    UserGroup moderator = new UserGroup();
    moderator.setName("Moderator");
    moderator.setWeight(1);
    groups.add(moderator);

    UserGroup viewer = new UserGroup();
    viewer.setName("Viewer");
    viewer.setWeight(UserGroupService.MAX_WEIGHT + 1);
    viewer.setDefaultGroup(true);
    groups.add(viewer);

    userGroupDao.save(groups);
    groups.forEach(userGroup -> logger.info("Created group {}", userGroup.getName()));
    return groups;
  }
}
