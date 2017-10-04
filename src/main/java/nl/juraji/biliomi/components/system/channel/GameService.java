package nl.juraji.biliomi.components.system.channel;

import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.core.GameDao;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 26-5-2017.
 * Biliomi v3
 */
@Default
public class GameService {

  @Inject
  private Logger logger;

  @Inject
  private TwitchApi twitchApi;

  @Inject
  private GameDao gameDao;

  @PostConstruct
  private void initGameService() {
    // run getCurrent once to persist the current game if it doesn't exist
    getCurrent();
  }
  public Game getByName(String name, boolean createIfNotExists) {
    return gameDao.getByName(name, createIfNotExists);
  }

  /**
   * Get the current channel game
   * Persists the game as Game if it does not exist yet
   *
   * @return The current channel game, mapped to a Game object or null if the request failed
   */
  public Game getCurrent() {
    try {
      Response<TwitchChannel> channel = twitchApi.getChannel();

      if (channel.isOK()) {
        return gameDao.getByName(channel.getData().getGame(), true);
      }
    } catch (Exception e) {
      logger.error("Error retrieving channel information for caster channel", e);
    }

    return null;
  }
}
