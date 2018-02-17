package nl.juraji.biliomi.io.api.twitch.oauth;

import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.CallbackServer;
import nl.juraji.biliomi.io.web.oauthflow.grants.code.OAuthFlowDirector;

import java.io.IOException;

/**
 * Created by Juraji on 28-7-2017.
 * Biliomi v3
 */
public class TwitchOAuthFlowDirector extends OAuthFlowDirector<TwitchOAuthScope> {

  public TwitchOAuthFlowDirector(String consumerKey) {
    super(consumerKey);
  }

  @Override
  public String getAuthenticationUri(TwitchOAuthScope... scopes) {
    return Url.url("https://api.twitch.tv/kraken", "oauth2", "authorize")
        .withQueryParam("response_type", "token")
        .withQueryParam("client_id", getConsumerKey())
        .withQueryParam("redirect_uri", getRedirectUri())
        .withQueryParam("state", getStateToken())
        .withQueryParam("force_verify", "true")
        .withQueryParam("scope", TwitchOAuthScope.join(scopes))
        .toString();
  }

  @Override
  public boolean awaitAccessToken() throws IOException {
    CallbackServer callback = createCallbackServer();
    callback.awaitAuthorization("access_token");
    accessToken = callback.getAccessToken();
    authenticationError = callback.getAuthorizationError();
    return accessToken != null;
  }
}
