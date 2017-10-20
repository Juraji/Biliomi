package nl.juraji.biliomi.components.system.timetracking;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.model.core.settings.TimeTrackingSettings;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.components.TimerService;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class TimeTrackingTimerService extends TimerService {
  private static final long UPDATE_INTERVAL = 1;
  private static final TimeUnit UPDATE_INTERVAL_TU = TimeUnit.MINUTES;

  @Inject
  private ChannelService channelService;

  @Inject
  private ChatService chatService;

  @Inject
  private UsersService usersService;

  @Inject
  private UserGroupService userGroupService;

  @Inject
  private SettingsService settingsService;
  private TimeTrackingSettings settings;
  private DateTime previousTrackingTime = DateTime.now();

  @PostConstruct
  private void initTimeTrackingTimerService() {
    settings = settingsService.getSettings(TimeTrackingSettings.class, e -> {
      settings = e;
      restart();
    });
  }

  @Override
  public void start() {
    super.start();
    scheduleAtFixedRate(this::trackTime, UPDATE_INTERVAL, UPDATE_INTERVAL_TU);
  }

  private void trackTime() {
    // If offline tracking is enabled then always track
    // Else track of online tracking is enabled and stream is actually online
    boolean doTrack = settings.isTrackOffline() || (settings.isTrackOnline() && channelService.isStreamOnline());
    DateTime now = DateTime.now();

    if (doTrack) {
      final long timeToAdd = new Duration(previousTrackingTime, now).getMillis();
      final List<UserGroup> timeBasedGroups = userGroupService.getTimeBasedGroups();
      final List<User> users = chatService.getViewersAsUsers();

      users.forEach(user -> user.setRecordedTime(user.getRecordedTime() + timeToAdd));

      if (timeBasedGroups.size() > 0) {
        users.forEach(user -> assignTimeBasedGroup(timeBasedGroups, user));
      }

      usersService.save(users);
    }

    // Always update previous tracking time
    previousTrackingTime = now;
  }

  private void assignTimeBasedGroup(List<UserGroup> timeBasedGroups, User user) {
    long userHours = TimeUnit.HOURS.convert(user.getRecordedTime(), TimeUnit.MILLISECONDS);

    EStream.from(timeBasedGroups.stream())
        .mapToBiEStream(UserGroup::getLevelUpHours)
        .filterValue(levelUpTime -> levelUpTime <= userHours)
        .filterKey(userGroup -> user.getUserGroup().isNotInGroup(userGroup))
        .sortedByValue((t1, t2) -> Long.compare(t2, t1))
        .limit(1)
        .forEach((ug, t) -> user.setUserGroup(ug));
  }
}
