package nl.juraji.biliomi.components.integrations.steam;

import nl.juraji.biliomi.components.integrations.steam.api.v1.SteamApi;
import nl.juraji.biliomi.components.integrations.steam.api.v1.model.library.SteamLibrary;
import nl.juraji.biliomi.components.integrations.steam.api.v1.model.library.SteamLibraryResponse;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.core.GameDao;
import nl.juraji.biliomi.utility.estreams.EStream;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
@Default
public class SteamLibraryImporter implements Runnable {

    @Inject
    private Logger logger;

    @Inject
    private GameDao gameDao;

    @Inject
    private SteamApi steamApi;


    @Override
    public void run() {
        logger.info("Importing games for Steam...");

        SteamLibrary library = null;

        try {
            Response<SteamLibraryResponse> libraryResponse = steamApi.getOwnedGames();

            if (libraryResponse.isOK() && libraryResponse.getData() != null) {
                library = libraryResponse.getData().getResponse();
            }
        } catch (Exception e) {
            logger.error("Failed retrieving Steam library information", e);
        }

        if (library == null) {
            return;
        }

        EStream.from(library.getGames())
                .mapToBiEStream(steamApp -> steamApp, steamApp -> gameDao.getBySteamIdOrName(steamApp.getAppId(), steamApp.getName()))
                .mapValue(game -> (game == null ? new Game() : game))
                .filterValue(game -> game.getSteamId() == null)
                .map((app, game) -> {
                    game.setName(app.getName());
                    game.setSteamId(app.getAppId());

                    if (game.getFirstPlayedOn() == null) {
                        game.setFirstPlayedOn(DateTime.now());
                    }

                    return game;
                })
                .forEach(gameDao::save);

        logger.info("Done! Imported {} items from your Steam library", library.getGameCount());
    }
}
