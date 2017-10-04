package nl.juraji.biliomi.io.api.spotify.oauth;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.api.spotify.oauth.model.SpotifyOAuthAccessToken;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.CallbackServer;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.OAuthFlowDirector;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
public class SpotifyOAuthFlowDirector extends OAuthFlowDirector<SpotifyOAuthScope> {
  private final String consumerSecret;
  private final WebClient webClient;
  private String accessToken;
  private String refreshToken;
  private String authenticationError;
  private int timeToLive;

  public SpotifyOAuthFlowDirector(String baseUri, String consumerKey, String consumerSecret, WebClient webClient) {
    super(baseUri, consumerKey);
    this.consumerSecret = consumerSecret;
    this.webClient = webClient;
  }

  @Override
  public String getAuthenticationUri(SpotifyOAuthScope... scopes) {
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("client_id", getConsumerKey());
    queryMap.put("response_type", "code");
    queryMap.put("redirect_uri", getRedirectUri());
    queryMap.put("state", getStateToken());
    queryMap.put("scope", SpotifyOAuthScope.join(scopes));
    queryMap.put("show_dialog", "true");
    return Url.url(getBaseUri(), "authorize").withQuery(queryMap).toString();
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

  public String getRefreshToken() {
    return refreshToken;
  }

  public long getTimeToLive() {
    return timeToLive * 1000;
  }

  @Override
  public String getAuthenticationError() {
    return authenticationError;
  }

  public boolean awaitRefreshedAccessToken(String refreshToken) {
    Map<String, Object> formData = new HashMap<>();
    formData.put("grant_type", "refresh_token");
    formData.put("refresh_token", refreshToken);

    return requestAccessToken(formData);
  }

  private boolean exchangeCodeForToken(String accessCode) {
    Map<String, Object> formData = new HashMap<>();
    formData.put("grant_type", "authorization_code");
    formData.put("code", accessCode);
    formData.put("redirect_uri", getRedirectUri());

    return requestAccessToken(formData);
  }

  private boolean requestAccessToken(Map<String, Object> requestFormData) {
    Url tokenUri = Url.url(getBaseUri(), "api", "token");

    String clientCode = getConsumerKey() + ':' + consumerSecret;
    String base64ClientCode = Base64.getEncoder().encodeToString(clientCode.getBytes());

    HttpFields headers = new HttpFields();
    headers.put(HttpHeader.AUTHORIZATION, "Basic " + base64ClientCode);
    headers.put(WebClient.NO_CACHE_HEADER, "true");

    String formDataString = Url.createQueryString(requestFormData);

    try {
      Response<SpotifyOAuthAccessToken> response = webClient.post(tokenUri, headers, formDataString, MediaType.FORM_DATA, SpotifyOAuthAccessToken.class);
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
