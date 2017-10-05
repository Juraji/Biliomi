package nl.juraji.biliomi.io.api.steam.v1;

import nl.juraji.biliomi.io.api.steam.v1.model.library.SteamLibraryResponse;
import nl.juraji.biliomi.io.api.steam.v1.model.players.SteamPlayersResponse;
import nl.juraji.biliomi.io.web.Response;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
public interface SteamApi {

  /**
   * Check wether the integration is set up proper
   */
  boolean isAvailable();

  /**
   * Get all library items for the authorized Steam user
   *
   * @see <a href="https://developer.valvesoftware.com/wiki/Steam_Web_API#GetOwnedGames_.28v0001.29">GetOwnedGames</a>
   */
  Response<SteamLibraryResponse> getOwnedGames() throws Exception;

  /**
   * Get the player summary for the authorized Steam user
   * Note: The availablility of data is decided by the privacy settings of the authorized Steam user
   *
   * @see <a href="https://developer.valvesoftware.com/wiki/Steam_Web_API#GetPlayerSummaries_.28v0002.29">GetPlayerSummaries</a>
   */
  Response<SteamPlayersResponse> getPlayerSummary() throws Exception;
}
