package nl.juraji.biliomi.io.api.spotify.v1;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.api.spotify.oauth.SpotifyOAuthFlowDirector;
import nl.juraji.biliomi.io.api.spotify.v1.model.SpotifySnapshot;
import nl.juraji.biliomi.io.api.spotify.v1.model.player.SpotifyCurrentTrack;
import nl.juraji.biliomi.io.api.spotify.v1.model.playlist.SpotifyPlaylist;
import nl.juraji.biliomi.io.api.spotify.v1.model.tracks.SpotifyTrack;
import nl.juraji.biliomi.io.api.spotify.v1.model.tracks.SpotifyTrackUriList;
import nl.juraji.biliomi.io.api.spotify.v1.model.tracks.SpotifyTracksSearchResult;
import nl.juraji.biliomi.io.api.spotify.v1.model.user.SpotifyUser;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@Default
@Singleton
public class SpotifyApiImpl implements SpotifyApi {
  private static final String OAUTH_HEADER_PREFIX = "Bearer ";

  @Inject
  @AppDataValue("spotify.api.uris.v1")
  private String apiBaseUri;

  @Inject
  @UserSetting("biliomi.integrations.spotify.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.spotify.consumerSecret")
  private String consumerSecret;

  @Inject
  @UserSetting("biliomi.core.countryCode")
  private String countryCode;

  @Inject
  private WebClient webClient;

  @Inject
  private AuthTokenDao authTokenDao;

  private final HttpFields headers = new HttpFields();

  @PostConstruct
  private void initSpotifyApi() {
    headers.put(HttpHeader.ACCEPT, "application/json");
    headers.put(WebClient.NO_CACHE_HEADER, "true");
  }

  @Override
  public boolean isAvailable() throws Exception {
    return executeTokenPreflight() != null;
  }

  @Override
  public Response<SpotifyUser> getMe() throws Exception {
    executeTokenPreflight();
    return webClient.get(Url.url(apiBaseUri, "me"), headers, SpotifyUser.class);
  }

  @Override
  public Response<SpotifyCurrentTrack> getMeCurrentlyPlaying() throws Exception {
    executeTokenPreflight();
    return webClient.get(Url.url(apiBaseUri, "me", "player", "currently-playing"), headers, SpotifyCurrentTrack.class);
  }

  @Override
  public Response<SpotifyPlaylist> getPlaylist(String playlistId) throws Exception {
    String userId = executeTokenPreflight();
    return webClient.get(Url.url(apiBaseUri, "users", userId, "playlists", playlistId), headers, SpotifyPlaylist.class);
  }

  @Override
  public Response<SpotifySnapshot> addToTracksPlaylist(String playlistId, String... trackIds) throws Exception {
    String userId = executeTokenPreflight();

    SpotifyTrackUriList trackUriList = new SpotifyTrackUriList();
    trackUriList.getUris().addAll(Arrays.asList(trackIds));
    String trackUriListMarshal = JacksonMarshaller.marshal(trackUriList);

    Url url = Url.url(apiBaseUri, "users", userId, "playlists", playlistId, "tracks");
    return webClient.post(url, headers, trackUriListMarshal, MediaType.JSON_UTF_8, SpotifySnapshot.class);
  }

  @Override
  public Response<SpotifyTrack> getTrack(String trackId) throws Exception {
    executeTokenPreflight();

    HashMap<String, Object> query = new HashMap<>();
    query.put("market", countryCode);

    return webClient.get(Url.url(apiBaseUri, "tracks", trackId).withQuery(query), headers, SpotifyTrack.class);
  }

  @Override
  public Response<SpotifyTracksSearchResult> searchTrack(String query, int limit) throws Exception {
    HashMap<String, Object> queryMap = new HashMap<>();
    queryMap.put("query", query);
    queryMap.put("market", countryCode);
    queryMap.put("limit", limit);
    queryMap.put("type", "track");

    String url = Url.url(apiBaseUri, "search").withQuery(queryMap).toString();
    return webClient.get(url, headers, SpotifyTracksSearchResult.class);
  }

  /**
   * Update the persisted access token and the authorization header if necessary
   *
   * @return The id of the currently linked user
   */
  @SuppressWarnings("Duplicates")
  private synchronized String executeTokenPreflight() throws Exception {
    AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "spotify");

    if (StringUtils.isEmpty(token.getToken())) {
      throw new UnavailableException("The Spotify Api is not connected to an account");
    }

    DateTime expiryTime = token.getExpiryTime();
    DateTime now = DateTime.now();
    if (expiryTime != null && now.isAfter(expiryTime)) {
      SpotifyOAuthFlowDirector director = new SpotifyOAuthFlowDirector(consumerKey, consumerSecret, webClient);
      boolean refreshSuccess = director.awaitRefreshedAccessToken(token.getRefreshToken());

      if (refreshSuccess) {
        token.setToken(director.getAccessToken());
        token.setIssuedAt(now);
        token.setTimeToLive(director.getTimeToLive());
        authTokenDao.save(token);
      } else {
        throw new UnavailableException("The Spotify Api failed to refresh the access token");
      }
    } else {
      headers.put(HttpHeader.AUTHORIZATION, OAUTH_HEADER_PREFIX + token.getToken());
    }
    return token.getUserId();
  }
}
