package nl.juraji.biliomi.io.api.patreon.v1;

import nl.juraji.biliomi.io.api.patreon.oauth.PatreonOAuthFlowDirector;
import nl.juraji.biliomi.io.api.patreon.v1.model.responses.PatreonPledgesResponse;
import nl.juraji.biliomi.io.api.patreon.v1.model.responses.PatreonUserResponse;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import nl.juraji.biliomi.utility.factories.ModelUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
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
  @AppDataValue("patreon.api.uris.authorization")
  private String athorizationBaseUri;

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
  public Response<PatreonUserResponse> getUserAndCampaignInfo() throws Exception {
    Url url = Url.url(apiBaseUri, "current_user", "campaigns").withQuery(getIncludeQuery());
    return webClient.get(url, headers, PatreonUserResponse.class);
  }

  @Override
  public Response<PatreonPledgesResponse> getPledges(String campaignId, String nextLink) throws Exception {
    if (nextLink == null) {
      Url url = Url.url(apiBaseUri, "campaigns", campaignId, "pledges").withQuery(getIncludeQuery());
      return webClient.get(url, headers, PatreonPledgesResponse.class);
    } else {
      return webClient.get(nextLink, headers, PatreonPledgesResponse.class);
    }
  }

  private Map<String, Object> getIncludeQuery() {
    return ModelUtils.mapWith("include", "rewards,creator,goals,pledges");
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
      PatreonOAuthFlowDirector director = new PatreonOAuthFlowDirector(athorizationBaseUri, consumerKey, consumerSecret, webClient);
      boolean refreshSuccess = director.awaitRefreshedAccessToken(token.getRefreshToken());

      if (refreshSuccess) {
        token.setToken(director.getAccessToken());
        token.setIssuedAt(now);
        token.setTimeToLive(director.getTimeToLive());
        authTokenDao.save(token);

        headers.put(HttpHeader.AUTHORIZATION, OAUTH_HEADER_PREFIX + token.getToken());
      } else {
        throw new UnavailableException("The Patreon Api failed to refresh the access token");
      }
    }
  }
}
