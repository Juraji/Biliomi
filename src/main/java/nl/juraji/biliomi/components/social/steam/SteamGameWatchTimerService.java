package nl.juraji.biliomi.components.social.steam;

import nl.juraji.biliomi.components.interfaces.TimerService;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.channel.GameService;
import nl.juraji.biliomi.io.api.steam.v1.SteamApi;
import nl.juraji.biliomi.io.api.steam.v1.model.players.SteamPlayer;
import nl.juraji.biliomi.io.api.steam.v1.model.players.SteamPlayersResponse;
import nl.juraji.biliomi.io.api.steam.v1.model.players.SteamProfileVisibilityState;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class SteamGameWatchTimerService extends TimerService {

  @Inject
  private SteamApi steamApi;

  @Inject
  private GameService gameService;

  @Inject
  private ChannelService channelService;

  @Override
  public void start() {
    super.start();

    logger.info("Started watching your Steam account in order to sync Twitch with your currently playing game");
    scheduleAtFixedRate(this::syncToTwitch, 0, 15, TimeUnit.MINUTES);
  }

  public boolean isAvailable() {
    return steamApi.isAvailable();
  }

  public Game syncToTwitchNow() throws Exception {
    Game game = getSteamGameAsGame();

    if (game == null) {
      throw new Exception("You are currently playing a game on Steam that is not known by Biliomi. Try importing your steam library");
    }

    logger.info("Steam game sync forced, updating Twitch with game: " + game.getName());
    channelService.updateGame(game.getName());
    return game;
  }

  private void syncToTwitch() {
    try {
      if (channelService.isStreamOffline()) {
        return;
      }

      Game game = getSteamGameAsGame();
      if (game == null) {
        logger.info("You are currently playing a game on Steam that is not known by Biliomi. Try importing your steam library");
        return;
      }

      String channelGame = channelService.getStream().getGame();
      if (!channelGame.equals(game.getName())) {
        // Only update Twitch if the Steam game differs from Twitch
        logger.info("Steam game changed, updating Twitch: " + channelGame + " -> " + game.getName());
        channelService.updateGame(game.getName());
      }
    } catch (UnavailableException e) {
      logger.error("The service is unavailabe unavailable", e);
    } catch (Exception e) {
      logger.error("An error occurred while fetching Steam user information", e);
    }
  }

  private Game getSteamGameAsGame() throws Exception {
    Response<SteamPlayersResponse> response = steamApi.getPlayerSummary();
    Game game = null;

    if (response.isOK()) {
      if (response.getData().getResponse().getPlayers().size() == 0) {
        throw new UnavailableException("Steam user not found");
      }

      SteamPlayer steamPlayer = response.getData().getResponse().getPlayers().get(0);
      if (SteamProfileVisibilityState.VISIBLE.equals(steamPlayer.getProfileVisibilityState())) {
        if (steamPlayer.getCurrentGameId() != null) {
          game = gameService.getBySteamId(steamPlayer.getCurrentGameId());
        }
      } else {
        throw new UnavailableException("The Steam profile is set to private, Biliomi can't read the nescessary information");
      }
    } else {
      // Something else went wrong
      throw new Exception(response.getRawData());
    }

    return game;
  }
}
