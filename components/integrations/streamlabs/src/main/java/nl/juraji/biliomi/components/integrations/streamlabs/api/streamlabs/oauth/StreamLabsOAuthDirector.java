package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth.model.StreamLabsToken;
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
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
public class StreamLabsOAuthDirector extends OAuthFlowDirector<StreamLabsOAuthScope> {
  private final String consumerSecret;
  private final WebClient webClient;

  public StreamLabsOAuthDirector(String consumerKey, String consumerSecret, WebClient webClient) {
    super(consumerKey);
    this.consumerSecret = consumerSecret;
    this.webClient = webClient;
  }

  @Override
  public String getAuthenticationUri(StreamLabsOAuthScope... scopes) {
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("response_type", "code");
    queryMap.put("client_id", getConsumerKey());
    queryMap.put("redirect_uri", getRedirectUri());
    queryMap.put("scope", StreamLabsOAuthScope.join(scopes));
    queryMap.put("state", getStateToken());
    return Url.url("https://streamlabs.com/api/v1.0", "authorize").withQuery(queryMap).toString();
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
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("grant_type", "refresh_token");
    queryMap.put("client_id", getConsumerKey());
    queryMap.put("client_secret", consumerSecret);
    queryMap.put("redirect_uri", getRedirectUri());
    queryMap.put("refresh_token", refreshToken);

    return requestAccessToken(queryMap);
  }

  private boolean exchangeCodeForToken(String accessToken) {
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("grant_type", "authorization_code");
    queryMap.put("client_id", getConsumerKey());
    queryMap.put("client_secret", consumerSecret);
    queryMap.put("redirect_uri", getRedirectUri());
    queryMap.put("code", accessToken);

    return requestAccessToken(queryMap);
  }

  private boolean requestAccessToken(Map<String, Object> queryMap) {
    Url tokenUri = Url.url("https://streamlabs.com/api/v1.0", "token");
    String formData = Url.createQueryString(queryMap);
    HttpFields headers = new HttpFields();
    headers.put(WebClient.NO_CACHE_HEADER, "true");

    try {
      Response<StreamLabsToken> response = webClient.post(tokenUri, headers, formData, MediaType.FORM_DATA, StreamLabsToken.class);
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
