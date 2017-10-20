package nl.juraji.biliomi.components.integrations.spotify.api.v1;

import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.SpotifySnapshot;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.player.SpotifyCurrentTrack;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.playlist.SpotifyPlaylist;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTrack;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTracksSearchResult;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.user.SpotifyUser;
import nl.juraji.biliomi.io.web.Response;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
public interface SpotifyApi {

  /**
   * Check if Spotify integration is set up properly
   *
   * @return True when account is linked, else False
   */
  boolean isAvailable() throws Exception;

  /**
   * Retrieves basic information on the currently linked user
   *
   * @return A Response containing a SpotifyUser object
   * @see <a href="https://developer.spotify.com/web-api/get-current-users-profile">Get Current User’s Profile</a>
   */
  Response<SpotifyUser> getMe() throws Exception;

  /**
   * Retrieves information on the currently played track by the currently linked user
   *
   * @return A Response containing a SpotifyCurrentTrack object
   * @see <a href="https://api.spotify.com/v1/me/player/currently-playing">Get the User’s Currently Playing Track</a>
   */
  Response<SpotifyCurrentTrack> getMeCurrentlyPlaying() throws Exception;

  /**
   * Retrieve a specific playlist from the linked user
   *
   * @param playlistId The Spotify playlist id
   * @return A Response containing the matching SpotifyPlaylist object
   */
  Response<SpotifyPlaylist> getPlaylist(String playlistId) throws Exception;

  /**
   * Add tracks to the playlist identiefied by the playlist id for the currently linked user
   *
   * @param playlistId The id of the playlist to which to add the tracks
   * @param trackIds   The track id's to add
   * @return A Response containing a SpotifySnapshot object
   */
  Response<SpotifySnapshot> addToTracksPlaylist(String playlistId, String... trackIds) throws Exception;

  /**
   * Retrieve a specific track
   *
   * @return A Response containing a SpotifyTrack object
   * @see <a href="https://developer.spotify.com/web-api/get-track/">Get a Track</a>
   */
  Response<SpotifyTrack> getTrack(String trackId) throws Exception;

  /**
   * Search for a track using the given query
   *
   * @param query The query to pass to spotify
   * @param limit The maximum amount of results to return
   * @return A Response containing a SpotifyTracksSearchResult containing a
   * SpotifyTrackPagingObject possibly containing a matched SpotifyTrack object
   */
  Response<SpotifyTracksSearchResult> searchTrack(String query, int limit) throws Exception;
}
