package nl.juraji.biliomi.components.chat.hosts;

import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TmiHost;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TmiHosts;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.internal.twitch.hosting.TwitchHostInEvent;
import nl.juraji.biliomi.model.internal.twitch.hosting.TwitchUnhostEvent;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.types.components.TimerService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class HostWatchUpdateService extends TimerService {

  @Inject
  private EventBus eventBus;

  @Inject
  private TwitchApi twitchApi;

  @Inject
  private ChannelService channelService;

  @Inject
  private HostersService hostersService;

  @Override
  public void start() {
    super.start();
    // Update every 3 minutes
    scheduleAtFixedRate(this::update,3, TimeUnit.MINUTES);
  }

  private void update() {
    try {
      Response<TmiHosts> hostersResponse = twitchApi.getHostUsers(String.valueOf(channelService.getChannelId()));

      if (hostersResponse.isOK()) {
        List<String> currentHosters = hostersService.getHosters();
        List<TmiHost> tmiHosts = hostersResponse.getData().getHosts();
        updateNewHosts(currentHosters, tmiHosts);
        updateUnhosts(currentHosters, tmiHosts);
      }
    } catch (Exception e) {
      logger.error("Failed updating hosts", e);
    }
  }

  private void updateNewHosts(List<String> currentHosters, List<TmiHost> tmiHosts) {
    tmiHosts.stream()
        .filter(tmiHost -> !currentHosters.contains(tmiHost.getHostUsername()))
        .map(tmiHost -> new TwitchHostInEvent(tmiHost.getHostUsername(), tmiHost.getHostId(), false))
        .forEach(eventBus::post);
  }

  private void updateUnhosts(List<String> currentHosters, List<TmiHost> tmiHosts) {
    List<String> tmiHostUsernames = tmiHosts.stream()
        .map(TmiHost::getHostUsername)
        .collect(Collectors.toList());

    currentHosters.stream()
        .filter(username -> !tmiHostUsernames.contains(username))
        .map(TwitchUnhostEvent::new)
        .forEach(eventBus::post);
  }
}
