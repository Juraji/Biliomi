package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiService;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 3-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class TGHugComponent extends Component {
  private static final double MOOD_INCREASE = 0.06;

  @Inject
  private TamagotchiService tamagotchiService;

  /**
   * Have your Tamagotchi hug another Tamagotchi (or user if they do not have a Tamagotchi)
   * Usage: !tghug [username]
   */
  @CommandRoute(command = "tghug")
  public boolean tghugCommand(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, l10n.get("Common.tamagotchi.notFound"));
      return false;
    }

    String otherUsername = arguments.get(0);
    if (otherUsername == null) {
      chat.whisper(user, l10n.get("ChatCommand.tgHug.usage"));
      return false;
    }

    User otherUser = usersService.getUser(otherUsername, true);
    if (otherUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(otherUsername));
      return false;
    }

    Tamagotchi otherTamagotchi = tamagotchiService.getTamagotchi(otherUser);
    chat.say(l10n.get("ChatCommand.tgHug.hug")
        .add("name", tamagotchi::getName)
        .add("othername", () -> (otherTamagotchi == null ? otherUser.getNameAndTitle() : otherTamagotchi.getName())));

    // Increase caller tamagotchi mood
    tamagotchiService.increaseAffection(tamagotchi);
    tamagotchiService.addToMoodLevel(tamagotchi, MOOD_INCREASE);
    tamagotchiService.save(tamagotchi);

    if (otherTamagotchi != null) {
      // Increase target tamagotchi mood, if present
      tamagotchiService.increaseAffection(otherTamagotchi);
      tamagotchiService.addToMoodLevel(otherTamagotchi, MOOD_INCREASE);
      tamagotchiService.save(otherTamagotchi);
    }

    return true;
  }
}
