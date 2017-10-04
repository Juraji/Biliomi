package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.cmd.CliCommandRoute;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;

/**
 * Created by robin on 1-6-17.
 * biliomi
 */
@Default
@Singleton
@SystemComponent
public class CliSteamLibraryComponent extends Component {

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
