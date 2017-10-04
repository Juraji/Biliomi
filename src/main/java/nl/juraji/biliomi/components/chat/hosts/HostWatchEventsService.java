package nl.juraji.biliomi.components.chat.hosts;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.HostWatchSettings;
import nl.juraji.biliomi.model.internal.events.twitch.hosting.TwitchHostInEvent;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import static nl.juraji.biliomi.components.chat.hosts.HostWatchComponent.INCOMING_HOST_NOTICE_TEMPLATE;

/**
 * Created by Juraji on 18-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class HostWatchEventsService implements Init {
  private static final Duration HOST_POINTS_COOLDOWN = Duration.standardHours(6);

  @Inject
  private UsersService usersService;

  @Inject
  private PointsService pointsService;

  @Inject
  private HostRecordService hostRecordService;

  @Inject
  private ChatService chat;

  @Inject
  private TemplateDao templateDao;

  @Inject
  private SettingsService settingsService;

  private HostWatchSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(HostWatchSettings.class, s -> settings = s);
  }

  @Subscribe
  public void onTwitchHostInEvent(TwitchHostInEvent event) {
    User channel = usersService.getUser(event.getChannelName(), true);
    long reward = (pointsEligible(channel) ? settings.getReward() : 0);

    if (reward > 0) {
      pointsService.give(channel, reward);
    }

    hostRecordService.recordIncomingHost(channel, event.isAuto());
    Template template = templateDao.getByKey(INCOMING_HOST_NOTICE_TEMPLATE);

    assert template != null; // Template cannot be null since it's set during install/update
    if (StringUtils.isNotEmpty(template.getTemplate())) {
      chat.say(Templater.template(template.getTemplate())
          .add("username", channel::getDisplayName)
          .add("points", pointsService.asString(settings.getReward())));
    }
  }

  // Check if channel has not hosted in the cooldown period
  private boolean pointsEligible(User channel) {
    DateTime previousWithCooldownAdded = hostRecordService.getPreviousHostInDate(channel)
        .withDurationAdded(HOST_POINTS_COOLDOWN, 1);
    DateTime now = DateTime.now();

    return now.isAfter(previousWithCooldownAdded);
  }
}
