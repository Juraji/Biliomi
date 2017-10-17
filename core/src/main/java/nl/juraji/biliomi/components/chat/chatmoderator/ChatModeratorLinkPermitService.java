package nl.juraji.biliomi.components.chat.chatmoderator;

import nl.juraji.biliomi.utility.types.Restartable;
import nl.juraji.biliomi.utility.types.collections.TimedMap;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class ChatModeratorLinkPermitService implements Restartable {
  private TimedMap<String, Boolean> register;

  @Override
  public void start() {
    if (register == null) {
      register = new TimedMap<>(getClass());
    }
  }

  @Override
  public void stop() {
    if (register != null) {
      register.stop();
      register = null;
    }
  }

  /**
   * Add a permit the the registry
   *
   * @param username The target user
   * @param duration The duration of the permit in milliseconds
   */
  public void addPermit(String username, long duration) {
    register.put(username, true, duration);
  }

  /**
   * Retrieve the current permit for a user
   * Removes the permit from the registry if it exists
   *
   * @param username The target user
   * @return True if there is a permit else False
   */
  public boolean usePermit(String username) {
    return register.remove(username) != null;
  }
}
