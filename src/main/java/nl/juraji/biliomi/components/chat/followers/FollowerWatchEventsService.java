package nl.juraji.biliomi.components.chat.followers;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.FollowerWatchSettings;
import nl.juraji.biliomi.model.internal.events.twitch.followers.TwitchFollowEvent;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 18-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class FollowerWatchEventsService implements Init {

  @Inject
  private UsersService usersService;

  @Inject
  private PointsService pointsService;

  @Inject
  private ChatService chat;

  @Inject
  private TemplateDao templateDao;

  @Inject
  @ChannelName
  private String channelName;

  @Inject
  private SettingsService settingsService;

  private FollowerWatchSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(FollowerWatchSettings.class, e -> settings = e);
  }

  @Subscribe
  public void onTwitchFollowEvent(TwitchFollowEvent event) {
    // At this point we retrieve the user by twitch Id, so the users service can correct possibly changed username and display name
    User user = usersService.getUserByTwitchId(event.getTwitchId());
    user.setFollower(true);

    // If the follow date is not null the user has followed before.
    // In this case skip the welcome and just save the follow status
    if (user.getFollowDate() == null) {
      user.setFollowDate(event.getFollowsSince());

      if (settings.getReward() > 0) {
        user.setPoints(user.getPoints() + settings.getReward());
      }

      Template template = templateDao.getByKey(FollowerWatchConstants.INCOMING_FOLLOW_NOTICE);
      assert template != null; // Template cannot be null since it's set during install/update
      if (StringUtils.isNotEmpty(template.getTemplate())) {
        chat.say(Templater.template(template.getTemplate())
            .add("username", user::getDisplayName)
            .add("points", pointsService.asString(settings.getReward())));
      }
    }

    usersService.save(user);
  }
}
