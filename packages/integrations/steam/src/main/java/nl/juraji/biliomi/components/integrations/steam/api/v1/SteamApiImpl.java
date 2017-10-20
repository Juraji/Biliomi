package nl.juraji.biliomi.components.integrations.steam.api.v1;

import nl.juraji.biliomi.components.integrations.steam.api.v1.model.library.SteamLibraryResponse;
import nl.juraji.biliomi.components.integrations.steam.api.v1.model.players.SteamPlayersResponse;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
@Default
public class SteamApiImpl implements SteamApi {
  private static final String API_BASE_URI = "http://api.steampowered.com";

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  private WebClient webClient;
  private String apiKey = null;
  private String userId = null;

  @PostConstruct
  private void initSteamApiImpl() {
    AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "steam");
    this.apiKey = token.getToken();
    this.userId = token.getUserId();
  }

  @Override
  public boolean isAvailable() {
    return !(apiKey == null || userId == null);
  }

  @Override
  public Response<SteamLibraryResponse> getOwnedGames() throws Exception {
    executeTokenPreflight();
    Map<String, Object> query = new HashMap<>();

    query.put("key", apiKey);
    query.put("steamid", userId);
    query.put("format", "json");
    query.put("include_appinfo", 1);

    return webClient.get(Url.url(API_BASE_URI, "IPlayerService", "GetOwnedGames", "v0001").withQuery(query), null, SteamLibraryResponse.class);
  }

  @Override
  public Response<SteamPlayersResponse> getPlayerSummary() throws Exception {
    executeTokenPreflight();
    Map<String, Object> query = new HashMap<>();

    query.put("key", apiKey);
    query.put("steamids", userId);
    query.put("format", "json");

    return webClient.get(Url.url(API_BASE_URI, "ISteamUser", "GetPlayerSummaries", "v0002").withQuery(query), null, SteamPlayersResponse.class);
  }

  private void executeTokenPreflight() throws UnavailableException {
    if (apiKey == null) {
      throw new UnavailableException("Missing Steam Api key or user id");
    }
  }
}
