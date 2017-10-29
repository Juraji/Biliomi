package nl.juraji.biliomi.components.achievements;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.AchievementRecord;
import nl.juraji.biliomi.model.games.AchievementRecordDao;
import nl.juraji.biliomi.model.games.AchievementsSettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
@NormalComponent
public class AchievementsComponent extends Component {

  @Inject
  private AchievementRecordDao recordDao;

  @Inject
  private AchievementsEventService eventService;

  @Override
  public void init() {
    eventService.init();
  }

  /**
   * State the user's current achievements
   * Usage: !myachievements
   */
  @CommandRoute(command = "myachievements", defaultCooldown = 5000)
  public boolean achievementsCommand(User user, Arguments arguments) {
    AchievementsSettings settings = settingsService.getSettings(AchievementsSettings.class);

    if (!settings.isAchievementsEnabled()) {
      chat.whisper(user, i18n.get("ChatCommand.myachievements.achievementsDisabled"));
    } else {
      List<AchievementRecord> records = recordDao.getRecords(user);
      if (records.size() == 0) {
        chat.whisper(user, i18n.get("ChatCommand.myachievements.stateAchievements.noAchievements"));
      } else {
        List<String> list = records.stream()
            .map(AchievementRecord::getAchievement)
            .collect(Collectors.toList());

        chat.say(i18n.get("ChatCommand.myachievements.stateAchievements")
            .add("username", user::getDisplayName)
            .add("count", records::size)
            .add("list", list));
      }
    }

    return true;
  }

  /**
   * Change achievement settings
   * Usage: !achievements [enable] [more...]
   */
  @CommandRoute(command = "achievements", systemCommand = true)
  public boolean achievementSettingsCommand(User user, Arguments arguments) {
    return captureSubCommands("achievements", () -> i18n.getString("ChatCommand.achievementSettings.usage"), user, arguments);
  }

  /**
   * Enable/disable achievements
   * Usage: !achievements enable [on or off]
   */
  @SubCommandRoute(command = "enable", parentCommand = "achievements")
  public boolean achievementSettingsEnableCommand(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, i18n.get("ChatCommand.achievements.enable.usage"));
      return false;
    }

    AchievementsSettings settings = settingsService.getSettings(AchievementsSettings.class);
    settings.setAchievementsEnabled(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.achievements.enable.set")
        .add("state", i18n.getEnabledDisabled(onOff)));
    return true;
  }
}
