package nl.juraji.biliomi.io.api.streamlabs.v1;

import nl.juraji.biliomi.io.api.streamlabs.oauth.StreamLabsOAuthDirector;
import nl.juraji.biliomi.io.api.streamlabs.v1.model.StreamLabsSocketToken;
import nl.juraji.biliomi.io.api.streamlabs.v1.model.StreamLabsTwitchUser;
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
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class StreamLabsApiImpl implements StreamLabsApi {

  @Inject
  @AppDataValue("streamlabs.api.uris.v1")
  private String apiBaseUri;

  @Inject
  @UserSetting("biliomi.integrations.streamLabs.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.streamLabs.consumerSecret")
  private String consumerSecret;

  @Inject
  private WebClient webClient;

  @Inject
  private AuthTokenDao authTokenDao;

  private final HttpFields headers = new HttpFields();

  @PostConstruct
  private void initStreamLabsApi() {
    headers.put(HttpHeader.ACCEPT, "application/json");
    headers.put(WebClient.NO_CACHE_HEADER, "true");
  }

  @Override
  public Response<StreamLabsTwitchUser> getMe() throws Exception {
    Map<String, Object> queryMap = executeTokenPreflight();
    return webClient.get(Url.url(apiBaseUri, "user").withQuery(queryMap), headers, StreamLabsTwitchUser.class);
  }

  @Override
  public Response<StreamLabsSocketToken> getSocketToken() throws Exception {
    Map<String, Object> queryMap = executeTokenPreflight();
    return webClient.get(Url.url(apiBaseUri, "socket", "token").withQuery(queryMap), headers, StreamLabsSocketToken.class);
  }

  /**
   * Update the persisted access token and the authorization header if necessary
   *
   * @return The current access token
   */
  @SuppressWarnings("Duplicates")
  private synchronized Map<String, Object> executeTokenPreflight() throws Exception {
    AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "streamlabs");

    if (StringUtils.isEmpty(token.getToken())) {
      throw new UnavailableException("The Stream Labs is not connected to an account");
    }

    DateTime expiryTime = token.getExpiryTime();
    DateTime now = DateTime.now();
    if (expiryTime != null && now.isAfter(expiryTime)) {
      StreamLabsOAuthDirector director = new StreamLabsOAuthDirector(apiBaseUri, consumerKey, consumerSecret, webClient);
      boolean refreshSuccess = director.awaitRefreshedAccessToken(token.getRefreshToken());

      if (refreshSuccess) {
        token.setToken(director.getAccessToken());
        token.setRefreshToken(director.getRefreshToken());
        token.setTimeToLive(director.getTimeToLive());
        token.setIssuedAt(now);
        authTokenDao.save(token);
      } else {
        throw new UnavailableException("The Stream Labs Api failed to refresh the access token: " + director.getAuthenticationError());
      }
    }

    return ModelUtils.mapWith("access_token", token.getToken());
  }
}
