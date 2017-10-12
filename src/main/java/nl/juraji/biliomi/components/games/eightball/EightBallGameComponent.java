package nl.juraji.biliomi.components.games.eightball;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class EightBallGameComponent extends Component {

  /**
   * The 8ball knows all
   * Usage: !8ball question
   */
  @CommandRoute(command = "8ball")
  public boolean eightBallCommand(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.say(l10n.get("ChatCommand.8ball.noQuestion"));
      return false;
    }

    List<String> strings = l10n.getKeyStartsWith("ChatCommand.8ball.answer.");
    String message = MathUtils.listRand(strings);
    chat.say(message);
    return true;
  }
}
