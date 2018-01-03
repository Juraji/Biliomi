package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchUser;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TwitchUserLogins;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserDao;
import nl.juraji.biliomi.model.core.UserGroup;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Default
public class UsersService {

  @Inject
  private Logger logger;

  @Inject
  private UserDao userDao;

  @Inject
  private UserGroupService userGroupService;

  @Inject
  private TwitchApi twitchApi;

  /**
   * Get user by internal id
   *
   * @param id The id of the user to retrieve
   * @return The User corresponding to the id
   */
  public User getUser(long id) {
    return userDao.get(id);
  }

  /**
   * Get a User by username, create new User if if nonexistent.
   * Synchronized, so multiple concurrent callers can assert creation
   *
   * @param username          The username
   * @return The User associated with the userrname
   */
  public synchronized User getUser(String username) {
    if (username == null) {
      return null;
    }

    String lcUsername = username.toLowerCase();
    User user = userDao.getByUsername(lcUsername);

    try {
      if (user == null) {
        Response<TwitchUserLogins> response = twitchApi.getUsersByUsername(lcUsername);

        if (response.isOK() && !response.getData().getUsers().isEmpty()) {
          TwitchUser twitchUser = response.getData().getUsers().get(0);
          user = userDao.getByTwitchId(twitchUser.getId());

          if (user == null) {
            UserGroup defaultUserGroup = userGroupService.getDefaultGroup();
            user = new User();

            user.setTwitchUserId(twitchUser.getId());
            user.setUserGroup(defaultUserGroup);
          }

          user.setUsername(twitchUser.getName());
          user.setDisplayName(twitchUser.getDisplayName());
        } else {
          throw new Exception(response.getRawData());
        }

        userDao.save(user);
      }
    } catch (Exception e) {
      logger.error("Failed getting user data from Twitch for " + username, e);
    }

    return user;
  }

  /**
   * Get a user using the Twitch id.
   * Note: Will always enrich user information with Twitch API.
   * This is to correct for changing usernames
   *
   * @param twitchId The user's Twitch id
   * @return The user associated with the id
   */
  public synchronized User getUserByTwitchId(long twitchId) {
    User user = userDao.getByTwitchId(twitchId);

    try {
      if (user == null) {
        Response<TwitchUser> response = twitchApi.getUser(String.valueOf(twitchId));

        if (response.isOK()) {
          TwitchUser twitchUser = response.getData();
          UserGroup defaultUserGroup = userGroupService.getDefaultGroup();
          user = new User();

          user.setUsername(twitchUser.getName());
          user.setDisplayName(twitchUser.getDisplayName());
          user.setUserGroup(defaultUserGroup);
          user.setTwitchUserId(twitchUser.getId());
        } else {
          throw new Exception(response.getRawData());
        }

        userDao.save(user);
      }
    } catch (Exception e) {
      logger.error("Failed getting user data from Twitch for user with id " + twitchId, e);
    }

    return user;
  }

  public List<User> getList() {
    return userDao.getList();
  }

  public List<User> getUsersByGroup(UserGroup userGroup) {
    return userDao.getUsersByGroup(userGroup);
  }

  public User getCaster() {
    return userDao.getCaster();
  }

  public List<User> getModerators() {
    return userDao.getModerators();
  }

  public List<User> getFollowers() {
    return userDao.getFollowers();
  }

  public List<User> getFollowers(int limit) {
    return userDao.getFollowers(limit);
  }

  public long getFollowerCount() {
    return userDao.getFollowerCount();
  }

  public List<User> getSubscribers() {
    return userDao.getSubscribers();
  }

  public List<User> getSubscribers(int limit) {
    return userDao.getSubscribers(limit);
  }

  public long getSubscriberCount() {
    return userDao.getSubscriberCount();
  }

  public List<User> getTopUsersByField(String fieldName, int limit, String... excludeUsernames) {
    return userDao.getTopUsersByField(fieldName, limit, excludeUsernames);
  }

  public void save(User entity) {
    userDao.save(entity);
  }

  public void save(Collection<User> entities) {
    userDao.save(entities);
  }
}
