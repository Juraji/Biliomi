package nl.juraji.biliomi.io.api.patreon.oauth;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.model.OAuthAccessTokenResponse;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.CallbackServer;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.OAuthFlowDirector;
import org.eclipse.jetty.http.HttpFields;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@SuppressWarnings("Duplicates")
public class PatreonOAuthFlowDirector extends OAuthFlowDirector<PatreonOAuthScope> {
  private final String consumerSecret;
  private final WebClient webClient;
  private String accessToken;
  private String refreshToken;
  private String authenticationError;
  private int timeToLive;

  public PatreonOAuthFlowDirector(String baseUri, String consumerKey, String consumerSecret, WebClient webClient) {
    super(baseUri, consumerKey);
    this.consumerSecret = consumerSecret;
    this.webClient = webClient;
  }

  @Override
  public String getAuthenticationUri(PatreonOAuthScope... scopes) {
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("client_id", getConsumerKey());
    queryMap.put("response_type", "code");
    queryMap.put("redirect_uri", getRedirectUri());
    queryMap.put("state", getStateToken());
    queryMap.put("scope", PatreonOAuthScope.join(scopes));
    return Url.url(getBaseUri(), "oauth2", "authorize").withQuery(queryMap).toString();
  }

  @Override
  public boolean awaitAccessToken() throws IOException {
    CallbackServer callback = new CallbackServer(getStateToken());
    callback.awaitAuthorization("code");
    if (callback.getAccessToken() == null) {
      authenticationError = callback.getAuthorizationError();
      return false;
    }

    return exchangeCodeForToken(callback.getAccessToken());
  }

  @Override
  public String getAccessToken() {
    return accessToken;
  }

  @Override
  public String getAuthenticationError() {
    return authenticationError;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public long getTimeToLive() {
    return timeToLive * 1000;
  }

  public boolean awaitRefreshedAccessToken(String refreshToken) {
    Map<String, Object> formData = new HashMap<>();
    formData.put("grant_type", "refresh_token");
    formData.put("refresh_token", refreshToken);

    return requestAccessToken(formData);
  }

  private boolean exchangeCodeForToken(String accessCode) {
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("grant_type", "authorization_code");
    queryMap.put("code", accessCode);
    queryMap.put("redirect_uri", getRedirectUri());

    return requestAccessToken(queryMap);
  }

  private boolean requestAccessToken(Map<String, Object> queryMap) {
    Url tokenUri = Url.url(getBaseUri(), "oauth2", "token");

    HttpFields headers = new HttpFields();
    headers.put(WebClient.NO_CACHE_HEADER, "true");

    queryMap.put("client_id", getConsumerKey());
    queryMap.put("client_secret", consumerSecret);
    String formDataString = Url.createQueryString(queryMap);

    try {
      Response<OAuthAccessTokenResponse> response = webClient.post(tokenUri, headers, formDataString, MediaType.FORM_DATA, OAuthAccessTokenResponse.class);
      //noinspection Duplicates
      if (response.isOK()) {
        accessToken = response.getData().getAccessToken();
        refreshToken = response.getData().getRefreshToken();
        timeToLive = response.getData().getExpiresIn();
      } else {
        authenticationError = "Failed exchanging authorization code for access token: " + response.getRawData();
      }
    } catch (Exception e) {
      authenticationError = e.getMessage();
    }

    return authenticationError == null;
  }
}
