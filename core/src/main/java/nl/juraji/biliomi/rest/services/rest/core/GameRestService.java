package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.core.GameDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/core/games")
public class GameRestService extends ModelRestService<Game> {

    @Inject
    private GameDao gameDao;

    @Override
    public List<Game> getEntities() {
        return gameDao.getList();
    }

    @Override
    public Game getEntity(long id) {
        return gameDao.get(id);
    }

    @Override
    public Game createEntity(Game e) {
        Game game = new Game();
        game.setName(e.getName());
        game.setFirstPlayedOn(e.getFirstPlayedOn());

        if (e.getCommunities() != null) {
            game.getCommunities().addAll(e.getCommunities());
        }

        gameDao.save(game);
        return game;
    }

    @Override
    public Game updateEntity(Game e, long id) {
        Game game = gameDao.get(id);

        if (game == null) {
            return null;
        }

        game.setName(e.getName());
        game.setFirstPlayedOn(e.getFirstPlayedOn());
        game.setSteamId(e.getSteamId());

        if (e.getCommunities() != null) {
            game.getCommunities().clear();
            game.getCommunities().addAll(e.getCommunities());
        }

        gameDao.save(game);
        return game;
    }

    @Override
    public boolean deleteEntity(long id) {
        throw new ForbiddenException();
    }
}
