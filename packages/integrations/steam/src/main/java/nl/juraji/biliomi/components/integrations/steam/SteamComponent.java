package nl.juraji.biliomi.components.integrations.steam;

import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.integrations.SteamSettings;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@Default
@Singleton
@NormalComponent
public class SteamComponent extends Component {

  @Inject
  private SteamGameWatchTimerService steamGameWatch;

  @Override
  public void init() {
    // Do not start if integration has not been set up
    if (steamGameWatch.isAvailable()) {
      SteamSettings settings = settingsService.getSettings(SteamSettings.class);
      if (settings.isAutoUpdateChannelGame()) {
        steamGameWatch.start();
      }
    }
  }

  /**
   * Set various Steam integration settings
   * Usage: !steam [autogamesync|syncnow] [more...]
   */
  @CommandRoute(command = "steam", systemCommand = true)
  public boolean steamCommand(User user, Arguments arguments) {
    return captureSubCommands("steam", () -> i18n.getString("ChatCommand.steam.usage"), user, arguments);
  }

  /**
   * Enable automatic game sync from Steam to Twitch
   * Usage: !steam autogamesync [on or off]
   */
  @SubCommandRoute(command = "autogamesync", parentCommand = "steam")
  public boolean steamautogamesyncCommand(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, i18n.get("ChatCommand.steam.autoGameSync.usage"));
      return false;
    }

    boolean doEnableWatch = OnOff.ON.equals(onOff);
    if (doEnableWatch && !steamGameWatch.isAvailable()) {
      chat.whisper(user, i18n.get("Common.steam.unavailable"));
      return false;
    }

    SteamSettings settings = settingsService.getSettings(SteamSettings.class);
    if (settings.isAutoUpdateChannelGame() != doEnableWatch) {
      if (doEnableWatch) {
        steamGameWatch.start();
      } else {
        steamGameWatch.stop();
      }
    }

    settings.setAutoUpdateChannelGame(doEnableWatch);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.steam.autoGameSync.set")
        .add("state", i18n.getEnabledDisabled(doEnableWatch)));
    return true;
  }

  /**
   * Sync Steam game to Twitch immediately
   * Usage: !steam syncnow
   */
  @SubCommandRoute(command = "syncnow", parentCommand = "steam")
  public boolean steamSyncNowCommand(User user, Arguments arguments) {

    try {
      Game syncedGame = steamGameWatch.syncToTwitchNow();
      chat.whisper(user, i18n.get("ChatCommand.steam.syncNow.success")
          .add("game", syncedGame::getName));

      return true;
    } catch (UnavailableException e) {
      logger.error(e);
      chat.whisper(user, i18n.get("Common.steam.unavailable"));
    } catch (Exception e) {
      logger.error(e);
      chat.whisper(user, i18n.get("ChatCommand.steam.syncNow.failed"));
    }

    return false;
  }

  /**
   * Export custom commands to CSV
   * Usage: /exportcommands
   */
  @CliCommandRoute(command = "importsteamlibrary", description = "Import your Steam library using the information in the user settings")
  public boolean importSteamLibraryCommand(ConsoleInputEvent event) {
    SteamLibraryImporter steamLibraryImporter = CDI.current().select(SteamLibraryImporter.class).get();
    steamLibraryImporter.run();
    return true;
  }
}
