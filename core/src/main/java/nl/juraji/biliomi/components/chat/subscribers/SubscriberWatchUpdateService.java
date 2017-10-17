package nl.juraji.biliomi.components.chat.subscribers;

import nl.juraji.biliomi.components.interfaces.TimerService;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchSubscription;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchUser;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TwitchSubscriptions;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.SubscriberPlanType;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.TwitchSubscriberEvent;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class SubscriberWatchUpdateService extends TimerService {

  @Inject
  private EventBus eventBus;


  @Inject
  private TwitchApi twitchApi;

  @Inject
  private ChannelService channelService;

  @Inject
  private UsersService usersService;

  @Override
  public void start() {
    super.start();
    schedule(this::update, SubscriberWatchConstants.FULL_UPDATE_INIT_WAIT, SubscriberWatchConstants.FULL_UPDATE_INIT_WAIT_TU);
    scheduleAtFixedRate(this::update, SubscriberWatchConstants.FULL_UPDATE_INTERVAL, SubscriberWatchConstants.FULL_UPDATE_INTERVAL_TU);
  }

  private void update() {
    ExecutorService chunkExecutor = ThreadPools.newExecutorService(8, "SubscriberWatchTimerServiceFullUpdateChunks");

    try {
      // Calculate how many pages to fetch (A page can have a max of 100 users)
      int pageCount = (int) Math.ceil(getSubscriberCount() / 100.0);
      List<Future<Response<TwitchSubscriptions>>> futures = new ArrayList<>();

      // Fetch subscribers from Twitch (8 pages of 100 at a time)
      IntStream.range(0, pageCount)
          .mapToObj(page -> page * 100)
          .map(offset -> chunkExecutor.submit(() -> twitchApi.getChannelSubscriptions(channelService.getChannelId(), 100, offset)))
          .forEachOrdered(futures::add);

      // Map all requests to a single list of subscribed twitch user ids
      // Failes when one or more pages failed to fetch
      List<TwitchSubscription> twitchSubscriptions = EStream.from(futures)
          .map(Future::get)
          .peek(r -> {
            if (r == null || !r.isOK()) {
              throw new Exception("A response was not OK, cannot make a complete comparison");
            }
          })
          .flatMap(r -> r.getData().getSubscriptions().stream())
          .collect(Collectors.toList());

      updateUnsubscribers(twitchSubscriptions);
      updateNewSubscribers(twitchSubscriptions);
      updateChangedUsernames(twitchSubscriptions);
    } catch (UnavailableException e) {
      logger.info("Subscriptions are not available for this channel, the SubscriberWatch will be disabled");
      this.stop();
    } catch (Exception e) {
      logger.error("Failed update of subscribers", e);
    } finally {
      chunkExecutor.shutdownNow();
    }
  }

  /**
   * Compare Twitch subscribers with local subscribers.
   * If a local subscriber is not in the Twitch subscribers,
   * they will be removed as local subscriber
   *
   * @param twitchSubscriptions A list with Twitch subscriber user ids
   */
  private void updateUnsubscribers(List<TwitchSubscription> twitchSubscriptions) {
    List<Long> tids = twitchSubscriptions.stream()
        .map(twitchSubscription -> twitchSubscription.getUser().getId())
        .collect(Collectors.toList());
    List<User> localSubscriptions = usersService.getSubscribers();

    List<User> unsubscribers = localSubscriptions.stream()
        .filter(user -> !tids.contains(user.getTwitchUserId()))
        .peek(user -> {
          user.setSubscriber(false);
          user.setSubscribeDate(null);
        })
        .collect(Collectors.toList());

    usersService.save(unsubscribers);
  }

  /**
   * Compare local subscribers with Twitch subscribers.
   * If a Twitch subscribers is NOT in the local subscribers,
   * they will get added as local subscriber and get the Tier 1 reward
   *
   * @param twitchSubscriptions A list with Twitch subscriber user ids
   */
  private void updateNewSubscribers(List<TwitchSubscription> twitchSubscriptions) {
    // Fetch currently known subbed users from the database
    List<Long> localSubscriptions = usersService.getSubscribers().stream()
        .map(User::getTwitchUserId)
        .collect(Collectors.toList());

    twitchSubscriptions.stream()
        .filter(twitchSubscription -> !localSubscriptions.contains(twitchSubscription.getUser().getId()))
        .forEach(twitchSubscription -> eventBus.post(new TwitchSubscriberEvent(
            twitchSubscription.getUser().getName(),
            twitchSubscription.getUser().getId(),
            new DateTime(twitchSubscription.getCreatedAt()),
            SubscriberPlanType.TIER1,
            false
        )));
  }


  /**
   * Update local users using the Twitch followers
   * (Oppertunity, since the data's already retrieved)
   *
   * @param twitchSubscriptions A list with Twitch follower objects
   */
  private void updateChangedUsernames(List<TwitchSubscription> twitchSubscriptions) {
    List<User> users = usersService.getList();

    Map<Long, TwitchUser> twitchUserMap = EStream.from(twitchSubscriptions)
        .map(TwitchSubscription::getUser)
        .mapToBiEStream(TwitchUser::getId)
        .invert()
        .toMap();

    List<User> updatedUsers = EStream.from(users)
        .mapToBiEStream(u -> twitchUserMap.get(u.getTwitchUserId()))
        .filterValue(Objects::nonNull)
        .filter((u, f) -> !u.getUsername().equals(f.getName()))
        .peek((u, f) -> {
          logger.info(u.getDisplayName() + " Changed their username to " + f.getDisplayName());
          u.setUsername(f.getName());
          u.setDisplayName(f.getDisplayName());
        })
        .map((u, f) -> u)
        .collect(Collectors.toList());

    usersService.save(updatedUsers);
  }

  private int getSubscriberCount() throws Exception {
    long channelId = channelService.getChannelId();
    Response<TwitchSubscriptions> response = twitchApi.getChannelSubscriptions(channelId, 0, 0);
    if (!response.isOK()) {
      if (response.getStatus() == 422) {
        throw new UnavailableException();
      } else {
        throw new Exception("Retrieving follower count failed, Status: " + response.getStatus() + ", Message: " + response.getRawData());
      }
    }

    return response.getData().getTotal();
  }
}
