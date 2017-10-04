package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.utility.commandrouters.cmd.CliCommandRoute;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.types.Exporter;

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
public class CliCommandExportComponent extends Component {

  /**
   * Export custom commands to CSV
   * Usage: /exportcommands
   */
  @CliCommandRoute(command = "exportcommands", description = "Export a CSV of your custom commands")
  public boolean exportCommandsCommand(ConsoleInputEvent event) {
    return exportCommands(CustomCommandExporter.class);
  }

  /**
   * Export all system commands to CSV
   * Usage: /exportsystemcommands
   */
  @CliCommandRoute(command = "exportsystemcommands", description = "Export a CSV of all system commands")
  public boolean exportSystemCommandsCommand(ConsoleInputEvent event) {
    return exportCommands(SystemCommandExporter.class);
  }

  private boolean exportCommands(Class<? extends Exporter> exporterClass) {
    Exporter exporter = CDI.current().select(exporterClass).get();
    try {
      exporter.generateRows();
      exporter.save();
      logger.info(exporter.getDoneMessage());
      return true;
    } catch (Exception e) {
      logger.error("An error uccurred during export", e);
    }

    return false;
  }
}
