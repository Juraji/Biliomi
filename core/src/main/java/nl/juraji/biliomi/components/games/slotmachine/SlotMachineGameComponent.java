package nl.juraji.biliomi.components.games.slotmachine;

import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.shared.GameMessagesService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 23-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class SlotMachineGameComponent extends Component {

  @Inject
  private EmoteService emoteService;

  @Inject
  private PointsService pointsService;

  @Inject
  private GameMessagesService gameMessagesService;

  /**
   * Run the Slot machine
   * Usage: !slot
   */
  @CommandRoute(command = "slot")
  public boolean slotCommand(User user, Arguments arguments) {
    // Retrieve 3 random Emotes
    Emote e1 = emoteService.getRandom();
    Emote e2 = emoteService.getRandom();
    Emote e3 = emoteService.getRandom();

    // Initially calculate the result
    long payout = calculateResultPayout(e1, e2, e3);

    // Check if the jackpot emote has been seen
    // If so add the value of the jackpot emote divided by 3 to the payout
    // and state the jackpotSeen message
    if (emoteService.isJackpotSeen(e1, e2, e3)) {
      Emote jpEmote = emoteService.getJackPotEmote();
      payout += Math.round(jpEmote.getValue());

      chat.say(l10n.get("ChatCommand.slot.jackpotSeen")
          .add("jackpotemote", jpEmote::getEmote)
          .add("points", pointsService.asString(jpEmote.getValue())));
    }

    // Get the win or lost message, depending on the payout being higher than 0
    String gameMessage;
    if (payout > 0) {
      pointsService.give(user, payout);
      gameMessage = gameMessagesService.getWinMessage(user.getDisplayName());
    } else {
      gameMessage = gameMessagesService.getLostMessage(user.getDisplayName());
    }

    // Post the result
    chat.say(l10n.get("ChatCommand.slot.result")
        .add("username", user::getDisplayName)
        .add("emote1", e1::getEmote)
        .add("emote2", e2::getEmote)
        .add("emote3", e3::getEmote)
        .add("gamemessage", gameMessage)
        .add("points", pointsService.asString(payout)));
    return true;
  }

  private long calculateResultPayout(Emote e1, Emote e2, Emote e3) {
    if (MathUtils.compareNumbers(e1.getId(), e2.getId(), e3.getId())) {
      // If all emotes equal return the value of the first emote times 3 (for each emote)
      return e1.getValue() * 3;
    } else {
      // Find out which if the emotes match
      int which = MathUtils.compareGroupedNumbers(
          MathUtils.integerArray(e1.getId(), e2.getId()),
          MathUtils.integerArray(e2.getId(), e3.getId()),
          MathUtils.integerArray(e3.getId(), e1.getId())
      );

      switch (which) {
        case 1:
          // e1 & e2 match
          return e1.getValue();
        case 2:
          // e2 & e3 match
          return e2.getValue();
        case 3:
          // e1 & e3 match
          return e3.getValue();
        default:
          // None of the emotes match
          return 0;
      }
    }
  }
}
