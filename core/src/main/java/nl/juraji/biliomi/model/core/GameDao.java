package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class GameDao extends JpaDao<Game> {

  public GameDao() {
    super(Game.class);
  }

  public Game getByName(String name) {
    if (name == null) {
      return null;
    }

    Game game = criteria()
        .add(Restrictions.eq("name", name).ignoreCase())
        .getResult();

    if (game == null) {
      game = new Game();
      game.setName(name);
      game.setFirstPlayedOn(DateTime.now());
      save(game);
    }

    return game;
  }

  public Game getBySteamId(long steamId) {
    return criteria()
        .add(Restrictions.eq("steamId", steamId))
        .getResult();
  }

  public Game getBySteamIdOrName(long steamId, String name) {
    return criteria()
        .add(Restrictions.or(
            Restrictions.eq("steamId", steamId),
            Restrictions.eq("name", name).ignoreCase()
        ))
        .getResult();
  }
}
