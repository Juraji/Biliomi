package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiService;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.system.commands.CommandService;
import nl.juraji.biliomi.components.shared.MessageTimerService;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 3-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class TGPlayComponent extends Component {
  private static final double PLAY_FOOD_DECREACE = -0.02;
  private static final double PLAY_MOOD_INCREACE = 0.2;
  private static final double PLAY_HYGIENE_DECREASE = -0.06;
  private static final double PLAYDATE_FOOD_DECREACE = -0.02;
  private static final double PLAYDATE_MOOD_INCREACE = 0.4;
  private static final double PLAYDATE_HYGIENE_DECREASE = -0.06;

  @Inject
  private TamagotchiService tamagotchiService;

  @Inject
  private CommandService commandService;

  @Inject
  private MessageTimerService messageTimerService;

  /**
   * Send your Tamagotchi off to play
   * Usage: !tgplay
   */
  @CommandRoute(command = "tgplay", defaultCooldown = 900000)
  public boolean tgplayCommand(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, l10n.get("Common.tamagotchi.notFound"));
      return false;
    }

    if (tamagotchiService.hasNotEnoughFood(tamagotchi, PLAY_FOOD_DECREACE)) {
      chat.whisper(user, l10n.get("Common.tamagotchi.notEnoughFood")
          .add("name", tamagotchi::getName));
      return false;
    }

    tamagotchiService.addToFoodStack(tamagotchi, PLAY_FOOD_DECREACE);
    tamagotchiService.addToMoodLevel(tamagotchi, PLAY_MOOD_INCREACE);
    tamagotchiService.addToHygieneLevel(tamagotchi, PLAY_HYGIENE_DECREASE);
    tamagotchiService.increaseAffection(tamagotchi);
    tamagotchiService.save(tamagotchi);

    // State that the Tamagotchi has been sent off to play
    chat.say(l10n.get("ChatCommand.tpglay.sentOffToPlay")
        .add("username", user::getDisplayName)
        .add("name", tamagotchi::getName));

    // Register a delayed message at the end of command cooldown
    Command tgplayCommand = commandService.getCommand("tgplay");
    messageTimerService.scheduleMessage(
        l10n.get("ChatCommand.tpglay.tamagotchiReturned")
            .add("name", tamagotchi::getName),
        tgplayCommand.getCooldown(),
        TimeUnit.MILLISECONDS
    );

    return true;
  }

  /**
   * Arrange a playdate between your Tamagotchi and another Tamagotchi
   * Usage: !tgplaydate [username]
   */
  @CommandRoute(command = "tgplaydate", defaultCooldown = 900000)
  public boolean tgplaydateCommand(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, l10n.get("Common.tamagotchi.notFound"));
      return false;
    }

    if (tamagotchiService.hasNotEnoughFood(tamagotchi, PLAYDATE_FOOD_DECREACE)) {
      chat.whisper(user, l10n.get("Common.tamagotchi.notEnoughFood")
          .add("name", tamagotchi::getName));
      return false;
    }

    String otherUsername = arguments.get(0);
    if (otherUsername == null) {
      chat.whisper(user, l10n.get("ChatCommand.tpglaydate.usage"));
      return false;
    }

    User otherUser = usersService.getUser(otherUsername);
    if (otherUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(otherUsername));
      return false;
    }

    Tamagotchi otherTamagotchi = tamagotchiService.getTamagotchi(otherUser);
    if (otherTamagotchi == null) {
      chat.whisper(user, l10n.get("ChatCommand.tpglaydate.otherUserHasNoTamagotchi")
          .add("otherusername", otherUser::getDisplayName));
      return false;
    } else {
      tamagotchiService.addToFoodStack(tamagotchi, PLAYDATE_FOOD_DECREACE);
      tamagotchiService.addToFoodStack(otherTamagotchi, PLAYDATE_FOOD_DECREACE);
      tamagotchiService.addToMoodLevel(tamagotchi, PLAYDATE_MOOD_INCREACE);
      tamagotchiService.addToMoodLevel(otherTamagotchi, PLAYDATE_MOOD_INCREACE);
      tamagotchiService.addToHygieneLevel(tamagotchi, PLAYDATE_HYGIENE_DECREASE);
      tamagotchiService.addToHygieneLevel(otherTamagotchi, PLAYDATE_HYGIENE_DECREASE);
      tamagotchiService.increaseAffection(tamagotchi);
      tamagotchiService.increaseAffection(otherTamagotchi);
      tamagotchiService.save(tamagotchi, otherTamagotchi);

      chat.say(l10n.get("ChatCommand.tpglaydate.sentOffOnPlaydate")
          .add("name", tamagotchi::getName)
          .add("othername", otherTamagotchi::getName));

      // Register a delayed message at the end of command cooldown
      Command tgplaydateCommand = commandService.getCommand("tgplaydate");
      messageTimerService.scheduleMessage(
          l10n.get("ChatCommand.tpglaydate.tamagotchiReturned")
              .add("name", tamagotchi::getName)
              .add("othername", otherTamagotchi::getName),
          tgplaydateCommand.getCooldown(),
          TimeUnit.MILLISECONDS
      );

      return true;
    }
  }
}
