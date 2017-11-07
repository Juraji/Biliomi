package nl.juraji.biliomi.components.games.eightball;

import nl.juraji.biliomi.config.eightball.EightballConfigService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.bot.AchievementEvent;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class EightBallGameComponent extends Component {

  @Inject
  private EightballConfigService configService;

  /**
   * The 8ball knows all
   * Usage: !8ball question
   */
  @CommandRoute(command = "8ball")
  public boolean eightBallCommand(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.say(i18n.get("ChatCommand.8ball.noQuestion"));
      return false;
    }

    eventBus.post(new AchievementEvent(user, "USE_EIGHTBALL", i18n.getString("Achievement.use8ball")));

    String message = MathUtils.listRand(configService.getEightballMessages());
    chat.say(message);
    return true;
  }
}
