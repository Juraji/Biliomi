package nl.juraji.biliomi.components.chat.subscribers;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.SubscriberPlanType;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.TwitchSubscriberEvent;
import nl.juraji.biliomi.model.subscribers.SubscriberWatchSettings;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class SubscriberWatchEventService implements Init {

  @Inject
  private UsersService usersService;

  @Inject
  private PointsService pointsService;

  @Inject
  private ChatService chat;

  @Inject
  private TemplateDao templateDao;

  @Inject
  private SettingsService settingsService;

  private SubscriberWatchSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(SubscriberWatchSettings.class, e -> settings = e);
  }

  @Subscribe
  public void onTwitchSubscriberEvent(TwitchSubscriberEvent event) {
    User user = usersService.getUser(event.getUsername(), true);
    long reward = getReward(event.getSubPlan());
    user.setSubscriber(true);

    if (user.getSubscribeDate() == null) {
      user.setSubscribeDate(event.getTime());
    }

    if (reward > 0) {
      pointsService.give(user, reward);
    }

    usersService.save(user);

    Template template;
    if (event.isResub()) {
      template = templateDao.getByKey(SubscriberWatchConstants.RESUB_NOTICE_TEMPLATE);
    } else {
      template = templateDao.getByKey(SubscriberWatchConstants.SUB_NOTICE_TEMPLATE);
    }

    assert template != null; // Template cannot be null since they're set during install/update
    if (StringUtils.isNotEmpty(template.getTemplate())) {
      chat.say(Templater.template(template.getTemplate())
          .add("username", user::getDisplayName)
          .add("points", () -> pointsService.asString(reward)));
    }
  }

  private long getReward(SubscriberPlanType subPlan) {
    switch (subPlan) {
      case PRIME:
      case TIER1:
        return settings.getRewardTier1();
      case TIER2:
        return settings.getRewardTier2();
      case TIER3:
        return settings.getRewardTier3();
      default:
        return 0;
    }
  }
}
