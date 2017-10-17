package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.components.games.tamagotchi.services.AgingTimerService;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by robin on 2-6-17.
 * biliomi
 */
@Default
@Singleton
@SystemComponent
public class CliTamagotchiTools extends Component {

  @Inject
  private AgingTimerService agingTimerService;

  /**
   * Run tamagotchi aging now
   * Note: This is a debug command, it ages all tamagotchis by one hour, regardless of how much time passed
   * Usage: /forcetamagotchiaging
   */
  @CliCommandRoute(command = "forcetamagotchiaging", description = "Force the Tamagotchi Aging service to run aging")
  public boolean forceTamagotchiAgingCommand(ConsoleInputEvent event) {
    agingTimerService.runNow();
    logger.info("Tamagotchi aging has been scheduled to run immediately");
    return true;
  }
}
