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

  public Game getByName(String name, boolean createIfNotExists) {
    if (name == null) {
      return null;
    }

    Game game = criteria()
        .add(Restrictions.eq("name", name).ignoreCase())
        .getResult();

    if (game == null && createIfNotExists) {
      game = new Game();
      game.setName(name);
      game.setFirstPlayedOn(DateTime.now());
      save(game);
    }

    return game;
  }
}
