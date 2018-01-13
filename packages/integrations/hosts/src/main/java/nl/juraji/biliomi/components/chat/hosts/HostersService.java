package nl.juraji.biliomi.components.chat.hosts;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.webhook.ChannelStateEvent;
import nl.juraji.biliomi.model.internal.twitch.hosting.TwitchHostInEvent;
import nl.juraji.biliomi.model.internal.twitch.hosting.TwitchUnhostEvent;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.collections.FastList;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
@EventBusSubscriber
public class HostersService {
  private final List<String> hosters = new FastList<>();

  @Inject
  private UsersService usersService;

  @Subscribe
  public void onTwitchHostInEvent(TwitchHostInEvent event) {
    String channelName = event.getChannelName();
    if (!hosters.contains(channelName)) {
      hosters.add(channelName);
    }
  }

  @Subscribe
  public void onTwitchUnhostEvent(TwitchUnhostEvent event) {
    String channelName = event.getChannelName();
    if (hosters.contains(channelName)) {
      hosters.remove(channelName);
    }
  }

  @Subscribe
  public void onChannelStateEvent(ChannelStateEvent event) {
    if (!event.isOnline()) {
      // Clear the hosters list when the channel went offline,
      // since it will no longer get updated by the watch service
      this.hosters.clear();
    }
  }

  public List<String> getHosters() {
    return Collections.unmodifiableList(hosters);
  }

  public List<User> getHostersAsUsers() {
    return hosters.stream()
        .map(username -> usersService.getUser(username))
        .collect(Collectors.toList());
  }
}
