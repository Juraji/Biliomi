package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.components.games.tamagotchi.services.AgingTimerService;
import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiService;
import nl.juraji.biliomi.components.games.tamagotchi.services.ToyFactoryService;
import nl.juraji.biliomi.components.shared.BadWordsService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.config.tamagotchi.TamagotchiConfigService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiToy;
import nl.juraji.biliomi.model.games.settings.TamagotchiSettings;
import nl.juraji.biliomi.model.internal.events.bot.AchievementEvent;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class TamagotchiComponent extends Component {

  @Inject
  private AgingTimerService agingTimerService;

  @Inject
  private TamagotchiService tamagotchiService;

  @Inject
  private ToyFactoryService toyFactoryService;

  @Inject
  private PointsService pointsService;

  @Inject
  private BadWordsService badWordsService;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private TamagotchiConfigService configService;

  private TamagotchiSettings settings;

  @Override
  public void init() {
    agingTimerService.start();
    settings = settingsService.getSettings(TamagotchiSettings.class, s -> settings = s);
  }

  /**
   * Have Biliomi state some information on your Tamagotchi
   * Usage: !tamagotchi or !tamagotchi toys
   */
  @CommandRoute(command = "tamagotchi")
  public boolean tamagotchiCommand(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, i18n.get("Common.tamagotchi.notFound"));
      return false;
    }

    if ("toys".equalsIgnoreCase(arguments.getSafe(0))) {
      List<String> toyNames = tamagotchi.getToys().stream().map(TamagotchiToy::getToyName).collect(Collectors.toList());
      chat.say(i18n.get("ChatCommand.tamagotchi.info.toys")
          .add("list", toyNames));
    } else {
      chat.say(i18n.get("ChatCommand.tamagotchi.info.info")
          .add("name", tamagotchi::getName)
          .add("gender", tamagotchiService.getGenderName(tamagotchi))
          .add("species", tamagotchi::getSpecies)
          .add("foodstack", (int) tamagotchi.getFoodStack())
          .add("posessiverefergender", tamagotchiService.getGenderPosessiveReferal(tamagotchi))
          .add("mood", tamagotchiService.getMoodText(tamagotchi)));

      chat.say(i18n.get("ChatCommand.tamagotchi.info.ageAndHygiene")
          .add("name", tamagotchi::getName)
          .add("age", () -> timeFormatter.timeQuantitySince(tamagotchi.getDateOfBirth()))
          .add("hygienestate", tamagotchiService.getHygieneState(tamagotchi)));
    }

    return true;
  }

  /**
   * Buy a new Tamagotchi or stuff to keep your Tamagotchi happy
   * Usage: !tgstore [buy|food|soap|toyinfo|toy] [more...]
   */
  @CommandRoute(command = "tgstore")
  public boolean tgStoreCommand(User user, Arguments arguments) {
    return captureSubCommands("tgstore", i18n.supply("ChatCommand.tgStore.usage"), user, arguments);
  }

  /**
   * Buy a Tamagotchi
   * Usage: !tgstore buy [species] [name...]
   */
  @SubCommandRoute(parentCommand = "tgstore", command = "buy")
  public boolean tgStoreCommandBuy(User user, Arguments arguments) {
    if (!arguments.assertMinSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.buy.usage"));
      chat.whisper(user, i18n.get("ChatCommand.tgStore.buy.usage.listSpecies")
          .add("list", configService::getAvailableSpecies));
      return false;
    }

    // Check if the user has the points to buy
    if (user.getPoints() < settings.getNewPrice()) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.buy.notEnoughPoints")
          .add("points", () -> pointsService.asString(settings.getNewPrice()))
          .add("balance", () -> pointsService.asString(user.getPoints())));
      return false;
    }

    // Check if the user doesn't already own a Tamagotchi
    if (tamagotchiService.userHasTamagotchi(user)) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.buy.userHasTamagotchi"));
      return false;
    }

    // Check if the prefered species exists
    String species = configService.getSpeciesIfExists(arguments.pop());
    if (species == null) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.buy.speciesNonExistent")
          .add("list", configService::getAvailableSpecies));
      return false;
    }

    // Check the length of the name
    String name = arguments.toString();
    if (name.length() > settings.getNameMaxLength()) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.buy.nameMaxLengthExceeded")
          .add("max", settings::getNameMaxLength));
      return false;
    }

    // Check for bad word usage in the name
    if (badWordsService.containsBadWords(name)) {
      chat.whisper(user, i18n.getInputContainsBadWords());
      return false;
    }

    pointsService.take(user, settings.getNewPrice());
    Tamagotchi tamagotchi = tamagotchiService.createTamagotchi(name, user, species);

    eventBus.post(new AchievementEvent(user, "TG_BUY_TAMAGOTCHI", i18n.getString("Achievement.buyTamagotchi")));

    chat.say(i18n.get("ChatCommand.tgStore.buy.newTamagotchi")
        .add("username", user::getDisplayName)
        .add("gender", tamagotchiService.getGenderName(tamagotchi))
        .add("species", tamagotchi::getSpecies)
        .add("name", tamagotchi::getName));

    return true;
  }

  /**
   * Buy food for your Tamagotchi
   * Usage: !tgstore food [amount|max]
   */
  @SubCommandRoute(parentCommand = "tgstore", command = "food")
  public boolean tgStoreCommandFood(User user, Arguments arguments) {
    String arg = arguments.get(0);
    Double amountToAdd = Numbers.asNumber(arg).withDefault(0.0).toDouble();

    if (amountToAdd == 0 && !"max".equalsIgnoreCase(arg)) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.food.usage"));
      return false;
    }

    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);
    if (tamagotchi == null) {
      chat.whisper(user, i18n.get("Common.tamagotchi.notFound"));
      return false;
    }

    // "max" can be used to do a top up of the tamagotchi's food stack
    if ("max".equalsIgnoreCase(arg)) {
      amountToAdd = settings.getMaxFood() - tamagotchi.getFoodStack();
    } else {
      amountToAdd = Double.min(amountToAdd, settings.getMaxFood());
    }

    long cost = (long) (settings.getFoodPrice() * amountToAdd);
    if (user.getPoints() < cost) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.food.notEnoughPoints")
          .add("addedamount", amountToAdd::intValue)
          .add("points", () -> pointsService.asString(cost))
          .add("balance", () -> pointsService.asString(user.getPoints())));
    }

    pointsService.take(user, cost);
    tamagotchi.setFoodStack(tamagotchi.getFoodStack() + amountToAdd);
    tamagotchiService.increaseAffection(tamagotchi);
    tamagotchiService.save(tamagotchi);

    eventBus.post(new AchievementEvent(user, "TG_BUY_FOOD", i18n.getString("Achievement.buyFood")));

    chat.whisper(user, i18n.get("ChatCommand.tgStore.food.added")
        .add("addedamount", (int) Math.ceil(amountToAdd))
        .add("name", tamagotchi::getName)
        .add("food", (int) tamagotchi.getFoodStack())
        .add("points", () -> pointsService.asString(cost)));

    return true;
  }

  /**
   * Clean your Tamagotchi
   * Usage: !tgstore soap
   */
  @SubCommandRoute(parentCommand = "tgstore", command = "soap")
  public boolean tgStoreCommandSoap(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, i18n.get("Common.tamagotchi.notFound"));
      return false;
    }

    if (user.getPoints() < settings.getSoapPrice()) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.soap.notEnoughPoints")
          .add("points", () -> pointsService.asString(settings.getSoapPrice()))
          .add("balance", () -> pointsService.asString(user.getPoints())));
      return false;
    }

    pointsService.take(user, settings.getSoapPrice());
    tamagotchi.setHygieneLevel(settings.getMaxHygiene());
    tamagotchiService.increaseAffection(tamagotchi);
    tamagotchiService.save(tamagotchi);

    eventBus.post(new AchievementEvent(user, "TG_BUY_SOAP", i18n.getString("Achievement.buySoap")));

    chat.say(i18n.get("ChatCommand.tgStore.soap.cleaned")
        .add("name", tamagotchi::getName)
        .add("addressgender", tamagotchiService.getGenderAddress(tamagotchi)));

    return true;
  }

  /**
   * Get information on a toy
   * Usage: !tgstore toyinfo [toy name]
   */
  @SubCommandRoute(parentCommand = "tgstore", command = "toyinfo")
  public boolean tgStoreCommandToyInfo(User user, Arguments arguments) {
    TamagotchiToy toy = toyFactoryService.getToy(arguments.toString());
    if (toy == null) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.toyinfo.usage")
          .add("list", toyFactoryService::getToyNameSet));
      return false;
    }

    chat.say(i18n.get("ChatCommand.tgStore.toyinfo.info")
        .add("toyname", toy::getToyName)
        .add("foodmodpercent", () -> MathUtils.doubleToPercentage(toy.getFoodModifier()))
        .add("moodmodpercent", () -> MathUtils.doubleToPercentage(toy.getMoodModifier()))
        .add("hygienemodpercent", () -> MathUtils.doubleToPercentage(toy.getHygieneModifier()))
        .add("points", () -> pointsService.asString(toy.getCost())));

    return true;
  }

  /**
   * Buy toys for your Tamagotchi.
   * Different Toys modify different properties of your tamagotchi, such as food or mood decay
   * Usage !tgstore toy [toy name]
   */
  @SubCommandRoute(parentCommand = "tgstore", command = "toy")
  public boolean tgStoreCommandToy(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, i18n.get("Common.tamagotchi.notFound"));
      return false;
    }

    TamagotchiToy toy = toyFactoryService.getToy(arguments.toString());
    if (toy == null) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.toy.usage")
          .add("list", toyFactoryService::getToyNameSet));
      return false;
    }

    if (toyFactoryService.tamagotchiHasToy(tamagotchi, toy)) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.toy.toyAlreadyPresent")
          .add("name", tamagotchi::getName)
          .add("toyname", toy::getToyName)
          .add("refergender", tamagotchiService.getGenderReferal(tamagotchi)));
      return false;
    }

    if (user.getPoints() < toy.getCost()) {
      chat.say(i18n.get("ChatCommand.tgStore.toy.notEnoughPoints")
          .add("toyname", toy::getToyName)
          .add("points", () -> pointsService.asString(toy.getCost()))
          .add("balance", () -> pointsService.asString(user.getPoints())));
      return false;
    }

    pointsService.take(user, toy.getCost());
    tamagotchi.getToys().add(toy);
    tamagotchiService.increaseAffection(tamagotchi);
    tamagotchiService.save(tamagotchi);

    eventBus.post(new AchievementEvent(user, "TG_BUY_TOY", i18n.getString("Achievement.buyToy")));

    chat.say(i18n.get("ChatCommand.tgStore.toy.toyAdded")
        .add("username", user::getDisplayName)
        .add("name", tamagotchi::getName)
        .add("toyname", toy::getToyName));

    return true;
  }

  /**
   * Kill your Tamagotchi to be able to get a new one and start all over
   * Usage: !tgstore kill [exact Tamagotchi name]
   */
  @SubCommandRoute(parentCommand = "tgstore", command = "kill")
  public boolean tgStoreCommandKill(User user, Arguments arguments) {
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);

    if (tamagotchi == null) {
      chat.whisper(user, i18n.get("Common.tamagotchi.notFound"));
      return false;
    }

    String nameValidation = arguments.toString();
    if (!nameValidation.equals(tamagotchi.getName())) {
      chat.whisper(user, i18n.get("ChatCommand.tgStore.kill.usage"));
      return false;
    }

    tamagotchiService.kill(tamagotchi);
    tamagotchiService.save(tamagotchi);

    chat.say(i18n.get("ChatCommand.tgStore.kill.killed")
        .add("username", user::getDisplayName)
        .add("name", tamagotchi::getName)
        .add("refergender", tamagotchiService.getGenderPosessiveReferal(tamagotchi))
        .add("age", () -> timeFormatter.timeQuantitySince(tamagotchi.getDateOfBirth())));
    return true;
  }
}
