package nl.juraji.biliomi.components.integrations.panelserver;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 30-1-2018.
 * Biliomi
 */
@Default
@Singleton
@NormalComponent
public class PanelServerComponent extends Component {

  @Inject
  private PanelServerService serverService;

  @Override
  public void init() {
    if (serverService.isPanelSourcePresent()) {
      serverService.start();
    }
  }

  @PreDestroy
  private void destroy() {
    serverService.stop();
  }

  @CliCommandRoute(command = "restartwebserver", description = "Restart the panel web server")
  public boolean restartWebserverCommand(ConsoleInputEvent event) {
    serverService.restart();
    return true;
  }
}
