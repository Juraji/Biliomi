package nl.juraji.biliomi.components.social.steam;

import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.model.social.steam.SteamSettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.cmd.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;

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
   * Usage: !steam [autogameupdate] [more...]
   */
  @CommandRoute(command = "steam", systemCommand = true)
  public boolean steamCommand(User user, Arguments arguments) {
    return captureSubCommands("steam", () -> l10n.getString("ChatCommand.steam.usage"), user, arguments);
  }

  /**
   * Enable automatic game sync from Steam to Twitch
   * Usage: !steam autogameupdate [on or off]
   */
  @SubCommandRoute(command = "autogameupdate", parentCommand = "steam")
  public boolean steamAutoGameUpdateCommand(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.steam.autoGameUpdate.usage"));
      return false;
    }

    boolean doEnableWatch = OnOff.ON.equals(onOff);
    if (doEnableWatch && !steamGameWatch.isAvailable()) {
      chat.whisper(user, l10n.get("ChatCommand.steam.autoGameUpdate.unavailable"));
      return false;
    }

    SteamSettings settings = settingsService.getSettings(SteamSettings.class);
    if (settings.isAutoUpdateChannelGame() != doEnableWatch){
      if (doEnableWatch) {
        steamGameWatch.start();
      } else {
        steamGameWatch.stop();
      }
    }

    settings.setAutoUpdateChannelGame(doEnableWatch);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.steam.autoGameUpdate.set")
        .add("state", l10n.getEnabledDisabled(doEnableWatch)));
    return true;
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
