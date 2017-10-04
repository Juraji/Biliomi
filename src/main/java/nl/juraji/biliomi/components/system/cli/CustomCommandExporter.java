package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.model.core.CustomCommandDao;
import nl.juraji.biliomi.model.core.CustomCommand;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.utility.calculate.TextUtils;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.Exporter;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
@Default
public class CustomCommandExporter extends Exporter {

  @Inject
  private CustomCommandDao customCommandDao;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private PointsService pointsService;

  protected CustomCommandExporter() throws IOException {
    super("Command", "Message", "System Command", "Moderator Can Always Activate", "User Group", "Cooldown", "Price", "Aliasses");
  }

  @Override
  public void generateRows() {
    List<CustomCommand> customCommands = customCommandDao.getList();

    EStream.from(customCommands)
        .sorted((c1, c2) -> c1.getCommand().compareTo(c2.getCommand()))
        .forEach(customCommand -> addRecord(
            customCommand.getCommand(),
            customCommand.getMessage(),
            customCommand.isSystemCommand(),
            customCommand.isModeratorCanActivate(),
            customCommand.getUserGroup().getName(),
            timeFormatter.timeQuantity(customCommand.getCooldown()),
            pointsService.asString(customCommand.getPrice()),
            TextUtils.commaList(customCommand.getAliasses())
        ));
  }

  @Override
  public String getDoneMessage() {
    return "Saved an export of your custom commands to \"" + getTargetFile().getAbsolutePath() + "\"";
  }
}
