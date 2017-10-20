package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiService;
import nl.juraji.biliomi.components.shared.MessageTimerService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.collections.TimedMap;
import nl.juraji.biliomi.utility.types.components.Component;

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
public class TGBattleComponent extends Component {
  private static final long REQUEST_TIMEOUT_MINUTES = 3;
  private static final long BATTLE_DURATION_MINUTES = 1;
  private static final double WIN_FOOD_DECREASE = -0.05;
  private static final double LOST_FOOD_DECREASE = WIN_FOOD_DECREASE * 2;
  private static final double WIN_HYGIENE_DECREASE = -0.1;
  private static final double LOST_HYGIENE_DECREASE = WIN_HYGIENE_DECREASE * 2;
  private static final double WIN_MOOD_INCREASE = 0.3;
  private static final double LOST_MOOD_DECREASE = -0.2;

  // (Target user id, requester user id)
  private final TimedMap<Long, Long> requests = new TimedMap<>(getClass());

  @Inject
  private TamagotchiService tamagotchiService;

  @Inject
  private MessageTimerService messageTimerService;

  @Inject
  @BotName
  private String botName;

  /**
   * Initiate a battle between your Tamagotchi and another Tamagotchi
   * Usage: !tgbattle [username]
   */
  @CommandRoute(command = "tgbattle")
  public boolean tgbattleCommand(User user, Arguments arguments) {
    if (requests.containsValue(user.getId())) {
      chat.whisper(user, l10n.get("ChatCommand.tgbattle.outgoingRequestStillWaiting"));
      return false;
    }

    if (requests.containsKey(user.getId())) {
      chat.whisper(user, l10n.get("ChatCommand.tgbattle.incomingRequestStillWaiting"));
      return false;
    }

    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);
    if (tamagotchi == null) {
      chat.whisper(user, l10n.get("Common.tamagotchi.notFound"));
      return false;
    }

    if (tamagotchiService.hasNotEnoughFood(tamagotchi, LOST_FOOD_DECREASE)) {
      chat.whisper(user, l10n.get("common.tgbattle.notEnoughFood")
          .add("name", tamagotchi::getName));
      return false;
    }

    String otherUsername = arguments.get(0);
    if (otherUsername == null) {
      chat.whisper(user, l10n.get("ChatCommand.tgbattle.usage"));
      return false;
    }

    User otherUser = usersService.getUser(otherUsername);
    if (otherUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(otherUsername));
      return false;
    }

    Tamagotchi otherTamagotchi = tamagotchiService.getTamagotchi(otherUser);
    if (otherTamagotchi == null) {
      chat.whisper(user, l10n.get("ChatCommand.tgbattle.otherUserHasNoTamagotchi")
          .add("otherusername", otherUser::getDisplayName));
      return false;
    }

    requests.put(otherUser.getId(), user.getId(), REQUEST_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    if (otherUser.getUsername().equals(botName)) {
      boolean b = tgbacceptCommand(otherUser, null);
      if (!b) {
        throw new IllegalStateException("Failed accepting battle for " + botName);
      }
    } else {
      chat.say(l10n.get("ChatCommand.tgbattle.stateBattleRequest")
          .add("username", user::getDisplayName)
          .add("otherusername", otherUser::getDisplayName)
          .add("name", tamagotchi::getName)
          .add("othername", otherTamagotchi::getName));
    }

    return true;
  }

  /**
   * Accept an incoming battle request
   * Usage: !tgbaccept
   */
  @CommandRoute(command = "tgbaccept")
  public boolean tgbacceptCommand(User user, Arguments arguments) {

    if (!requests.containsKey(user.getId())) {
      // Return if the calling user is not in the requestmap
      // This means the user never got a request, or the request has timed out
      return false;
    }

    User otherUser = usersService.getUser(requests.get(user.getId()));
    Tamagotchi tamagotchi1 = tamagotchiService.getTamagotchi(user);
    Tamagotchi tamagotchi2 = tamagotchiService.getTamagotchi(otherUser);

    if (tamagotchi1 == null || tamagotchi2 == null) {
      logger.error("One of the Tamagotchis was NULL. Did a Tamagotchi die?");
      return false;
    }

    if (tamagotchiService.hasNotEnoughFood(tamagotchi2, LOST_FOOD_DECREASE)) {
      chat.whisper(user, l10n.get("common.tgbattle.notEnoughFood")
          .add("name", tamagotchi2::getName));
      return false;
    }

    chat.say(l10n.get("ChatCommand.tgbaccept.accepted")
        .add("username", user::getDisplayName)
        .add("name1", tamagotchi1::getName)
        .add("name2", tamagotchi2::getName));

    // Tamagotchis don't have experience, so we are having the affection tip the balance
    int affectionDiff = MathUtils.minMax(tamagotchi1.getAffection() - tamagotchi2.getAffection(), -10, 10);
    boolean decision = (MathUtils.randRange(0, 24) <= (12 + affectionDiff));

    tamagotchiService.addToFoodStack(tamagotchi1, (decision ? WIN_FOOD_DECREASE : LOST_FOOD_DECREASE));
    tamagotchiService.addToFoodStack(tamagotchi2, (decision ? LOST_FOOD_DECREASE : WIN_FOOD_DECREASE));
    tamagotchiService.addToHygieneLevel(tamagotchi1, (decision ? WIN_HYGIENE_DECREASE : LOST_HYGIENE_DECREASE));
    tamagotchiService.addToHygieneLevel(tamagotchi2, (decision ? LOST_HYGIENE_DECREASE : WIN_HYGIENE_DECREASE));
    tamagotchiService.addToMoodLevel(tamagotchi1, (decision ? WIN_MOOD_INCREASE : LOST_MOOD_DECREASE));
    tamagotchiService.addToMoodLevel(tamagotchi2, (decision ? LOST_MOOD_DECREASE : WIN_MOOD_INCREASE));

    if (decision) {
      tamagotchiService.increaseAffection(tamagotchi1);
    } else {
      tamagotchiService.increaseAffection(tamagotchi2);
    }

    tamagotchiService.save(tamagotchi1, tamagotchi2);

    messageTimerService.scheduleMessage(
        l10n.get("Common.tgbattle.result")
            .add("victorname", () -> (decision ? tamagotchi1.getName() : tamagotchi2.getName()))
            .add("losername", () -> (decision ? tamagotchi2.getName() : tamagotchi1.getName())),
        BATTLE_DURATION_MINUTES, TimeUnit.MINUTES);
    return true;
  }
}
