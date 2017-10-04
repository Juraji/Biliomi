package nl.juraji.biliomi.components.chat.chatmoderator;

import nl.juraji.biliomi.utility.types.Restartable;
import nl.juraji.biliomi.utility.types.collections.TimedMap;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class ChatModeratorTallyService implements Restartable {
  private static final int TIME_TO_LIVE = 4;
  private static final TimeUnit TIME_TO_LIVE_UNIT = TimeUnit.HOURS;
  private TimedMap<String, Integer> tallyRegister;

  @Override
  public void start() {
    if (tallyRegister == null) {
      tallyRegister = new TimedMap<>(getClass());
    }
  }

  @Override
  public void stop() {
    if (tallyRegister != null) {
      tallyRegister.stop();
      tallyRegister = null;
    }
  }

  /**
   * Increment the tally by 1 for User
   *
   * @param username The user to tally
   * @return The strike count after tally
   */
  public int tally(String username) {
    return tallyRegister.putOrUpdate(username, i -> (i == null ? 1 : i + 1), TIME_TO_LIVE, TIME_TO_LIVE_UNIT);
  }

  /**
   * Get the current tally for a user
   *
   * @param username The user the retrieve the tally for
   * @return The current tally
   */
  public int getCurrent(String username) {
    return tallyRegister.get(username);
  }
}
