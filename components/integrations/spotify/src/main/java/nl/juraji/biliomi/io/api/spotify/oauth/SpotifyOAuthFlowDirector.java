package nl.juraji.biliomi.io.api.spotify.oauth;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.CallbackServer;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.OAuthFlowDirector;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.model.OAuthAccessTokenResponse;
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
@SuppressWarnings("Duplicates")
public class SpotifyOAuthFlowDirector extends OAuthFlowDirector<SpotifyOAuthScope> {
  private final String consumerSecret;
  private final WebClient webClient;

  public SpotifyOAuthFlowDirector(String consumerKey, String consumerSecret, WebClient webClient) {
    super(consumerKey);
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
    return Url.url("https://accounts.spotify.com", "authorize").withQuery(queryMap).toString();
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
    Url tokenUri = Url.url("https://accounts.spotify.com", "api", "token");

    String clientCode = getConsumerKey() + ':' + consumerSecret;
    String base64ClientCode = Base64.getEncoder().encodeToString(clientCode.getBytes());

    HttpFields headers = new HttpFields();
    headers.put(HttpHeader.AUTHORIZATION, "Basic " + base64ClientCode);
    headers.put(WebClient.NO_CACHE_HEADER, "true");

    String formDataString = Url.createQueryString(requestFormData);

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
