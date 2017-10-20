package nl.juraji.biliomi.components.chat.followers;

import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchFollower;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchUser;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TwitchFollows;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.followers.TwitchFollowEvent;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import nl.juraji.biliomi.utility.types.components.TimerService;
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
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class FollowerWatchUpdateService extends TimerService {

  @Inject
  private EventBus eventBus;

  @Inject
  private TwitchApi twitchApi;

  @Inject
  private UsersService usersService;

  @Inject
  private ChannelService channelService;

  @Override
  public void start() {
    super.start();
    schedule(this::update, FollowerWatchConstants.FULL_UPDATE_INIT_WAIT, FollowerWatchConstants.FULL_UPDATE_INIT_WAIT_TU);
    scheduleAtFixedRate(this::incrementalUpdate, FollowerWatchConstants.INCR_UPDATE_INTERVAL, FollowerWatchConstants.INCR_UPDATE_INTERVAL_TU);
    scheduleAtFixedRate(this::update, FollowerWatchConstants.FULL_UPDATE_INTERVAL, FollowerWatchConstants.FULL_UPDATE_INTERVAL_TU);
  }

  // Todo: Remove when Twitch PubSub supports followers and is implemented in PubSub client
  // Twitch Trello card: https://trello.com/c/HJvZ8sVP
  @Deprecated
  private void incrementalUpdate() {
    try {
      Response<TwitchFollows> followsResponse = twitchApi.getChannelFollowers(channelService.getChannelId(), 100, 0);

      if (followsResponse.isOK()) {
        List<TwitchFollower> twitchFollowers = followsResponse.getData().getFollows();
        updateNewFollowers(twitchFollowers);
      }
    } catch (Exception e) {
      logger.error("Failed incremental update of followers", e);
    }
  }

  private void update() {
    ExecutorService chunkUpdateExecutor = ThreadPools.newExecutorService(8, "FollowerWatchTimerFullUpdateChunks");

    try {
      // Calculate how many pages to fetch (A page can have a max of 100 users)
      int pageCount = (int) Math.ceil(getFollowerCount() / 100.0);
      List<Future<Response<TwitchFollows>>> futures = new ArrayList<>();

      // Fetch followers from Twitch (8 pages of 100 at a time)
      IntStream.range(0, pageCount)
          .mapToObj(page -> page * 100)
          .map(offset -> chunkUpdateExecutor.submit(() -> twitchApi.getChannelFollowers(channelService.getChannelId(), 100, offset)))
          .forEachOrdered(futures::add);

      // Map all requests to a single list of followers
      // Failes when one or more pages failed to fetch
      List<TwitchFollower> twitchFollowers = EStream.from(futures)
          .map(Future::get)
          .peek(r -> {
            if (r == null || !r.isOK()) {
              throw new Exception("A response was not OK, cannot make a complete comparison");
            }
          })
          .flatMap(response -> response.getData().getFollows().stream())
          .collect(Collectors.toList());

      updateUnfollowers(twitchFollowers);
      updateNewFollowers(twitchFollowers);
      updateChangedUsernames(twitchFollowers);
    } catch (Exception e) {
      logger.error("Failed full update of followers", e);
    } finally {
      chunkUpdateExecutor.shutdownNow();
    }
  }

  /**
   * Compare Twitch followers with local followers.
   * If a local follower is not in the Twitch followers,
   * they will be removed as local follower
   *
   * @param twitchFollowers A list with Twitch follower objects
   */
  private void updateUnfollowers(List<TwitchFollower> twitchFollowers) {
    List<Long> tids = twitchFollowers.stream()
        .map(twitchFollower -> twitchFollower.getUser().getId())
        .collect(Collectors.toList());
    List<User> localFollowers = usersService.getFollowers();

    List<User> unfollowers = localFollowers.stream()
        .filter(user -> !tids.contains(user.getTwitchUserId()))
        .peek(user -> user.setFollower(false))
        .collect(Collectors.toList());

    usersService.save(unfollowers);
  }

  /**
   * Compare local followers with Twitch followers.
   * If a Twitch followers is NOT in the local followers,
   * they will get added as local follower
   *
   * @param twitchFollowers A list with Twitch follower objects
   */
  private void updateNewFollowers(List<TwitchFollower> twitchFollowers) {
    // Fetch currently known subbed users from the database
    List<Long> localFollowers = usersService.getFollowers().stream()
        .map(User::getTwitchUserId)
        .collect(Collectors.toList());

    List<TwitchFollower> newFollowers = twitchFollowers.stream()
        .filter(twitchFollower -> !localFollowers.contains(twitchFollower.getUser().getId()))
        .collect(Collectors.toList());

    newFollowers.forEach(twitchFollower -> eventBus.post(new TwitchFollowEvent(
        twitchFollower.getUser().getName(),
        twitchFollower.getUser().getId(),
        new DateTime(twitchFollower.getCreatedAt())
    )));
  }

  /**
   * Update local users using the Twitch followers
   * (Oppertunity, since the data's already retrieved)
   *
   * @param twitchFollowers A list with Twitch follower objects
   */
  private void updateChangedUsernames(List<TwitchFollower> twitchFollowers) {
    List<User> users = usersService.getList();

    Map<Long, TwitchUser> twitchUserMap = EStream.from(twitchFollowers)
        .map(TwitchFollower::getUser)
        .mapToBiEStream(TwitchUser::getId)
        .invert()
        .toMap();

    List<User> updatedUsers = EStream.from(users)
        .mapToBiEStream(u -> twitchUserMap.get(u.getTwitchUserId()))
        .filterValue(Objects::nonNull)
        .filter((u, f) -> !u.getUsername().equals(f.getName()))
        .peek((u, f) -> {
          logger.info(u.getDisplayName() + " changed their username to " + f.getDisplayName());
          u.setUsername(f.getName());
          u.setDisplayName(f.getDisplayName());
        })
        .map((u, f) -> u)
        .collect(Collectors.toList());

    usersService.save(updatedUsers);
  }

  private int getFollowerCount() throws Exception {
    Response<TwitchFollows> response = twitchApi.getChannelFollowers(channelService.getChannelId(), 0, 0);
    if (response.isOK()) {
      return response.getData().getTotal();
    } else {
      throw new Exception("Retrieving follower count failed, Status: " + response.getStatus() + ", Message: " + response.getRawData());
    }
  }
}
