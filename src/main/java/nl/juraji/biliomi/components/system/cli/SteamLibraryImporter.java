package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.io.api.steam.v1.SteamApi;
import nl.juraji.biliomi.io.api.steam.v1.model.SteamApp;
import nl.juraji.biliomi.io.api.steam.v1.model.SteamLibrary;
import nl.juraji.biliomi.io.api.steam.v1.model.wrappers.SteamLibraryResponse;
import nl.juraji.biliomi.model.core.GameDao;
import nl.juraji.biliomi.io.web.Response;
import org.apache.logging.log4j.Logger;

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
      Response<SteamLibraryResponse> libraryResponse = steamApi.getLibrary();

      if (libraryResponse.isOK() && libraryResponse.getData() != null) {
        library = libraryResponse.getData().getResponse();
      }
    } catch (Exception e) {
      logger.error("Failed retrieving Steam library information", e);
    }

    if (library == null) {
      return;
    }

    library.getGames().stream()
        .map(SteamApp::getName)
        .sorted(String::compareTo)
        .forEach(gameName -> gameDao.getByName(gameName, true));

    logger.info("Done! Imported {} items from your Steam library", library.getGameCount());
  }
}
