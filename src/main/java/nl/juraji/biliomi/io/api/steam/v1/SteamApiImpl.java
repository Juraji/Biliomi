package nl.juraji.biliomi.io.api.steam.v1;

import nl.juraji.biliomi.io.api.steam.v1.model.wrappers.SteamLibraryResponse;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;

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

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  @AppDataValue("steam.api.uris.v1")
  private String apiBaseUri;

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
  public Response<SteamLibraryResponse> getLibrary() throws Exception {
    Map<String, Object> query = getDefaultQuery();
    query.put("include_appinfo", 1);

    return webClient.get(Url.url(apiBaseUri, "IPlayerService", "GetOwnedGames", "v0001").withQuery(query), null, SteamLibraryResponse.class);
  }

  private Map<String, Object> getDefaultQuery() throws Exception {
    if (apiKey == null) {
      throw new Exception("Invalid or no Steam API key set");
    }

    if (userId == null) {
      throw new Exception("Invalid or no Steam API key set");
    }

    Map<String, Object> query = new HashMap<>();

    query.put("key", apiKey);
    query.put("steamid", userId);
    query.put("format", "json");

    return query;
  }
}
