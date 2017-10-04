package nl.juraji.biliomi.io.api.twitch.oauth;

import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.CallbackServer;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.OAuthFlowDirector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 28-7-2017.
 * Biliomi v3
 */
public class TwitchOAuthFlowDirector extends OAuthFlowDirector<TwitchOAuthScope> {

  private String accessToken;
  private String authenticationError;

  public TwitchOAuthFlowDirector(String baseUri, String consumerKey) {
    super(baseUri, consumerKey);
  }

  @Override
  public String getAuthenticationUri(TwitchOAuthScope... scopes) {
    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("response_type", "token");
    queryMap.put("client_id", getConsumerKey());
    queryMap.put("redirect_uri", getRedirectUri());
    queryMap.put("state", getStateToken());
    queryMap.put("force_verify", "true");
    queryMap.put("scope", TwitchOAuthScope.join(scopes));
    return Url.url(getBaseUri(), "oauth2", "authorize").withQuery(queryMap).toString();
  }

  @Override
  public boolean awaitAccessToken() throws IOException {
    CallbackServer callback = new CallbackServer(getStateToken());
    callback.awaitAuthorization("access_token");
    accessToken = callback.getAccessToken();
    authenticationError = callback.getAuthorizationError();
    return accessToken != null;
  }

  @Override
  public String getAccessToken() {
    return accessToken;
  }

  @Override
  public String getAuthenticationError() {
    return authenticationError;
  }
}
