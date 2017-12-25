package nl.juraji.biliomi.components.games.adventures.services;

import nl.juraji.biliomi.components.games.adventures.AdventureComponent;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.config.adventures.AdventuresConfigService;
import nl.juraji.biliomi.config.adventures.YamlAdventureStory;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.AdventureSettings;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.internal.events.bot.AchievementEvent;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.I18nData;
import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import nl.juraji.biliomi.utility.types.Counter;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.I18nMap;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static nl.juraji.biliomi.components.games.adventures.services.AdventureState.*;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class AdventureRunnerService {

  private final List<Adventurer> survivors = new ArrayList<>();
  private final List<Adventurer> victims = new ArrayList<>();

  @Inject
  private EventBus eventBus;

  @Inject
  private SettingsService settingsService;

  @Inject
  private PointsService pointsService;

  @Inject
  private ChatService chat;

  @Inject
  private AdventuresConfigService configService;

  @Inject
  private UsersService usersService;

  @Inject
  private AdventureRecordService adventureRecordService;

  @Inject
  @I18nData(AdventureComponent.class)
  private I18nMap i18n;

  private AdventureSettings settings;
  private AdventureState state = NOT_RUNNING;
  private ScheduledExecutorService executor = null;
  private DateTime nextRun = DateTime.now();
  private YamlAdventureStory story = null;

  @PostConstruct
  private void initAdventureRunnerService() {
    settings = settingsService.getSettings(AdventureSettings.class, s -> settings = s);
  }

  public AdventureState getState() {
    return state;
  }

  public DateTime getNextRun() {
    return nextRun;
  }

  public boolean userExists(User user) {
    Supplier<Boolean> isSurvivor = () -> survivors.stream()
        .map(Adventurer::getUser)
        .anyMatch(survivor -> user.getId() == survivor.getId());
    Supplier<Boolean> isVictim = () -> victims.stream()
        .map(Adventurer::getUser)
        .anyMatch(victim -> user.getId() == victim.getId());
    return isSurvivor.get() || isVictim.get();
  }

  public void join(User user, Tamagotchi tamagotchi, long bet) {
    // Adventure is not running, set to join
    if (NOT_RUNNING.equals(state)) {
      state = JOIN;
      story = configService.getRandomStory();
      executor = ThreadPools.newScheduledExecutorService("AdventureRunner");
      executor.schedule(this::runAdventure, settings.getJoinTimeout(), TimeUnit.MILLISECONDS);
    }

    // Add tamagotchi if exists and affection is high enough
    if (tamagotchi != null) {
      addAdventurer(new Adventurer(tamagotchi, bet / 2, settings.getWinMultiplier()));
    }

    eventBus.post(new AchievementEvent(user, "ADV_GO_ON_ADVENTURE", i18n.getString("Achievement.goOnAdventure")));

    Adventurer adventurer = new Adventurer(user, bet, settings.getWinMultiplier());
    addAdventurer(adventurer);
  }

  private void addAdventurer(Adventurer adventurer) {
    if (MathUtils.randChoice()) {
      survivors.add(adventurer);
    } else {
      victims.add(adventurer);
    }
  }

  private void runAdventure() {
    state = RUNNING;

    chat.say(i18n.get("Adventure.runAdventure")
        .add("title", story::getTitle)
        .add("count", () -> survivors.size() + victims.size()));

    Counter counter = new Counter(1);
    story.getChapters().stream()
        .map(Templater::template)
        .forEach(templater -> {
          executor.schedule(() -> formatAndPostChapter(templater), counter.get() * configService.getNextChapterInterval(), TimeUnit.MILLISECONDS);
          counter.getAndIncrement();
        });

    executor.schedule(() -> {
      runPayouts();
      resetAdventure();
    }, counter.get() * configService.getNextChapterInterval(), TimeUnit.MILLISECONDS);
  }

  private void formatAndPostChapter(Templater templater) {
    if (survivors.isEmpty() && templater.templateContainsKey("survivors")) {
      return;
    }

    if (victims.isEmpty() && templater.templateContainsKey("victims")) {
      return;
    }

    if (!survivors.isEmpty()) {
      templater.add("survivors", () -> survivors.stream().map(Adventurer::getName).collect(Collectors.toList()));
    }

    if (!victims.isEmpty()) {
      templater.add("victims", () -> victims.stream().map(Adventurer::getName).collect(Collectors.toList()));
    }

    chat.say(templater);
  }

  private void runPayouts() {
    // (UserId, amount)
    Map<Long, Long> payouts = new HashMap<>();

    survivors.forEach(adventurer -> {
      long bet = adventurer.getBet();
      long payout = adventurer.getPayout();
      User user = adventurer.getUser();

      adventureRecordService.recordAdventureRun(user, bet, payout, adventurer.isTamagotchi());
      processSurvivorAchievements(adventurer);

      payouts.compute(user.getId(), (id, value) -> {
        long add = (adventurer.isTamagotchi() ? payout : bet + payout);
        return (value == null ? add : value + add);
      });
    });

    if (payouts.size() > 0) {
      Map<User, Long> userLongMap = EBiStream.from(payouts)
          .mapKey(id -> usersService.getUser(id))
          .peek(((user, payout) -> user.setPoints(user.getPoints() + payout)))
          .toMap();

      usersService.save(userLongMap.keySet());
      chat.say(i18n.get("Adventure.payouts")
          .add("list", () -> EBiStream.from(userLongMap)
              .mapKey(User::getDisplayName)
              .mapValue(payout -> pointsService.asString(payout))
              .toMap()));
    }

    victims.forEach((adventurer -> adventureRecordService.recordAdventureRun(
        adventurer.getUser(),
        adventurer.getBet(),
        0,
        adventurer.isTamagotchi())));
  }

  private void processSurvivorAchievements(Adventurer adventurer) {
    User user = adventurer.getUser();

    // Go big achievement
    if (adventurer.getBet() >= 1000) {
      eventBus.post(new AchievementEvent(user, "ADV_GO_BIG", i18n.getString("Achievement.goBig")));
    }

    long recordCount = adventureRecordService.getRecordCount(user);

    // Bear Grylls achievement
    if (recordCount >= 10) {
      eventBus.post(new AchievementEvent(user, "ADV_BEAR_GRYLLS", i18n.getString("Achievement.bearGrylls")));
    }

    // Steve Irwin achievement
    if (recordCount >= 20) {
      eventBus.post(new AchievementEvent(user, "ADV_STEVE_IRWIN", i18n.getString("Achievement.steveIrwin")));
    }
  }

  private void resetAdventure() {
    survivors.clear();
    victims.clear();
    nextRun = DateTime.now().withDurationAdded(settings.getCooldown(), 1);
    story = null;

    if (executor != null) {
      executor.shutdownNow();
      executor = null;
    }

    state = NOT_RUNNING;
  }
}
