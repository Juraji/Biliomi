package nl.juraji.biliomi.io.api.patreon.v1;

import nl.juraji.biliomi.io.api.patreon.oauth.PatreonOAuthFlowDirector;
import nl.juraji.biliomi.io.api.patreon.v1.model.PatreonCampaigns;
import nl.juraji.biliomi.io.api.patreon.v1.model.PatreonPledges;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class PatreonApiImpl implements PatreonApi {
  private static final String OAUTH_HEADER_PREFIX = "Bearer ";

  @Inject
  @AppDataValue("patreon.api.uris.v1")
  private String apiBaseUri;

  @Inject
  @UserSetting("biliomi.integrations.patreon.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.patreon.consumerSecret")
  private String consumerSecret;

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
    try {
      executeTokenPreflight();
      return true;
    } catch (UnavailableException e) {
      // Catch the unavailable exception and just return false
      return false;
    }
  }

  @Override
  public Response<PatreonCampaigns> getUserAndCampaignInfo() throws Exception {
    executeTokenPreflight();
    Url url = Url.url(apiBaseUri, "current_user", "campaigns");
    return webClient.get(url, headers, PatreonCampaigns.class);
  }

  @Override
  public Response<PatreonPledges> getPledges(String campaignId) throws Exception {
    executeTokenPreflight();
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("page[count]", 10);
    queryMap.put("sort", "created");

    Url url = Url.url(apiBaseUri, "campaigns", campaignId, "pledges").withQuery(queryMap);
    return webClient.get(url, headers, PatreonPledges.class);
  }

  @Override
  public Response<PatreonPledges> getPledges(PatreonPledges previousResponse) throws Exception {
    executeTokenPreflight();
    if (previousResponse == null || previousResponse.getLinks().getNextPageLink() == null) {
      throw new IllegalArgumentException("PatreonPledges is null or does not have a next link");
    }
    return webClient.get(previousResponse.getLinks().getNextPageLink(), headers, PatreonPledges.class);
  }

  @SuppressWarnings("Duplicates")
  private void executeTokenPreflight() throws UnavailableException {
    AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "patreon");

    if (StringUtils.isEmpty(token.getToken())) {
      throw new UnavailableException("The Patreon Api is not connected to an account");
    }

    DateTime expiryTime = token.getExpiryTime();
    DateTime now = DateTime.now();
    if (expiryTime != null && now.isAfter(expiryTime)) {
      PatreonOAuthFlowDirector director = new PatreonOAuthFlowDirector(consumerKey, consumerSecret, webClient);
      boolean refreshSuccess = director.awaitRefreshedAccessToken(token.getRefreshToken());

      if (refreshSuccess) {
        token.setToken(director.getAccessToken());
        token.setIssuedAt(now);
        token.setTimeToLive(director.getTimeToLive());
        authTokenDao.save(token);
      } else {
        throw new UnavailableException("The Patreon Api failed to refresh the access token");
      }
    } else {
      headers.put(HttpHeader.AUTHORIZATION, OAUTH_HEADER_PREFIX + token.getToken());
    }
  }
}
