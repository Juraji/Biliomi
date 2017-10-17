package nl.juraji.biliomi.components.system.timetracking;

import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.TimeTrackingSettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class TimeTrackingComponent extends Component {

  @Inject
  private TimeTrackingTimerService timeTrackingTimer;

  @Override
  public void init() {
    timeTrackingTimer.start();
  }

  /**
   * The main command for setting timetracker settings
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Usage: !timetracker [trackonlinetime|trackofflinetime] [more...]
   */
  @CommandRoute(command = "timetracker", systemCommand = true)
  public boolean timeTrackerCommand(User user, Arguments arguments) {
    return captureSubCommands("timetracker", l10n.supply("ChatCommand.timeTrackerCommand.usage"), user, arguments);
  }

  /**
   * The sub command for enabling/disabling timetracking while stream is online
   * Usage: !timetracker trackonlinetime [on/off]
   */
  @SubCommandRoute(parentCommand = "timetracker", command = "trackonlinetime")
  public boolean timeTrackerCommandTrackOnlineTime(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.timeTrackerCommand.trackOnlineTime.usage"));
      return false;
    }

    TimeTrackingSettings settings = settingsService.getSettings(TimeTrackingSettings.class);
    settings.setTrackOnline(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.timeTrackerCommand.trackOnlineTime.set")
        .add("state", l10n.getEnabledDisabled(settings.isTrackOnline())));
    return true;
  }

  /**
   * The sub command for enabling/disabling timetracking while stream is offline
   * Usage: !timetracker trackofflinetime [on/off]
   */
  @SubCommandRoute(parentCommand = "timetracker", command = "trackofflinetime")
  public boolean timeTrackerCommandTrackOfflineTime(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.timeTrackerCommand.trackOfflineTime.usage"));
      return false;
    }

    TimeTrackingSettings settings = settingsService.getSettings(TimeTrackingSettings.class);
    settings.setTrackOffline(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.timeTrackerCommand.trackOfflineTime.set")
        .add("state", l10n.getEnabledDisabled(settings.isTrackOffline())));
    return true;
  }
}
