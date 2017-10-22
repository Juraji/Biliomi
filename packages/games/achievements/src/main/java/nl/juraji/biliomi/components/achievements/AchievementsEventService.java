package nl.juraji.biliomi.components.achievements;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.games.AchievementRecord;
import nl.juraji.biliomi.model.games.AchievementRecordDao;
import nl.juraji.biliomi.model.games.AchievementsSettings;
import nl.juraji.biliomi.model.internal.events.bot.AchievementEvent;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.I18nData;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.collections.I18nMap;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
@EventBusSubscriber
public class AchievementsEventService implements Init {

  @Inject
  @I18nData(AchievementsComponent.class)
  private I18nMap i18n;

  @Inject
  private ChatService chat;

  @Inject
  private AchievementRecordDao recordDao;

  @Inject
  private SettingsService settingsService;
  private AchievementsSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(AchievementsSettings.class, s -> settings = s);
  }

  @Subscribe
  public void onAchievementEvent(AchievementEvent event) {
    if (settings.isAchievementsEnabled()) {
      if (!recordDao.recordExists(event.getUser(), event.getAchievement())) {
        AchievementRecord record = new AchievementRecord();
        record.setUser(event.getUser());
        record.setAchievement(event.getAchievement());

        recordDao.save(record);

        chat.say(i18n.get("Event.achievement.get")
            .add("username", event.getUser()::getDisplayName)
            .add("achievement", event::getAchievement));
      }
    }
  }
}
